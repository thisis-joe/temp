package com.ssafy.home.house;

import com.ssafy.home.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/houses")
@RequiredArgsConstructor
public class HouseController {
    private final HouseService houseService;

    @GetMapping("/dongcodes")
    ApiResponse<List<DongCodeDto>> dongCodes(@RequestParam(required = false) String keyword) {
        return ApiResponse.ok(houseService.searchDongCodes(keyword));
    }

    @GetMapping
    ApiResponse<List<HouseInfoDto>> houses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String dongCode
    ) {
        return ApiResponse.ok(houseService.searchHouses(keyword, dongCode));
    }

    @GetMapping("/{aptSeq}")
    ApiResponse<HouseDetailDto> detail(@PathVariable String aptSeq) {
        return ApiResponse.ok(houseService.detail(aptSeq));
    }

    @GetMapping("/{aptSeq}/deals")
    ApiResponse<List<HouseDealDto>> deals(@PathVariable String aptSeq) {
        return ApiResponse.ok(houseService.deals(aptSeq));
    }
}
