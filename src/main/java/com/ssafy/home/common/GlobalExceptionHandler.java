package com.ssafy.home.common;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiResponse<Void> badRequest(IllegalArgumentException e) {
        return new ApiResponse<>(false, e.getMessage(), null);
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiResponse<Void> notFound(NoSuchElementException e) {
        return new ApiResponse<>(false, e.getMessage(), null);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiResponse<Void> validation(Exception e) {
        if (e instanceof MethodArgumentNotValidException ex) {
            String message = ex.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return new ApiResponse<>(false, message, null);
        }
        return new ApiResponse<>(false, e.getMessage(), null);
    }

    @ExceptionHandler(SecurityException.class)
    ApiResponse<Void> unauthorized(SecurityException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return new ApiResponse<>(false, e.getMessage(), null);
    }

    @ExceptionHandler(RestClientResponseException.class)
    ApiResponse<Void> externalApiError(RestClientResponseException e, HttpServletResponse response) {
        // 외부 공공데이터 API가 인증/승인/트래픽 문제를 반환해도 사용자는 JSON 형태로 원인을 확인할 수 있다.
        response.setStatus(HttpStatus.BAD_GATEWAY.value());
        return new ApiResponse<>(false, "외부 API 호출 실패: " + e.getStatusCode() + " " + e.getResponseBodyAsString(), null);
    }
}
