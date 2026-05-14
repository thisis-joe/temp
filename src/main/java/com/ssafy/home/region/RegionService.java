package com.ssafy.home.region;

import com.ssafy.home.common.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
@EnableConfigurationProperties({VWorldProperties.class, SgisProperties.class})
@RequiredArgsConstructor
@Slf4j
public class RegionService {
    private final RestClient restClient;
    private final VWorldProperties vworld;
    private final SgisProperties sgis;
    private final RegionMapper regionMapper;

    public List<RegionCodeDto> sidos() {
        List<RegionCodeDto> result = regionMapper.findSidos();
        log.info("DB 기반 시도 조회 완료. resultCount={}", result.size());
        return result;
    }

    public List<RegionCodeDto> sigungus(String sidoCode) {
        requireCode(sidoCode, "시도 코드가 필요합니다.");
        List<RegionCodeDto> result = regionMapper.findSigungus(sidoCode);
        log.info("DB 기반 시군구 조회 완료. sidoCode={}, resultCount={}", sidoCode, result.size());
        return result;
    }

    public List<RegionCodeDto> dongs(String sigunguCode) {
        requireCode(sigunguCode, "시군구 코드가 필요합니다.");
        List<RegionCodeDto> result = regionMapper.findDongs(sigunguCode);
        log.info("DB 기반 읍면동 조회 완료. sigunguCode={}, resultCount={}", sigunguCode, result.size());
        return result;
    }

    public String vworldSido() {
        return vworldData("LT_C_ADSIDO_INFO", null);
    }

    public String vworldSigungu(String sidoCode) {
        return vworldData("LT_C_ADSIGG_INFO", filter("ctprvn_cd", sidoCode));
    }

    public String vworldDong(String sigunguCode) {
        return vworldData("LT_C_ADEMD_INFO", filter("sig_cd", sigunguCode));
    }

    public String sgisAccessToken() {
        URI uri = UriComponentsBuilder
                .fromHttpUrl("https://sgisapi.mods.go.kr/OpenAPI3/auth/authentication.json")
                .queryParam("consumer_key", sgis.consumerKey())
                .queryParam("consumer_secret", sgis.consumerSecret())
                .build()
                .toUri();
        log.info("SGIS 인증 토큰 호출 시작. endpoint={}", uri.getPath());
        String body = restClient.get().uri(uri).retrieve().body(String.class);
        log.info("SGIS 인증 토큰 호출 완료. responseLength={}", body == null ? 0 : body.length());
        return body;
    }

    private String vworldData(String data, String attrFilter) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl("https://api.vworld.kr/req/data")
                .queryParam("service", "data")
                .queryParam("request", "GetFeature")
                .queryParam("data", data)
                .queryParam("format", "json")
                .queryParam("size", 1000)
                .queryParam("page", 1)
                .queryParam("geometry", "false")
                .queryParam("key", vworld.key());
        if (attrFilter != null) {
            builder.queryParam("attrFilter", attrFilter);
        }
        URI uri = builder.build().toUri();
        log.info("VWorld API 호출 시작. data={}, attrFilter={}, endpoint={}", data, attrFilter, uri.getPath());
        String body = restClient.get().uri(uri).retrieve().body(String.class);
        if (body != null && body.contains("\"status\"") && body.contains("\"ERROR\"")) {
            throw new ExternalApiException("VWorld API 오류 응답: " + body);
        }
        log.info("VWorld API 호출 완료. data={}, attrFilter={}, responseLength={}", data, attrFilter, body == null ? 0 : body.length());
        return body;
    }

    private static String filter(String column, String value) {
        requireCode(value, "지역 코드가 필요합니다.");
        return column + ":=:" + value;
    }

    private static void requireCode(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
