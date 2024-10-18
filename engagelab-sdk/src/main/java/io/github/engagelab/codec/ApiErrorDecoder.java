package io.github.engagelab.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import io.github.engagelab.exception.ApiErrorException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        try {
            Response.Body body = response.body();
            String bodyContent = Util.toString(body.asReader());
            log.info("bodyContent:{}", bodyContent);
            ApiErrorException.ApiError apiError = new ObjectMapper().readValue(bodyContent, ApiErrorException.ApiError.class);
            return new ApiErrorException(status, apiError);
        } catch (Exception exception) {
            log.error("decode error", exception);
            ApiErrorException.ApiError.Error error = new ApiErrorException.ApiError.Error();
            error.setCode(500);
            error.setMessage(exception.getMessage());
            ApiErrorException.ApiError apiError = new ApiErrorException.ApiError();
            apiError.setError(error);
            return new ApiErrorException(status, apiError);
        }
    }

}
