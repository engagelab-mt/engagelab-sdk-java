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
        String bodyContent;
        try {
            Response.Body body = response.body();
            bodyContent = Util.toString(body.asReader());
            log.info("bodyContent:{}", bodyContent);
        } catch (Exception exception) {
            return handleException(exception, status);
        }
        try {
            ApiErrorException.ApiError.Error error = new ObjectMapper().readValue(bodyContent, ApiErrorException.ApiError.Error.class);
            if (error.getCode() == null && error.getMessage() == null) {
                return handleBodyParsingException(bodyContent, status);
            }
            ApiErrorException.ApiError apiError = new ApiErrorException.ApiError();
            apiError.setError(error);
            return new ApiErrorException(status, apiError);
        } catch (Exception e) {
            return handleBodyParsingException(bodyContent, status);
        }
    }

    private Exception handleBodyParsingException(String bodyContent, int status) {
        try {
            log.info("handleBodyParsingException bodyContent:{}", bodyContent);
            ApiErrorException.ApiError apiError = new ObjectMapper().readValue(bodyContent, ApiErrorException.ApiError.class);
            return new ApiErrorException(status, apiError);
        } catch (Exception ex) {
            return handleException(ex, status);
        }
    }

    private Exception handleException(Exception exception, int status) {
        log.error("decode error", exception);
        ApiErrorException.ApiError.Error error = new ApiErrorException.ApiError.Error();
        error.setCode(500);
        error.setMessage(exception.getMessage());
        ApiErrorException.ApiError apiError = new ApiErrorException.ApiError();
        apiError.setError(error);
        return new ApiErrorException(status, apiError);
    }
}
