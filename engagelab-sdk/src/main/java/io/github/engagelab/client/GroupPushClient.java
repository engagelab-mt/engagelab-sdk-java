package io.github.engagelab.client;

import feign.Headers;
import feign.RequestLine;
import io.github.engagelab.bean.push.GroupPushParam;
import io.github.engagelab.bean.push.GroupPushResult;

/**
 * (<a href="https://www.engagelab.com/zh_CN/docs/app-push/rest-api/group-push-api">REST API - Group Push</a>)
 */
public interface GroupPushClient {

    @RequestLine("POST v4/grouppush")
    @Headers("Content-Type: application/json; charset=utf-8")
    GroupPushResult push(GroupPushParam param);

}
