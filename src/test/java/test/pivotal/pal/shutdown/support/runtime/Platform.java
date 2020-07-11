package test.pivotal.pal.shutdown.support.runtime;

import org.springframework.web.client.RestOperations;

import java.io.IOException;
import java.util.Map;

import static java.lang.Thread.sleep;

public class Platform {
    private final PalTrackerProcessMonitor palTrackerProcessMonitor;

    public Platform(PalTrackerProcessMonitor.ServerPlatform platform,
                    RestOperations restOperations) {
        this.palTrackerProcessMonitor = new PalTrackerProcessMonitor( restOperations,
                System.getProperty("user.dir") + "/build/libs/pal-tracker.jar",
                "8880",
                platform);
    }

    public void spawnNewPalTrackerProcess() throws IOException, InterruptedException {
        palTrackerProcessMonitor.start();
    }

    public void spawnNewPalTrackerProcess(Map<String,String> envVars) throws IOException, InterruptedException {
        palTrackerProcessMonitor.start(envVars);
    }

    public boolean doesPalTrackerProcessSupportsShutdownHandling() {
        return palTrackerProcessMonitor.doesProcessSupportsShutdownHandling();
    }

    public String getPalTrackerUrl() {
        return palTrackerProcessMonitor.url();
    }

    public void interruptPalTrackerForShutdown() {
        palTrackerProcessMonitor.requestShutdown();
    }

    public void forciblyTerminatePalTracker() {
        palTrackerProcessMonitor.forciblyTerminate();
    }

    public void waitUntilPalTrackerProcessIsTerminated() throws InterruptedException {
        while (palTrackerProcessMonitor.processUp()) sleep(500L);
    }

    public boolean isPalTrackerProcessUp() {
        return palTrackerProcessMonitor.processUp();
    }
}
