package io.github.engagelab.api;

import feign.Feign;
import feign.Logger;
import feign.auth.BasicAuthRequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import io.github.engagelab.bean.push.PushParam;
import io.github.engagelab.bean.push.PushResult;
import io.github.engagelab.client.PushClient;
import io.github.engagelab.codec.ApiErrorDecoder;
import lombok.NonNull;

import java.net.Proxy;

public class PushApi {

    private final PushClient pushClient;

    protected PushApi(@NonNull PushClient pushClient) {
        this.pushClient = pushClient;
    }

    public PushResult push(@NonNull PushParam param) {
        return pushClient.push(param);
    }

    public static class Builder {

        private String host = "https://push.api.engagelab.cc";
        private Proxy proxy;
        private String appKey;
        private String masterSecret;
        private Logger.Level loggerLevel = Logger.Level.BASIC;

        public Builder setHost(@NonNull String host) {
            this.host = host;
            return this;
        }

        public Builder setProxy(@NonNull Proxy proxy) {
            this.proxy = proxy;
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

        public PushApi build() {
            okhttp3.OkHttpClient.Builder delegateBuilder = new okhttp3.OkHttpClient().newBuilder();
            if (proxy != null) {
                delegateBuilder.proxy(proxy);
            }
            PushClient pushClient = Feign.builder()
                    .client(new OkHttpClient(delegateBuilder.build()))
                    .requestInterceptor(new BasicAuthRequestInterceptor(appKey, masterSecret))
                    .encoder(new JacksonEncoder())
                    .decoder(new JacksonDecoder())
                    .errorDecoder(new ApiErrorDecoder())
                    .logger(new Slf4jLogger())
                    .logLevel(loggerLevel)
                    .target(PushClient.class, host);
            return new PushApi(pushClient);
        }
    }

}
