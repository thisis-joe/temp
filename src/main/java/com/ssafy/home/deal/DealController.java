package com.ssafy.home.deal;

import com.ssafy.home.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deals")
public class DealController {
    private final DealService dealService;

    public DealController(DealService dealService) {
        this.dealService = dealService;
    }

    @PostMapping("/fetch")
    ApiResponse<List<PropertyDeal>> fetch(
            @RequestParam DealType type,
            @RequestParam String lawdCd,
            @RequestParam String dealYmd,
            @RequestParam(defaultValue = "100") int numOfRows
    ) {
        // 외부 공공데이터 XML API를 호출한 뒤, 파싱 결과를 MySQL에 저장한다.
        return ApiResponse.ok(dealService.fetchAndSave(type, lawdCd, dealYmd, numOfRows));
    }

    @GetMapping
    ApiResponse<List<PropertyDeal>> search(
            @RequestParam(required = false) String dealType,
            @RequestParam(required = false) String lawdCd,
            @RequestParam(required = false) String dong,
            @RequestParam(required = false) String houseName,
            @RequestParam(required = false) String dealYmd
    ) {
        // 이미 DB에 저장된 실거래가를 조건별로 조회한다. 화면의 "DB 검색" 버튼과 연결된다.
        return ApiResponse.ok(dealService.search(dealType, lawdCd, dong, houseName, dealYmd));
    }
}
