package com.ssafy.home.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final RequestLoggingInterceptor requestLoggingInterceptor;
    private final LoginCheckInterceptor loginCheckInterceptor;

    @Bean
    RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**", "/images/**", "/favicon.ico");

        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns(
                        "/favorites",
                        "/admin",
                        "/api/admin/**",
                        "/api/favorites/**",
                        "/api/logs/**",
                        "/api/deals/fetch",
                        "/api/deals/fetch-all"
                );
    }
}
