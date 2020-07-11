package test.pivotal.pal.shutdown.support;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import test.pivotal.pal.shutdown.support.runtime.PalTrackerProcessMonitor;
import test.pivotal.pal.shutdown.support.runtime.Platform;
import test.pivotal.pal.shutdown.support.userrequest.UserRequestExecutor;

@Configuration
public class Config {

    @ConditionalOnProperty(name = "serverplatform", havingValue = "WINDOWS")
    @Bean
    public PalTrackerProcessMonitor.ServerPlatform winServerPlatform() {
        return PalTrackerProcessMonitor.ServerPlatform.WIN;
    }

    @ConditionalOnProperty(name = "serverplatform", matchIfMissing = true)
    @Bean
    public PalTrackerProcessMonitor.ServerPlatform unixServerPlatform() {
        return PalTrackerProcessMonitor.ServerPlatform.NIX;
    }

    @Bean
    public RestOperations restOperations() {
        return new RestTemplate();
    }

    @Bean
    public UserRequestExecutor userRequestExecutor() {
        return new UserRequestExecutor(restOperations());
    }

    @Bean
    public Platform runtimePlatform() {
        return new Platform(unixServerPlatform(),restOperations());
    }
}
