package io.github.engagelab.bean.device;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DeviceSetParam {

    @JsonProperty("tags")
    private Tags tags;

    @JsonProperty("alias")
    private String alias;

    @Data
    public static class Tags {
        @JsonProperty("add")
        private List<String> add;

        @JsonProperty("remove")
        private List<String> remove;
    }

}
