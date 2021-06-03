package com.pnxtest.syncloud;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(
        builder = LogMsg.Builder.class
)
public class LogMsg {
    @JsonProperty("Msg")
    private final String msg;
    @JsonProperty("data")
    private final String data;
//    @JsonProperty("Ex")
//    private final StackifyError ex;
    @JsonProperty("Th")
    private final String th;
    @JsonProperty("EpochMs")
    private final Long epochMs;
    @JsonProperty("Level")
    private final String level;
    @JsonProperty("TransID")
    private final String transId;
    @JsonProperty("SrcMethod")
    private final String srcMethod;
    @JsonProperty("SrcLine")
    private final Integer srcLine;
    @JsonProperty("id")
    private final String id;
    @JsonProperty("Tags")
    private final List<String> tags;

    public String getMsg() {
        return this.msg;
    }

    public String getData() {
        return this.data;
    }

//    public StackifyError getEx() {
//        return this.ex;
//    }

    public String getTh() {
        return this.th;
    }

    public Long getEpochMs() {
        return this.epochMs;
    }

    public String getLevel() {
        return this.level;
    }

    public String getTransId() {
        return this.transId;
    }

    public String getSrcMethod() {
        return this.srcMethod;
    }

    public Integer getSrcLine() {
        return this.srcLine;
    }

    public String getId() {
        return this.id;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public Builder toBuilder() {
        return newBuilder().msg(this.msg).data(this.data)
                //.ex(this.ex)
                .th(this.th)
                .epochMs(this.epochMs)
                .level(this.level)
                .transId(this.transId)
                .srcMethod(this.srcMethod)
                .srcLine(this.srcLine).tags(this.tags);
    }

    private LogMsg(Builder builder) {
        this.msg = builder.msg;
        this.data = builder.data;
        //this.ex = builder.ex;
        this.th = builder.th;
        this.epochMs = builder.epochMs;
        this.level = builder.level;
        this.transId = builder.transId;
        this.srcMethod = builder.srcMethod;
        this.srcLine = builder.srcLine;
        this.id = UUID.randomUUID().toString();
        this.tags = builder.tags;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @JsonIgnoreProperties(
            ignoreUnknown = true
    )
    public static class Builder {
        @JsonProperty("Msg")
        private String msg;
        @JsonProperty("data")
        private String data;
//        @JsonProperty("Ex")
//        private StackifyError ex;
        @JsonProperty("Th")
        private String th;
        @JsonProperty("EpochMs")
        private Long epochMs;
        @JsonProperty("Level")
        private String level;
        @JsonProperty("TransID")
        private String transId;
        @JsonProperty("SrcMethod")
        private String srcMethod;
        @JsonProperty("SrcLine")
        private Integer srcLine;
        @JsonProperty("Tags")
        private List<String> tags;

        public Builder() {
        }

        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder data(String data) {
            this.data = data;
            return this;
        }

//        public LogMsg.Builder ex(StackifyError ex) {
//            this.ex = ex;
//            return this;
//        }

        public Builder th(String th) {
            this.th = th;
            return this;
        }

        public Builder epochMs(Long epochMs) {
            this.epochMs = epochMs;
            return this;
        }

        public Builder level(String level) {
            this.level = level;
            return this;
        }

        public Builder transId(String transId) {
            this.transId = transId;
            return this;
        }

        public Builder srcMethod(String srcMethod) {
            this.srcMethod = srcMethod;
            return this;
        }

        public Builder srcLine(Integer srcLine) {
            this.srcLine = srcLine;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public LogMsg build() {
            return new LogMsg(this);
        }
    }
}