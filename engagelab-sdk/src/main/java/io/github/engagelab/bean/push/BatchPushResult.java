package io.github.engagelab.bean.push;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchPushResult {

    private Map<String, SinglePushResult> results;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SinglePushResult {
        private String target;
        private boolean success;
        private long msg_id;
    }
}

