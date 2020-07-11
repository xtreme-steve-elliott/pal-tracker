package test.pivotal.pal.shutdown.support.userrequest;

public class UserRequest {
    private final String baseUrl;
    private final String requestName;

    public UserRequest(String baseUrl, String requestName) {
        this.baseUrl = baseUrl;
        this.requestName = requestName;
    }

    public static RequestNameParam baseUrl(String baseUrl) {
        Builder builder = new Builder();
        builder.baseUrl(baseUrl);
        return builder;
    }

    public interface RequestNameParam {
        Builder requestName(String requestName);
    }

    public static class Builder implements RequestNameParam {
        private String baseUrl;
        private String requestName;

        Builder(){}

        public RequestNameParam baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public UserRequest build() {
            return new UserRequest(baseUrl,requestName);
        }

        @Override
        public Builder requestName(String requestName) {
            this.requestName = requestName;
            return this;
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getRequestName() {
        return requestName;
    }

    public String getUrl() {
        return baseUrl + "/shutdownDemo/" + requestName;
    }
}
