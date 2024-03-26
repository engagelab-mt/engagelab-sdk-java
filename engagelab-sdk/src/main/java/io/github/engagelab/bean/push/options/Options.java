package io.github.engagelab.bean.push.options;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Options {

    @JsonProperty("time_to_live")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer timeToLive;

    @JsonProperty("override_msg_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long overrideMsgId;

    @JsonProperty("apns_production")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean apnsProduction;

    @JsonProperty("apns_collapse_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String apnsCollapseId;

    @JsonProperty("big_push_duration")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer bigPushDuration;

    @JsonProperty("multi_language")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> multiLanguage;

    @JsonProperty("third_party_channel")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> thirdPartyChannel;

}
