package com.ssafy.home.config;

import com.ssafy.home.member.AuthController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        HttpSession session = request.getSession(false);
        Long memberId = session == null ? null : (Long) session.getAttribute(AuthController.LOGIN_MEMBER_ID);

        if (memberId != null) {
            log.info("AUTH_PASS method={} uri={} memberId={} sessionId={}",
                    request.getMethod(), request.getRequestURI(), memberId, session.getId());
            return true;
        }

        log.warn("AUTH_REQUIRED method={} uri={} query={} remoteAddr={} userAgent={}",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent"));

        if (request.getRequestURI().startsWith("/api/")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write("""
                    {"success":false,"message":"로그인이 필요한 기능입니다.","code":"LOGIN_REQUIRED","status":401}
                    """);
            return false;
        }

        String redirect = request.getRequestURI();
        if (request.getQueryString() != null && !request.getQueryString().isBlank()) {
            redirect += "?" + request.getQueryString();
        }
        response.sendRedirect("/members?redirect=" + URLEncoder.encode(redirect, StandardCharsets.UTF_8));
        return false;
    }
}
