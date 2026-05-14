package com.ssafy.home.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    ResponseEntity<ErrorResponse> badRequest(Exception e, HttpServletRequest request) {
        String message = e instanceof MethodArgumentTypeMismatchException ex
                ? "요청 파라미터 형식이 올바르지 않습니다: " + ex.getName()
                : e.getMessage();
        log.warn("Bad request [{} {}] {}", request.getMethod(), request.getRequestURI(), message);
        return error(HttpStatus.BAD_REQUEST, "BAD_REQUEST", message, request);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    ResponseEntity<ErrorResponse> validation(Exception e, HttpServletRequest request) {
        String message;
        if (e instanceof MethodArgumentNotValidException ex) {
            message = ex.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
        } else {
            message = e.getMessage();
        }
        log.warn("Validation failed [{} {}] {}", request.getMethod(), request.getRequestURI(), message);
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, request);
    }

    @ExceptionHandler(SecurityException.class)
    ResponseEntity<ErrorResponse> unauthorized(SecurityException e, HttpServletRequest request) {
        log.warn("Unauthorized [{} {}] {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return error(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", e.getMessage(), request);
    }

    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<ErrorResponse> notFound(NoSuchElementException e, HttpServletRequest request) {
        log.warn("Not found [{} {}] {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return error(HttpStatus.NOT_FOUND, "NOT_FOUND", e.getMessage(), request);
    }

    @ExceptionHandler(RestClientResponseException.class)
    ResponseEntity<ErrorResponse> externalApiResponse(RestClientResponseException e, HttpServletRequest request) {
        String message = "외부 API 호출 실패: " + e.getStatusCode() + " " + e.getResponseBodyAsString();
        log.error("External API response error [{} {}] {}", request.getMethod(), request.getRequestURI(), message, e);
        return error(HttpStatus.BAD_GATEWAY, "EXTERNAL_API_ERROR", message, request);
    }

    @ExceptionHandler({RestClientException.class, ExternalApiException.class})
    ResponseEntity<ErrorResponse> externalApi(Exception e, HttpServletRequest request) {
        String message = "외부 API 호출 중 오류가 발생했습니다.";
        log.error("External API error [{} {}] {}", request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return error(HttpStatus.BAD_GATEWAY, "EXTERNAL_API_ERROR", message, request);
    }

    @ExceptionHandler(DataAccessException.class)
    ResponseEntity<ErrorResponse> database(DataAccessException e, HttpServletRequest request) {
        log.error("Database error [{} {}] {}", request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "DATABASE_ERROR", "데이터 처리 중 오류가 발생했습니다.", request);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> unknown(Exception e, HttpServletRequest request) {
        log.error("Unhandled error [{} {}] {}", request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "서버 오류가 발생했습니다.", request);
    }

    private static ResponseEntity<ErrorResponse> error(HttpStatus status, String code, String message, HttpServletRequest request) {
        return ResponseEntity.status(status)
                .body(ErrorResponse.of(message, code, status.value(), request.getRequestURI()));
    }
}
