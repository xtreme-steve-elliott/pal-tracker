package io.pivotal.pal.tracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static java.lang.Thread.sleep;

@Service
public class BackingService {
    private final Logger logger = LoggerFactory.getLogger(BackingService.class);
    private final BackingServiceFailure failure;
    private boolean warmed = false;

    public BackingService(BackingServiceFailure failure) {
        this.failure = failure;
    }

    public boolean ping() {
        if (!warmed) {
            // Simulate backing service lazy initialization
            initialize();
            warmed = true;
        }
        return !failure.isSimulateFailure();
    }

    public void initialize() {
        logger.info("Initializing Backing Service...");

        // Simulate backing service initialization
        try {
            sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("Backing Service is initialized, ready to service requests...");
    }

    public void doSomething() {
        // Simulate doing something
        logger.info("Backing Service is doing something, maybe.");
    }
}
