package com.ssafy.home.region;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/regions")
public class RegionController {
    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping(value = "/sido", produces = MediaType.APPLICATION_JSON_VALUE)
    String sido() {
        return regionService.sido();
    }

    @GetMapping(value = "/sigungu", produces = MediaType.APPLICATION_JSON_VALUE)
    String sigungu(@RequestParam String sidoCode) {
        return regionService.sigungu(sidoCode);
    }

    @GetMapping(value = "/dong", produces = MediaType.APPLICATION_JSON_VALUE)
    String dong(@RequestParam String sigunguCode) {
        return regionService.dong(sigunguCode);
    }

    @GetMapping(value = "/sgis-token", produces = MediaType.APPLICATION_JSON_VALUE)
    String sgisToken() {
        return regionService.sgisAccessToken();
    }
}
