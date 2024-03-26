package io.github.engagelab.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import io.github.engagelab.exception.ApiErrorException;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class ApiErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        try {
            Response.Body body = response.body();
            String bodyContent = Util.toString(body.asReader(StandardCharsets.UTF_8));
            log.info("bodyContent:{}", bodyContent);
            ApiErrorException.ApiError apiError = new ObjectMapper().readValue(bodyContent, ApiErrorException.ApiError.class);
            return new ApiErrorException(status, apiError);
        } catch (Exception exception) {
            return new ApiErrorException(status, null);
        }
    }

}
