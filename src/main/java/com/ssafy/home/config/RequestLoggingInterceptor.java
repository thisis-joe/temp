package com.ssafy.home.config;

import com.ssafy.home.member.AuthController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.UUID;

@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {
    private static final String START_TIME = RequestLoggingInterceptor.class.getName() + ".START_TIME";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME, startTime);
        MDC.put("requestId", requestId);

        HttpSession session = request.getSession(false);
        Object memberId = session == null ? null : session.getAttribute(AuthController.LOGIN_MEMBER_ID);
        log.info("REQUEST_START method={} uri={} query={} remoteAddr={} sessionId={} memberId={} userAgent={}",
                request.getMethod(),
                request.getRequestURI(),
                safeQuery(request.getQueryString()),
                request.getRemoteAddr(),
                session == null ? "none" : session.getId(),
                memberId == null ? "anonymous" : memberId,
                request.getHeader("User-Agent"));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long startTime = (long) request.getAttribute(START_TIME);
        long durationMs = System.currentTimeMillis() - startTime;
        Object pattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String handlerName = handler == null ? "unknown" : handler.toString();

        if (ex == null) {
            log.info("REQUEST_END method={} uri={} pattern={} status={} durationMs={} handler={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    pattern,
                    response.getStatus(),
                    durationMs,
                    handlerName);
        } else {
            log.error("REQUEST_ERROR method={} uri={} pattern={} status={} durationMs={} handler={} exceptionType={} exceptionMessage={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    pattern,
                    response.getStatus(),
                    durationMs,
                    handlerName,
                    ex.getClass().getName(),
                    ex.getMessage(),
                    ex);
        }
        MDC.clear();
    }

    private static String safeQuery(String queryString) {
        if (queryString == null || queryString.isBlank()) {
            return "";
        }
        return queryString.replaceAll("(?i)(serviceKey|password|consumer_secret|key)=([^&]*)", "$1=***");
    }
}
