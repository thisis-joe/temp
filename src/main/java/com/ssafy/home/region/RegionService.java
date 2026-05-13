package com.ssafy.home.region;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@EnableConfigurationProperties({VWorldProperties.class, SgisProperties.class})
public class RegionService {
    private final RestClient restClient;
    private final VWorldProperties vworld;
    private final SgisProperties sgis;

    public RegionService(RestClient restClient, VWorldProperties vworld, SgisProperties sgis) {
        this.restClient = restClient;
        this.vworld = vworld;
        this.sgis = sgis;
    }

    public String sido() {
        return vworldData("LT_C_ADSIDO_INFO", vworld.sidoKey(), null);
    }

    public String sigungu(String sidoCode) {
        return vworldData("LT_C_ADSIGG_INFO", vworld.sigunguKey(), filter("ctprvn_cd", sidoCode));
    }

    public String dong(String sigunguCode) {
        return vworldData("LT_C_ADEMD_INFO", vworld.dongKey(), filter("sig_cd", sigunguCode));
    }

    public String sgisAccessToken() {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://sgisapi.mods.go.kr/OpenAPI3/auth/authentication.json")
                .queryParam("consumer_key", sgis.consumerKey())
                .queryParam("consumer_secret", sgis.consumerSecret())
                .toUriString();
        return restClient.get().uri(url).retrieve().body(String.class);
    }

    private String vworldData(String data, String key, String attrFilter) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl("https://api.vworld.kr/req/data")
                .queryParam("service", "data")
                .queryParam("request", "GetFeature")
                .queryParam("data", data)
                .queryParam("format", "json")
                .queryParam("size", 1000)
                .queryParam("page", 1)
                .queryParam("geometry", "false")
                .queryParam("key", key);
        if (attrFilter != null) {
            builder.queryParam("attrFilter", attrFilter);
        }
        return restClient.get().uri(builder.build(false).toUriString()).retrieve().body(String.class);
    }

    private static String filter(String column, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("지역 코드가 필요합니다.");
        }
        return column + ":=:" + value;
    }
}
