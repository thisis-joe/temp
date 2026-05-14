package com.ssafy.home.deal;

import com.ssafy.home.common.ApiResponse;
import com.ssafy.home.common.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/deals")
@RequiredArgsConstructor
public class DealController {
    private final DealService dealService;
    private final OperationLogService operationLogService;

    @PostMapping("/fetch")
    ApiResponse<List<PropertyDeal>> fetch(
            @RequestParam DealType type,
            @RequestParam String lawdCd,
            @RequestParam String dealYmd,
            @RequestParam(defaultValue = "100") int numOfRows
    ) {
        List<PropertyDeal> deals = dealService.fetchAndSave(type, lawdCd, dealYmd, numOfRows);
        operationLogService.record("deals", "FETCH",
                "공공데이터 단일 수집 완료. type=%s, lawdCd=%s, dealYmd=%s, requestedRows=%d, savedRows=%d"
                        .formatted(type, lawdCd, dealYmd, numOfRows, deals.size()));
        return ApiResponse.ok(deals);
    }

    @PostMapping("/fetch-all")
    ApiResponse<List<DealFetchResult>> fetchAll(
            @RequestParam String lawdCd,
            @RequestParam String dealYmd,
            @RequestParam(defaultValue = "100") int numOfRows
    ) {
        List<DealFetchResult> results = dealService.fetchAllAndSave(lawdCd, dealYmd, numOfRows);
        int saved = results.stream().mapToInt(DealFetchResult::savedCount).sum();
        operationLogService.record("deals", "FETCH_ALL",
                "공공데이터 통합 수집 완료. lawdCd=%s, dealYmd=%s, requestedRowsPerApi=%d, apiCount=%d, savedRows=%d, detail=%s"
                        .formatted(lawdCd, dealYmd, numOfRows, results.size(), saved, results));
        return ApiResponse.ok(results);
    }

    @GetMapping("/summary")
    ApiResponse<List<DealSummary>> summary(@RequestParam String lawdCd, @RequestParam String dealYmd) {
        return ApiResponse.ok(dealService.summarize(lawdCd, dealYmd));
    }

    @GetMapping
    ApiResponse<List<PropertyDeal>> search(
            @RequestParam(required = false) String dealType,
            @RequestParam(required = false) String lawdCd,
            @RequestParam(required = false) String dong,
            @RequestParam(required = false) String houseName,
            @RequestParam(required = false) String dealYmd,
            @RequestParam(required = false) String keyword
    ) {
        return ApiResponse.ok(dealService.search(dealType, lawdCd, dong, houseName, dealYmd, keyword));
    }
}
