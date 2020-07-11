package test.pivotal.pal.shutdown.support.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

import java.io.IOException;
import java.net.Socket;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.assertj.core.api.Assertions.fail;
import static test.pivotal.pal.shutdown.support.util.MapBuilder.envMapBuilder;

public class PalTrackerProcessMonitor {
    private final Logger logger = LoggerFactory.getLogger(PalTrackerProcessMonitor.class);
    private final RestOperations restOperations;
    private final String jarPath;
    private final String port;
    private final ServerPlatform serverPlatform;
    private final String actuatorEndpoint;

    public enum ServerPlatform {
        NIX {
            @Override
            public void forceTermination(Long pid) {
                runCmd("kill -9 " + pid);
            }
        },
        WIN {
            @Override
            public void forceTermination(Long pid) {
                ServerPlatform.runCmd("Stop-Process -Force -Id " + pid);
            }
        };

        public abstract void forceTermination(Long pid);

        private static void runCmd(String cmd) {
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Process serverProcess;

    public PalTrackerProcessMonitor(RestOperations restOperations, String jarPath, String port, ServerPlatform serverPlatform) {
        this.restOperations = restOperations;
        this.jarPath = jarPath;
        this.port = port;
        this.serverPlatform = serverPlatform;
        this.actuatorEndpoint = "http://localhost:" + port + "/actuator/health";
    }

    public void start() throws IOException, InterruptedException {
        start(null);
    }

    public void start(Map<String, String> moreEnvVars) throws IOException, InterruptedException {
        if (canConnect()) throw new RuntimeException("Process already bound to port "
                + port + ". You must terminate before running the demo.");

        ProcessBuilder processBuilder = new ProcessBuilder()
                .command("java", "-jar", jarPath)
                .inheritIO();

        Map<String,String> envVars = envMapBuilder()
                .put("SERVER_PORT", port.toString())
                .put("WELCOME_MESSAGE", "hello from Graceful Shutdown Test")
                .put("MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE", "*")
                .put("MANAGEMENT_ENDPOINT_HEALTH_SHOWDETAILS", "always")
                .put("LOGGING_FILE_NAME", "./build/reports/log/pal-tracker.log")
                .build();

        if (moreEnvVars != null)
            envVars.putAll(moreEnvVars);

        processBuilder
                .environment()
                .putAll(envVars);

        serverProcess = processBuilder.start();

        waitUntilServerIsUp(port);
    }

    public String url() {
        return "http://localhost:" + port;
    }

    public String getRequestUrl(String path) {
        return url() + "/shutdownDemo/" + path;
    }

    public boolean processUp() {
        return serverProcess.isAlive();
    }

    public boolean doesProcessSupportsShutdownHandling() {
        return serverProcess.supportsNormalTermination();
    }

    public void requestShutdown() {
        serverProcess.destroy();
    }

    public void forciblyTerminate() {
        serverPlatform.forceTermination(serverProcess.pid());
    }

    public void waitUntilServerIsUp(String port) throws InterruptedException {
        int timeout = 30;
        Instant start = Instant.now();
        boolean isUp = false;

        logger.debug("Waiting on port {}...", port);

        while (!isUp) {
            try {
                if (restOperations.getForEntity(actuatorEndpoint,String.class).getStatusCode().is2xxSuccessful()) {
                    isUp = true;
                    logger.debug(" server is up.");
                } else {
                    throw new RuntimeException("Actuator does report healthy");
                }
            } catch (Throwable e) {
                if (serverProcess.isAlive()) {
                    long timeSpent = ChronoUnit.SECONDS.between(start, Instant.now());
                    if (timeSpent > timeout) {
                        logger.error("Timed out waiting for server on port {}" + port);
                        fail("Server Startup Failed");
                    }
                    Thread.sleep(200);
                } else {
                    logger.error("Server did not start.");
                    logger.error("Make sure to build the boot jar via `./gradlew bootJar` before running the shutdown tests.");
                    fail("Server did not start.  Make sure to build the boot jar via `./gradlew bootJar` before running the shutdown tests");
                }
            }
        }
    }

    public boolean canConnect() {
        try {
            new Socket("localhost",
                    Integer.parseInt(port))
                    .close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
