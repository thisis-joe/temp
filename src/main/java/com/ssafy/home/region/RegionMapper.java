package com.ssafy.home.region;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RegionMapper {
    List<RegionCodeDto> findSidos();

    List<RegionCodeDto> findSigungus(@Param("sidoCode") String sidoCode);

    List<RegionCodeDto> findDongs(@Param("sigunguCode") String sigunguCode);
}
