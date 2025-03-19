package io.github.engagelab.bean.device;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceStatusGetResult {

    @JsonProperty("regid")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String registrationId;

    @JsonProperty("online")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean online;

    @JsonProperty("last_online_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime lastOnlineTime;
}
