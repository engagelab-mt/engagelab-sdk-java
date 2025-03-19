package io.github.engagelab.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import io.github.engagelab.bean.device.*;
import io.github.engagelab.enums.Platform;

import java.util.List;

/**
 * (<a href="https://www.engagelab.com/docs/app-push/rest-api/Tag-Alias-API">REST API - Device</a>)
 */
public interface DeviceClient {

    @RequestLine("POST /v4/devices/status")
    @Headers("Content-Type: application/json; charset=utf-8")
    List<DeviceStatusGetResult> getDeviceStatus(DeviceStatusGetParam param);

    @RequestLine("GET /v4/devices/{registration_id}")
    @Headers("Content-Type: application/json; charset=utf-8")
    DeviceGetResult getDevice(@Param("registration_id") String registrationId);

    @RequestLine("POST /v4/devices/{registration_id}")
    @Headers("Content-Type: application/json; charset=utf-8")
    void setDevice(@Param("registration_id") String registrationId, DeviceSetParam param);

    @RequestLine("GET /v4/tags")
    @Headers("Content-Type: application/json; charset=utf-8")
    TagsGetResult getTags();

    @RequestLine("POST /v4/tags/{tag}")
    @Headers("Content-Type: application/json; charset=utf-8")
    void setTag(@Param("tag") String tag, TagSetParam param);

    @RequestLine("DELETE /v4/tags/{tag}?platform={platforms}")
    @Headers("Content-Type: application/json; charset=utf-8")
    void deleteTag(@Param("tag") String tag, @Param("platforms") List<Platform> platforms);

    @RequestLine("GET /v4/tags_count?tags={tags}&platform={platforms}")
    @Headers("Content-Type: application/json; charset=utf-8")
    TagsCountGetResult getTagCount(@Param("tags") List<String> tags, @Param("platforms") List<Platform> platforms);

    @RequestLine("GET /v4/tags/{tag}/registration_ids/{registration_id}")
    @Headers("Content-Type: application/json; charset=utf-8")
    TagsGetResult getTagStatus(@Param("tag") String tag, @Param("registration_id") String registrationId);

    @RequestLine("GET /v4/tags/quota-info?tags={tags}&platform={platforms}")
    @Headers("Content-Type: application/json; charset=utf-8")
    TagQuotaGetResult getTagQuota(@Param("tags") List<String> tags, @Param("platforms") List<Platform> platforms);

    @RequestLine("GET /v4/aliases/{alias}?platform={platforms}")
    @Headers("Content-Type: application/json; charset=utf-8")
    AliasStatusGetResult getAliasStatus(@Param("alias") String alias, @Param("platforms") List<Platform> platforms);

    @RequestLine("DELETE /v4/aliases/{alias}?platform={platforms}")
    @Headers("Content-Type: application/json; charset=utf-8")
    void deleteAlas(@Param("alias") String alias, @Param("platforms") List<Platform> platforms);
}
