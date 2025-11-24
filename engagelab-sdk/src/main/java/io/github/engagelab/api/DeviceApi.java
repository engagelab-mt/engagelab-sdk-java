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
import io.github.engagelab.bean.device.*;
import io.github.engagelab.client.DeviceClient;
import io.github.engagelab.codec.ApiErrorDecoder;
import io.github.engagelab.enums.Platform;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class DeviceApi {

    private final DeviceClient deviceClient;

    protected DeviceApi(@NonNull DeviceClient deviceClient) {
        this.deviceClient = deviceClient;
    }

    public List<DeviceStatusGetResult> getDeviceStatus(DeviceStatusGetParam param) {
        return deviceClient.getDeviceStatus(param);
    }

    public DeviceGetResult getDevice(String registrationId) {
        return deviceClient.getDevice(registrationId);
    }

    public void setDevice(String registrationId, DeviceSetParam param) {
        deviceClient.setDevice(registrationId, param);
    }

    public TagsGetResult getTags() {
        return deviceClient.getTags();
    }

    public void setTag(String tag, TagSetParam param) {
        deviceClient.setTag(tag, param);
    }

    public void deleteTag(String tag, List<Platform> platforms) {
        deviceClient.deleteTag(tag, platforms.stream()
                .map(Platform::name)
                .collect(Collectors.joining(",")));
    }

    public TagsCountGetResult getTagCount(List<String> tags, List<Platform> platforms) {
        return deviceClient.getTagCount(String.join(",", tags), platforms.stream()
                .map(Platform::name)
                .collect(Collectors.joining(",")));
    }

    public TagsGetResult getTagStatus(String tag, String registrationId) {
        return deviceClient.getTagStatus(tag, registrationId);
    }

    public TagQuotaGetResult getTagQuota(List<String> tags, List<Platform> platforms) {
        return deviceClient.getTagQuota(String.join(",", tags), platforms.stream()
                .map(Platform::name)
                .collect(Collectors.joining(",")));
    }

    public AliasStatusGetResult getAliasStatus(String alias, List<Platform> platforms) {
        return deviceClient.getAliasStatus(alias, platforms.stream()
                .map(Platform::name)
                .collect(Collectors.joining(",")));
    }

    public void deleteAlas(String alias, List<Platform> platforms) {
        deviceClient.deleteAlas(alias, platforms.stream()
                .map(Platform::name)
                .collect(Collectors.joining(",")));
    }

    /**
     * 删除用户
     * 注意：
     * - 删除操作是异步执行的
     * - 将删除用户的所有相关数据，包括绑定的标签、别名、设备信息和时区信息
     * - 一旦删除用户，就无法将其恢复
     * - 不支持批量删除
     * 
     * @param registrationId 设备在engagelab系统的唯一标识
     */
    public void deleteDevice(@NonNull String registrationId) {
        deviceClient.deleteDevice(registrationId);
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

        public DeviceApi build() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            DeviceClient deviceClient = Feign.builder()
                    .client(client)
                    .requestInterceptor(new BasicAuthRequestInterceptor(appKey, masterSecret))
                    .encoder(new JacksonEncoder(objectMapper))
                    .decoder(new JacksonDecoder(objectMapper))
                    .errorDecoder(new ApiErrorDecoder())
                    .logger(new Slf4jLogger())
                    .logLevel(loggerLevel)
                    .target(DeviceClient.class, host);
            return new DeviceApi(deviceClient);
        }
    }

}
