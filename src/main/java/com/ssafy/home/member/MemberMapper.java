package com.ssafy.home.member;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberMapper {
    int insert(Member member);
    Member findById(long id);
    Member findByEmail(String email);
    List<Member> findAll(@Param("keyword") String keyword);
    int update(Member member);
    int delete(long id);
}
