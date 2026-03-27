package io.github.engagelab.api;

import feign.Client;
import feign.Feign;
import feign.Logger;
import feign.auth.BasicAuthRequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import io.github.engagelab.bean.image.ImageParam;
import io.github.engagelab.bean.image.ImageResult;
import io.github.engagelab.client.ImageClient;
import io.github.engagelab.codec.ApiErrorDecoder;
import lombok.NonNull;

/**
 * 图片上传 API
 * <a href="https://www.engagelab.com/zh_CN/docs/app-push/developer-guide/rest-api/image-api">REST API - Image</a>
 */
public class ImageApi {

    private final ImageClient imageClient;

    protected ImageApi(@NonNull ImageClient imageClient) {
        this.imageClient = imageClient;
    }

    /**
     * 上传 OPPO 图片（大图 / 小图标）
     * <p>返回的 big_picture_id / small_picture_id 可填入
     * {@code options.third_party_channel.oppo} 中使用。</p>
     *
     * @param param 图片 URL 参数
     * @return 图片标识结果
     */
    public ImageResult uploadOppoImage(@NonNull ImageParam param) {
        return imageClient.uploadOppoImage(param);
    }

    public static class Builder {

        private String host;
        private Client client = new OkHttpClient();
        private String appKey;
        private String masterSecret;
        private Logger.Level loggerLevel = Logger.Level.BASIC;

        public Builder setHost(@NonNull String host) {
            this.host = host;
            return this;
        }

        public Builder setClient(@NonNull Client client) {
            this.client = client;
            return this;
        }

        public Builder setAppKey(@NonNull String appKey) {
            this.appKey = appKey;
            return this;
        }

        public Builder setMasterSecret(@NonNull String masterSecret) {
            this.masterSecret = masterSecret;
            return this;
        }

        public Builder setLoggerLevel(@NonNull Logger.Level loggerLevel) {
            this.loggerLevel = loggerLevel;
            return this;
        }

        public ImageApi build() {
            ImageClient imageClient = Feign.builder()
                    .client(client)
                    .requestInterceptor(new BasicAuthRequestInterceptor(appKey, masterSecret))
                    .encoder(new JacksonEncoder())
                    .decoder(new JacksonDecoder())
                    .errorDecoder(new ApiErrorDecoder())
                    .logger(new Slf4jLogger())
                    .logLevel(loggerLevel)
                    .target(ImageClient.class, host);
            return new ImageApi(imageClient);
        }
    }
}
