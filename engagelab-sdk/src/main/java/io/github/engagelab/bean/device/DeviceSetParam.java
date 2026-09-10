package io.github.engagelab.bean.device;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
public class DeviceSetParam {

    @JsonProperty("tags")
    @Setter(AccessLevel.NONE)
    private Object tags;

    @JsonProperty("alias")
    private String alias;

    public void setTags(Tags tags) {
        this.tags = tags;
    }

    public void clearTags() {
        this.tags = "";
    }

    @Data
    public static class Tags {
        @JsonProperty("add")
        private List<String> add;

        @JsonProperty("remove")
        private List<String> remove;
    }

}
