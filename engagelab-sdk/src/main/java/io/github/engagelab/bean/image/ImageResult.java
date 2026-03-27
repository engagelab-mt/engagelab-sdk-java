package io.github.engagelab.bean.image;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 图片上传响应结果
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageResult {

    /**
     * 大图片标识，可用于推送 API 中 options.third_party_channel.oppo.big_picture_id
     */
    @JsonProperty("big_picture_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String bigPictureId;

    /**
     * 小图标标识，可用于推送 API 中 options.third_party_channel.oppo.small_picture_id
     */
    @JsonProperty("small_picture_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String smallPictureId;
}
