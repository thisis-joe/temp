package com.ssafy.home.deal;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DealMapper {
    int insert(PropertyDeal deal);

    List<PropertyDeal> search(
            @Param("dealType") String dealType,
            @Param("lawdCd") String lawdCd,
            @Param("dong") String dong,
            @Param("houseName") String houseName,
            @Param("dealYmd") String dealYmd
    );

    int countByTypeAndMonth(@Param("dealType") String dealType, @Param("lawdCd") String lawdCd, @Param("dealYmd") String dealYmd);
}
