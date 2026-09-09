package io.github.engagelab.api;

import feign.Client;
import feign.Feign;
import feign.Logger;
import feign.auth.BasicAuthRequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import io.github.engagelab.bean.voice.VoiceResult;
import io.github.engagelab.bean.voice.VoiceUploadParam;
import io.github.engagelab.client.VoiceClient;
import io.github.engagelab.codec.ApiErrorDecoder;
import io.github.engagelab.codec.VoiceMultipartEncoder;
import lombok.NonNull;

import java.io.File;
import java.util.List;

public class VoiceApi {
    private final VoiceClient voiceClient;

    protected VoiceApi(@NonNull VoiceClient voiceClient) {
        this.voiceClient = voiceClient;
    }

    public VoiceResult create(@NonNull String language, @NonNull File file) {
        return voiceClient.create(new VoiceUploadParam(language, file));
    }

    public List<VoiceResult> list() {
        return voiceClient.list();
    }

    public VoiceResult get(@NonNull String language) {
        return voiceClient.get(language);
    }

    public void delete(@NonNull String language) {
        voiceClient.delete(language);
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

        public VoiceApi build() {
            VoiceClient voiceClient = Feign.builder()
                    .client(client)
                    .requestInterceptor(new BasicAuthRequestInterceptor(appKey, masterSecret))
                    .encoder(new VoiceMultipartEncoder(new JacksonEncoder()))
                    .decoder(new JacksonDecoder())
                    .errorDecoder(new ApiErrorDecoder())
                    .logger(new Slf4jLogger())
                    .logLevel(loggerLevel)
                    .target(VoiceClient.class, host);
            return new VoiceApi(voiceClient);
        }
    }
}
