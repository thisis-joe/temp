package com.ssafy.home.house;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class HouseService {
    private final HouseMapper houseMapper;

    public List<DongCodeDto> searchDongCodes(String keyword) {
        List<DongCodeDto> dongCodes = houseMapper.searchDongCodes(blankToNull(keyword));
        log.info("법정동 검색 완료. keyword={}, resultCount={}", keyword, dongCodes.size());
        return dongCodes;
    }

    public List<HouseInfoDto> searchHouses(String keyword, String dongCode) {
        List<HouseInfoDto> houses = houseMapper.searchHouses(blankToNull(keyword), normalizeDongCode(dongCode));
        log.info("주거 단지 통합 검색 완료. keyword={}, dongCode={}, resultCount={}", keyword, dongCode, houses.size());
        return houses;
    }

    public HouseDetailDto detail(String aptSeq) {
        HouseInfoDto house = houseMapper.findHouse(aptSeq);
        if (house == null) {
            throw new NoSuchElementException("아파트 정보를 찾을 수 없습니다.");
        }
        List<HouseDealDto> deals = houseMapper.findDeals(aptSeq);
        log.info("주거 단지 상세 조회 완료. aptSeq={}, aptName={}, dealCount={}", aptSeq, house.aptNm(), deals.size());
        return new HouseDetailDto(house, deals);
    }

    public List<HouseDealDto> deals(String aptSeq) {
        List<HouseDealDto> deals = houseMapper.findDeals(aptSeq);
        log.info("주거 단지 거래 이력 조회 완료. aptSeq={}, resultCount={}", aptSeq, deals.size());
        return deals;
    }

    private static String normalizeDongCode(String dongCode) {
        if (dongCode == null || dongCode.isBlank()) {
            return null;
        }
        return dongCode.length() >= 10 ? dongCode.substring(0, 10) : dongCode;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
