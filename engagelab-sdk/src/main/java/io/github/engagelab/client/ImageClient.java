package io.github.engagelab.client;

import feign.Headers;
import feign.RequestLine;
import io.github.engagelab.bean.image.ImageParam;
import io.github.engagelab.bean.image.ImageResult;

/**
 * <a href="https://www.engagelab.com/zh_CN/docs/app-push/developer-guide/rest-api/image-api">REST API - Image</a>
 */
public interface ImageClient {

    /**
     * 上传 OPPO 图片（大图 / 小图标）
     *
     * @param param 图片 URL 参数
     * @return 图片标识结果
     */
    @RequestLine("POST /v4/image/oppo")
    @Headers("Content-Type: application/json; charset=utf-8")
    ImageResult uploadOppoImage(ImageParam param);
}
