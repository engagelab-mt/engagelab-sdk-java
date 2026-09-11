package io.github.engagelab.bean.push;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchPushResult {

    private Map<String, SinglePushResult> results;

    @JsonProperty("rate_limit_info")
    private RateLimitInfo rateLimitInfo;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SinglePushResult {
        private String target;
        private boolean success;
        @JsonProperty("msg_id")
        private Long msgId;

        private Error error;

        /**
         * @deprecated use {@code getMsgId()} instead.
         */
        @Deprecated
        @JsonIgnore
        public long getMsg_id() {
            return msgId == null ? 0L : msgId;
        }

        /**
         * @deprecated use {@code setMsgId(Long)} instead.
         */
        @Deprecated
        @JsonIgnore
        public void setMsg_id(long msgId) {
            this.msgId = msgId;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Error {
        private Integer code;
        private String message;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RateLimitInfo {
        private String message;

        @JsonProperty("rate_limit_occurred")
        private Boolean rateLimitOccurred;
    }
}
