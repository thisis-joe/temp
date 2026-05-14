package com.ssafy.home.house;

import java.math.BigDecimal;

public record HouseDealDto(
        Integer no,
        String aptSeq,
        String aptDong,
        String floor,
        Integer dealYear,
        Integer dealMonth,
        Integer dealDay,
        BigDecimal excluUseAr,
        String dealAmount
) {
}
