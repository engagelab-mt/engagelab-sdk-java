package io.github.engagelab.bean.image;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 图片上传请求参数
 * <a href="https://www.engagelab.com/zh_CN/docs/app-push/developer-guide/rest-api/image-api">REST API - Image</a>
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageParam {

    /**
     * 大图片 URL（可选）
     * OPPO 规格：984×369 px，最大 1MB，支持 PNG/JPG/JPEG
     */
    @JsonProperty("big_picture_url")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String bigPictureUrl;

    /**
     * 小图标 URL（可选）
     * OPPO 规格：144×144 px，最大 50KB，支持 PNG/JPG/JPEG
     */
    @JsonProperty("small_picture_url")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String smallPictureUrl;
}
