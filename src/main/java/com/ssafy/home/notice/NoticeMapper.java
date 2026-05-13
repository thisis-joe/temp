package com.ssafy.home.notice;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeMapper {
    int insert(Notice notice);
    Notice findById(long id);
    List<Notice> findAll(@Param("keyword") String keyword);
    int update(Notice notice);
    int delete(long id);
    int increaseViewCount(long id);
}
