package io.github.engagelab.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import io.github.engagelab.bean.voice.VoiceResult;
import io.github.engagelab.bean.voice.VoiceUploadParam;

import java.util.List;

public interface VoiceClient {
    @RequestLine("POST /v4/voices")
    @Headers("Content-Type: multipart/form-data")
    VoiceResult create(VoiceUploadParam param);

    @RequestLine("GET /v4/voices")
    List<VoiceResult> list();

    @RequestLine("GET /v4/voices/{language}")
    VoiceResult get(@Param("language") String language);

    @RequestLine("DELETE /v4/voices/{language}")
    void delete(@Param("language") String language);
}
