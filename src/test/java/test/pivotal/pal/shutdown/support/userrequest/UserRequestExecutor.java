package test.pivotal.pal.shutdown.support.userrequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UserRequestExecutor {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final RestOperations restOperations;

    public UserRequestExecutor(RestOperations restOperations) {
        this.restOperations = restOperations;
    }

    public Future<UserResponse> submitRequest(UserRequest userRequest) {
        assert(userRequest != null);
        final Logger logger = LoggerFactory.getLogger(getLoggerName(userRequest));

        return executor.submit(() -> {
            logger.info("Started executing request {} at {}",
                    userRequest.getRequestName(),
                    userRequest.getUrl());

            Temporal start = LocalTime.now();
            UserResponse command;

            try {
                command = new UserResponse(
                        restOperations.getForEntity(userRequest.getUrl(), String.class).getStatusCodeValue(),
                        Duration.between(start, LocalTime.now()),
                        false);
            } catch (Exception e) {
                command = new UserResponse(-1,
                        Duration.between(start, LocalTime.now()),
                        true);
            }

            logger.info("Finished executing request {}", command);
            return command;

        });
    }

    private static String getLoggerName(UserRequest userRequest) {
        return "io.pivotal.pal.shutdown." + userRequest.getRequestName();
    }
}
