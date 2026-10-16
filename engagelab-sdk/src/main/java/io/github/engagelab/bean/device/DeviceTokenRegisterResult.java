package io.github.engagelab.bean.device;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DeviceTokenRegisterResult {
    @JsonProperty("results")
    private List<TokenResult> results;

    @Data
    public static class TokenResult {
        @JsonProperty("token")
        private String token;

        @JsonProperty("registration_id")
        private String registrationId;

        @JsonProperty("is_new")
        private Boolean isNew;

        @JsonProperty("code")
        private Integer code;

        @JsonProperty("message")
        private String message;
    }
}
