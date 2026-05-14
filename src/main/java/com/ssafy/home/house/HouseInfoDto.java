package com.ssafy.home.house;

public record HouseInfoDto(
        String aptSeq,
        String sggCd,
        String umdCd,
        String sidoName,
        String gugunName,
        String dongName,
        String umdNm,
        String jibun,
        String roadNm,
        String aptNm,
        Integer buildYear,
        String latitude,
        String longitude,
        Integer dealCount,
        Integer latestDealYear,
        Integer latestDealMonth,
        String latestDealAmount
) {
}
