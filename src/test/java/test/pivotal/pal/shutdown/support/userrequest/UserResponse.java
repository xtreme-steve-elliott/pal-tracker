package test.pivotal.pal.shutdown.support.userrequest;

import java.time.Duration;
import java.util.Objects;

public class UserResponse {
    public final int status;
    public final Duration latency;
    public final boolean connectException;

    public UserResponse(int status, Duration latency, boolean connectException) {
        this.status = status;
        this.latency = latency;
        this.connectException = connectException;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserResponse command = (UserResponse) o;
        return status == command.status &&
                connectException == command.connectException &&
                Objects.equals(latency, command.latency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, latency, connectException);
    }

    @Override
    public String toString() {
        return "HttpCommand{" +
                "status=" + status +
                ", latency=" + latency +
                ", connectException=" + connectException +
                '}';
    }
}
