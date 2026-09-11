package io.github.engagelab.client;

import feign.Headers;
import feign.RequestLine;
import io.github.engagelab.bean.app.AppVipStatusResult;

public interface AppClient {
    @RequestLine("GET /v4/app/vip/status")
    @Headers("Content-Type: application/json; charset=utf-8")
    AppVipStatusResult getVipStatus();
}
