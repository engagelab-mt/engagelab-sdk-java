package io.github.engagelab.bean.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DeviceTokenRegisterParam {
    @JsonProperty("platform")
    private String platform;

    @JsonProperty("tokens")
    private List<String> tokens;

    @JsonProperty("apns_production")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean apnsProduction;
}
