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

    /**
     * 第三方通道配置，key 为通道名称，value 为通道参数对象。
     * <p>使用 Map 以保持灵活性，便于在不发版的情况下适配 API 字段变更。</p>
     *
     * <p>OPPO 通道示例：</p>
     * <pre>{@code
     * Map<String, Object> oppo = new HashMap<>();
     * oppo.put("big_picture_id",  "上传图片 API 返回的大图标识");  // 大图，style=3 时必填
     * oppo.put("small_picture_id", "上传图片 API 返回的小图标识"); // 小图标
     * oppo.put("channel_id",       "通知栏分类");
     * oppo.put("notify_level",     1); // 1=通知栏 2=通知栏+锁屏 16=全部
     *
     * Map<String, Object> thirdPartyChannel = new HashMap<>();
     * thirdPartyChannel.put("oppo", oppo);
     * options.setThirdPartyChannel(thirdPartyChannel);
     * }</pre>
     */
    @JsonProperty("third_party_channel")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> thirdPartyChannel;

    @JsonProperty("classification")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer classification;

    @JsonProperty("voice_value")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String voiceValue;

    @JsonProperty("enhanc_message")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean enhanc_message;

    /**
     * 可选
     * 推送计划标识
     * 需先创建计划标识值,可在控制台创建或者通过API创建。
     */
    @JsonProperty("plan_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String planId;

    /**
     * 可选
     * 推送请求标识，用于防止重复推送
     * 只允许字母、数字、下划线和减号，长度不超过64个字符。注意相同AppKey下这个字段必须保持唯一性。使用详情可参考如何避免重复推送
     */
    @JsonProperty("cid")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cid;

    @JsonProperty("auto_truncation")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean autoTruncation;
}
