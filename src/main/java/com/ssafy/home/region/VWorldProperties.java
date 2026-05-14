package com.ssafy.home.region;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.vworld")
public record VWorldProperties(String key) {
}
