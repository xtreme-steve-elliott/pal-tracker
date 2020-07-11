package test.pivotal.pal.shutdown;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestOperations;
import test.pivotal.pal.shutdown.support.*;
import test.pivotal.pal.shutdown.support.runtime.Platform;
import test.pivotal.pal.shutdown.support.userrequest.UserRequest;
import test.pivotal.pal.shutdown.support.userrequest.UserRequestExecutor;
import test.pivotal.pal.shutdown.support.userrequest.UserResponse;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Config.class)
public class TestForceTerminate {
    private final Logger logger = LoggerFactory.getLogger(TestForceTerminate.class);

    @Autowired
    public RestOperations restOperations;

    @Autowired
    public Platform platform;

    @Autowired
    public UserRequestExecutor userRequestExecutor;

    @Disabled
    @Test
    public void testForcibleTermination() throws IOException, InterruptedException, ExecutionException {
        /*
          Start pal-tracker with default shutdown configuration.
         */
        logger.info("pal-tracker with default shutdown settings.");
        platform.spawnNewPalTrackerProcess();

        // Verify `pal-tracker` process supports shutdown handling
        logger.info("Verify `pal-tracker` process supports shutdown handling.");
        assertThat(platform.doesPalTrackerProcessSupportsShutdownHandling());

        /*
            Execute a long running request immediately before the shutdown request -
            it will fail on a socket exception.
        */
        logger.info("execute long running concurrent request immediately before the shutdown request.");
        Future<UserResponse> firstResponse =
                userRequestExecutor.submitRequest(
                        UserRequest
                                .baseUrl(platform.getPalTrackerUrl())
                                .requestName("User1beforeShutdown")
                                .build());

        /*
            sleep a bit to give OS scheduler a chance to give the first
            request some love.
         */
        sleep(1000L);

        // execute pal-tracker shutdown request
        logger.info("Tell the platform to terminate pal-tracker process");
        platform.forciblyTerminatePalTracker();

        sleep(1000L);

        /*
          Execute a request AFTER the shutdown interrupt -
          it will fail on a socket connection error.
         */
        logger.info("Execute long running request concurrently after shutdown request.");
        Future<UserResponse> secondResponse =
                userRequestExecutor.submitRequest(
                        UserRequest
                                .baseUrl(platform.getPalTrackerUrl())
                                .requestName("User3afterShutdown")
                                .build());


         /*
            sleep a bit to give OS scheduler a chance to give the second
            request some love.
         */
        sleep(500L);

        /*
          Wait for server to terminate before proceeding with assertion.
         */
        logger.info("Wait for the platform to terminate pal-tracker...");
        platform.waitUntilPalTrackerProcessIsTerminated();
        logger.info("pal-tracker process is shutdown.");

        sleep(1000);

        /*
          Assertions show what the end state should be.
          Beware timing is dependent where you run the tests.

          If you have assertion failures,
          check the log output timings of the test logs,
          as well as the pal-tracker spring boot console logs
          to see if the shutdown behavior is as expected.
         */
        assertThat(platform.isPalTrackerProcessUp()).isFalse();
        assertThat(firstResponse.isDone());
        assertThat(firstResponse.get().connectException).isTrue();
        assertThat(secondResponse.isDone());
        assertThat(secondResponse.get().connectException).isTrue();
    }

}
