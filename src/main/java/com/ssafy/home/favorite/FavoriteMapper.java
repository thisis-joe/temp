package com.ssafy.home.favorite;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FavoriteMapper {
    int insert(Favorite favorite);
    List<Favorite> findByMemberId(long memberId);
    int delete(@Param("id") long id, @Param("memberId") long memberId);
}
