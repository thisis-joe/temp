package com.ssafy.home.deal;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DealMapper {
    int insert(PropertyDeal deal);

    int deleteByTypeAndMonth(@Param("dealType") String dealType, @Param("lawdCd") String lawdCd, @Param("dealYmd") String dealYmd);

    List<PropertyDeal> search(
            @Param("dealType") String dealType,
            @Param("lawdCd") String lawdCd,
            @Param("dong") String dong,
            @Param("houseName") String houseName,
            @Param("dealYmd") String dealYmd,
            @Param("keyword") String keyword
    );

    int countByTypeAndMonth(@Param("dealType") String dealType, @Param("lawdCd") String lawdCd, @Param("dealYmd") String dealYmd);

    List<DealSummary> summarize(@Param("lawdCd") String lawdCd, @Param("dealYmd") String dealYmd);
}
