package io.github.engagelab.bean.device;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class TagsCountGetResult {

    @JsonProperty("tagsCount")
    private Map<String, Long> tagsCount;

}
