package io.github.engagelab.client;

import feign.Headers;
import feign.RequestLine;
import io.github.engagelab.bean.push.BatchPushParam;
import io.github.engagelab.bean.push.BatchPushResult;
import io.github.engagelab.bean.push.PushParam;
import io.github.engagelab.bean.push.PushResult;

/**
 * (<a href="https://www.engagelab.com/docs/app-push/rest-api/create-push-api">REST API - Push</a>)
 */
public interface PushClient {

    @RequestLine("POST /v4/push")
    @Headers("Content-Type: application/json; charset=utf-8")
    PushResult push(PushParam param);

    @RequestLine("POST /v4/batch/push/regid")
    @Headers("Content-Type: application/json; charset=utf-8")
    BatchPushResult batchPushByRegId(BatchPushParam param);

    @RequestLine("POST /v4/batch/push/alias")
    @Headers("Content-Type: application/json; charset=utf-8")
    BatchPushResult batchPushByAlias(BatchPushParam param);

    // ********************* 如果遇到此api没有及时补充字段的情况，可以自行构建json，调用下面的接口 *********************

    @RequestLine("POST /v4/push")
    @Headers("Content-Type: application/json; charset=utf-8")
    PushResult push(Object param);
}
