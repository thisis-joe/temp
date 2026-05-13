package com.ssafy.home.deal;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.public-data")
public record PublicDataProperties(String serviceKey) {
}
