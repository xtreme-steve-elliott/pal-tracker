package io.pivotal.pal.tracker.shutdown;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.lang.Thread.sleep;

@RestController
@RequestMapping("/shutdownDemo")
public class ShutdownDemoController {
    private final long userOneLatency;
    private final long userTwoLatency;
    private final long userThreeLatency;

    public ShutdownDemoController(@Value("${user1.latency: 5000}") long userOneLatency,
                                  @Value("${user2.latency: 15000}") long userTwoLatency,
                                  @Value("${user3.latency: 500}") long userThreeLatency) {
        this.userOneLatency = userOneLatency;
        this.userTwoLatency = userTwoLatency;
        this.userThreeLatency = userThreeLatency;
    }

    @GetMapping("/User1beforeShutdown")
    public String userRequestOneBeforeShutdown() throws InterruptedException {
        sleep(userOneLatency);
        return "Hello, User One!";
    }

    @GetMapping("/User2beforeShutdown")
    public String userRequestTwoBeforeShutdown() throws InterruptedException {
        sleep(userTwoLatency);
        return "Hello, User Two!";
    }

    @GetMapping("/User3afterShutdown")
    public String userRequestThreeAfterShutdown() throws InterruptedException {
        sleep(userThreeLatency);
        return "Hello, User Three!";
    }
}
