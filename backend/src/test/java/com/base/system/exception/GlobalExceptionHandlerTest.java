package com.base.system.exception;

import com.base.common.result.Result;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 全局异常处理器测试
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void missingRequestParameterShouldReturnParamError() {
        MissingServletRequestParameterException exception =
                new MissingServletRequestParameterException("date", "LocalDate");

        Result<Void> result = globalExceptionHandler.handleMissingServletRequestParameterException(exception);

        assertEquals(400, result.getCode());
        assertEquals("缺少请求参数：date", result.getMessage());
    }

    @Test
    void invalidRequestParameterFormatShouldReturnParamError() {
        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                "invalid", LocalDate.class, "date", null, new IllegalArgumentException("invalid date"));

        Result<Void> result = globalExceptionHandler.handleMethodArgumentTypeMismatchException(exception);

        assertEquals(400, result.getCode());
        assertEquals("请求参数date格式错误", result.getMessage());
    }
}
