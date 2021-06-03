package com.pnxtest.syncloud;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(
        builder = LogMsgGroup.Builder.class
)
public class LogMsgGroup {
    @JsonProperty("CDID")
    private final Integer cdId;
    @JsonProperty("CDAppID")
    private final Integer cdAppId;
    @JsonProperty("AppNameID")
    private final String appNameId;
    @JsonProperty("AppEnvID")
    private final String appEnvId;
    @JsonProperty("EnvID")
    private final Integer envId;
    @JsonProperty("Env")
    private final String env;
    @JsonProperty("ServerName")
    private final String serverName;
    @JsonProperty("AppName")
    private final String appName;
    @JsonProperty("AppLoc")
    private final String appLoc;
    @JsonProperty("Logger")
    private final String logger;
    @JsonProperty("Platform")
    private final String platform;
    @JsonProperty("Msgs")
    private final List<LogMsg> msgs;

    public Integer getCdId() {
        return this.cdId;
    }

    public Integer getCdAppId() {
        return this.cdAppId;
    }

    public String getAppNameId() {
        return this.appNameId;
    }

    public String getAppEnvId() {
        return this.appEnvId;
    }

    public Integer getEnvId() {
        return this.envId;
    }

    public String getEnv() {
        return this.env;
    }

    public String getServerName() {
        return this.serverName;
    }

    public String getAppName() {
        return this.appName;
    }

    public String getAppLoc() {
        return this.appLoc;
    }

    public String getLogger() {
        return this.logger;
    }

    public String getPlatform() {
        return this.platform;
    }

    public List<LogMsg> getMsgs() {
        return this.msgs;
    }

    public Builder toBuilder() {
        return newBuilder().cdId(this.cdId).cdAppId(this.cdAppId).appNameId(this.appNameId).appEnvId(this.appEnvId).envId(this.envId).env(this.env).serverName(this.serverName).appName(this.appName).appLoc(this.appLoc).logger(this.logger).platform(this.platform).msgs(this.msgs);
    }

    private LogMsgGroup(Builder builder) {
        this.cdId = builder.cdId;
        this.cdAppId = builder.cdAppId;
        this.appNameId = builder.appNameId;
        this.appEnvId = builder.appEnvId;
        this.envId = builder.envId;
        this.env = builder.env;
        this.serverName = builder.serverName;
        this.appName = builder.appName;
        this.appLoc = builder.appLoc;
        this.logger = builder.logger;
        this.platform = builder.platform;
        this.msgs = builder.msgs;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        @JsonProperty("CDID")
        private Integer cdId;
        @JsonProperty("CDAppID")
        private Integer cdAppId;
        @JsonProperty("AppNameID")
        private String appNameId;
        @JsonProperty("AppEnvID")
        private String appEnvId;
        @JsonProperty("EnvID")
        private Integer envId;
        @JsonProperty("Env")
        private String env;
        @JsonProperty("ServerName")
        private String serverName;
        @JsonProperty("AppName")
        private String appName;
        @JsonProperty("AppLoc")
        private String appLoc;
        @JsonProperty("Logger")
        private String logger;
        @JsonProperty("Platform")
        private String platform;
        @JsonProperty("Msgs")
        private List<LogMsg> msgs;

        public Builder() {
        }

        public Builder cdId(Integer cdId) {
            this.cdId = cdId;
            return this;
        }

        public Builder cdAppId(Integer cdAppId) {
            this.cdAppId = cdAppId;
            return this;
        }

        public Builder appNameId(String appNameId) {
            this.appNameId = appNameId;
            return this;
        }

        public Builder appEnvId(String appEnvId) {
            this.appEnvId = appEnvId;
            return this;
        }

        public Builder envId(Integer envId) {
            this.envId = envId;
            return this;
        }

        public Builder env(String env) {
            this.env = env;
            return this;
        }

        public Builder serverName(String serverName) {
            this.serverName = serverName;
            return this;
        }

        public Builder appName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder appLoc(String appLoc) {
            this.appLoc = appLoc;
            return this;
        }

        public Builder logger(String logger) {
            this.logger = logger;
            return this;
        }

        public Builder platform(String platform) {
            this.platform = platform;
            return this;
        }

        public Builder msgs(List<LogMsg> msgs) {
            this.msgs = msgs;
            return this;
        }

        public LogMsgGroup build() {
            return new LogMsgGroup(this);
        }
    }
}
