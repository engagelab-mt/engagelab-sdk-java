package io.github.engagelab.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Client;
import feign.Feign;
import feign.Logger;
import feign.auth.BasicAuthRequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import io.github.engagelab.bean.schedule.*;
import io.github.engagelab.client.ScheduleClient;
import io.github.engagelab.codec.ApiErrorDecoder;
import lombok.NonNull;

import java.util.List;

public class ScheduleApi {

    private final ScheduleClient scheduleClient;

    protected ScheduleApi(@NonNull ScheduleClient scheduleClient) {
        this.scheduleClient = scheduleClient;
    }

    public SchedulePushResult schedulePush(SchedulePushParam param) {
        return scheduleClient.schedulePush(param);
    }

    public SchedulePushUpdateResult updateSchedulePush(SchedulePushUpdateParam param) {
        return scheduleClient.updateSchedulePush(param.getScheduleId(), param);
    }

    public void deleteSchedulePush(String scheduleId) {
        scheduleClient.deleteSchedulePush(scheduleId);
    }

    public List<SchedulePushGetResult> getSchedulePush(String scheduleId) {
        return scheduleClient.getSchedulePush(scheduleId);
    }

    public SchedulePushListResult listSchedulePush(SchedulePushListParam param) {
        return scheduleClient.listSchedulePush(param.getPage());
    }

    public SchedulePushDetailGetResult getSchedulePushDetail(SchedulePushDetailGetParam param) {
        return scheduleClient.getSchedulePushDetail(param.getScheduleId());
    }

    // ********************* 如果遇到此api没有及时补充字段的情况，可以自行构建json，调用下面的接口 *********************

    public SchedulePushResult schedulePush(Object param) {
        return scheduleClient.schedulePush(param);
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

        public ScheduleApi build() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            ScheduleClient scheduleClient = Feign.builder()
                    .client(client)
                    .requestInterceptor(new BasicAuthRequestInterceptor(appKey, masterSecret))
                    .encoder(new JacksonEncoder(objectMapper))
                    .decoder(new JacksonDecoder(objectMapper))
                    .errorDecoder(new ApiErrorDecoder())
                    .logger(new Slf4jLogger())
                    .logLevel(loggerLevel)
                    .target(ScheduleClient.class, host);
            return new ScheduleApi(scheduleClient);
        }
    }

}
