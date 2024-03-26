package io.github.engagelab.client;

import feign.Headers;
import feign.RequestLine;
import io.github.engagelab.bean.push.PushParam;
import io.github.engagelab.bean.push.PushResult;

/**
 * (<a href="https://www.engagelab.com/docs/app-push/rest-api/create-push-api">REST API - Push</a>)
 */
public interface PushClient {

    @RequestLine("POST /v4/push")
    @Headers("Content-Type: application/json; charset=utf-8")
    PushResult push(PushParam param);

}
