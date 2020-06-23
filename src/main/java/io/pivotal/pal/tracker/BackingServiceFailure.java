package io.pivotal.pal.tracker;

import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "backingServiceFailure")
public class BackingServiceFailure {
    private boolean simulateFailure;

    @WriteOperation
    public void set() {
        simulateFailure = true;
    }

    @DeleteOperation
    public void unset() {
        simulateFailure = false;
    }

    @ReadOperation
    public boolean isSimulateFailure() {
        return simulateFailure;
    }
}
