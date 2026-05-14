package com.ssafy.home.region;

import com.ssafy.home.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionController {
    private final RegionService regionService;

    @GetMapping("/sido")
    ApiResponse<List<RegionCodeDto>> sido() {
        return ApiResponse.ok(regionService.sidos());
    }

    @GetMapping("/sigungu")
    ApiResponse<List<RegionCodeDto>> sigungu(@RequestParam String sidoCode) {
        return ApiResponse.ok(regionService.sigungus(sidoCode));
    }

    @GetMapping("/dong")
    ApiResponse<List<RegionCodeDto>> dong(@RequestParam String sigunguCode) {
        return ApiResponse.ok(regionService.dongs(sigunguCode));
    }

    @GetMapping(value = "/sgis-token", produces = MediaType.APPLICATION_JSON_VALUE)
    String sgisToken() {
        return regionService.sgisAccessToken();
    }

    @GetMapping(value = "/vworld/sido", produces = MediaType.APPLICATION_JSON_VALUE)
    String vworldSido() {
        return regionService.vworldSido();
    }

    @GetMapping(value = "/vworld/sigungu", produces = MediaType.APPLICATION_JSON_VALUE)
    String vworldSigungu(@RequestParam String sidoCode) {
        return regionService.vworldSigungu(sidoCode);
    }

    @GetMapping(value = "/vworld/dong", produces = MediaType.APPLICATION_JSON_VALUE)
    String vworldDong(@RequestParam String sigunguCode) {
        return regionService.vworldDong(sigunguCode);
    }
}
