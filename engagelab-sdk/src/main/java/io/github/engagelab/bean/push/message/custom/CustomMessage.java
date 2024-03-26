package io.github.engagelab.bean.push.message.custom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomMessage {

    @JsonProperty("title")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String title;

    @JsonProperty("msg_content")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String content;

    @JsonProperty("content_type")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String contentType;

    @JsonProperty("extras")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Map<String, Object> extras;

}
