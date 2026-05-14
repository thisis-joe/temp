package com.ssafy.home.house;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HouseMapper {
    List<DongCodeDto> searchDongCodes(@Param("keyword") String keyword);

    List<HouseInfoDto> searchHouses(
            @Param("keyword") String keyword,
            @Param("dongCode") String dongCode
    );

    HouseInfoDto findHouse(@Param("aptSeq") String aptSeq);

    List<HouseDealDto> findDeals(@Param("aptSeq") String aptSeq);
}
