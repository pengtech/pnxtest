package com.pnxtest.syncloud;

public class ApiConfiguration {
    private static final String DEFAULT_API_URL = "https://api.stackify.com";
    private final String apiUrl;
    private final String apiKey;
    private final String application;
    private final String environment;
    //private final EnvironmentDetail envDetail;

    public String getApiUrl() {
        return this.apiUrl != null ? this.apiUrl : "https://api.stackify.com";
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public String getApplication() {
        return this.application;
    }

    public String getEnvironment() {
        return this.environment;
    }

//    public EnvironmentDetail getEnvDetail() {
//        return this.envDetail;
//    }

    private ApiConfiguration(Builder builder) {
        this.apiUrl = builder.apiUrl;
        this.apiKey = builder.apiKey;
        this.application = builder.application;
        this.environment = builder.environment;
        //this.envDetail = builder.envDetail;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return newBuilder()
                .apiUrl(this.apiUrl)
                .apiKey(this.apiKey)
                .application(this.application)
                .environment(this.environment);
                //.envDetail(this.envDetail);
    }

    public String toString() {
        return "ApiConfiguration [apiUrl=" + this.apiUrl + ", apiKey=" + this.apiKey + ", application=" + this.application + ", environment=" + this.environment + "]";
    }

    public static class Builder {
        private String apiUrl;
        private String apiKey;
        private String application;
        private String environment;
        //private EnvironmentDetail envDetail;

        public Builder() {
        }

        public Builder apiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder application(String application) {
            this.application = application;
            return this;
        }

        public Builder environment(String environment) {
            this.environment = environment;
            return this;
        }

//        public ApiConfiguration.Builder envDetail(EnvironmentDetail envDetail) {
//            this.envDetail = envDetail;
//            return this;
//        }

        public ApiConfiguration build() {
            return new ApiConfiguration(this);
        }
    }
}
