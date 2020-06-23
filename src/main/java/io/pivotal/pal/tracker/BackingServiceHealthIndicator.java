package io.pivotal.pal.tracker;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class BackingServiceHealthIndicator implements HealthIndicator {
    private final BackingService backingService;

    public BackingServiceHealthIndicator(BackingService backingService) {
        this.backingService = backingService;
    }

    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder();

        if (backingService.ping()){
            builder.up();
        } else {
            builder.down();
        }

        return builder.build();
    }
}
