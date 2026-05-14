package com.ssafy.home.deal;

import java.math.BigDecimal;

public record DealSummary(
        String dealType,
        int dealCount,
        Long minDealAmount,
        Long avgDealAmount,
        Long maxDealAmount,
        Long avgDeposit,
        Long avgMonthlyRent,
        BigDecimal avgExclusiveArea,
        BigDecimal avgPricePerSquareMeter
) {
}
