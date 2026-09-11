package io.github.engagelab.bean.app;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AppVipStatusResult {
    @JsonProperty("vip_status")
    private Integer vipStatus;

    @JsonProperty("vip_end_time")
    private Long vipEndTime;
}
