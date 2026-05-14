package com.ssafy.home.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberMapper memberMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public MemberDto register(MemberDto.RegisterRequest request) {
        log.info("회원 가입 요청 검증 시작. email={}, name={}", request.email(), request.name());
        if (memberMapper.findByEmail(request.email()) != null) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        Member member = new Member();
        member.setEmail(request.email());
        member.setPassword(passwordEncoder.encode(request.password()));
        member.setName(request.name());
        member.setPhone(request.phone());
        member.setAddress(request.address());
        member.setRole("USER");
        memberMapper.insert(member);
        log.info("회원 가입 DB 저장 완료. memberId={}, email={}", member.getId(), member.getEmail());
        return MemberDto.from(memberMapper.findById(member.getId()));
    }

    public Member login(MemberDto.LoginRequest request) {
        log.info("로그인 요청 검증 시작. email={}", request.email());
        Member member = memberMapper.findByEmail(request.email());
        if (member == null || !passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new SecurityException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        log.info("로그인 검증 성공. memberId={}, email={}", member.getId(), member.getEmail());
        return member;
    }

    public MemberDto find(long id) {
        return MemberDto.from(requiredMember(id));
    }

    public List<MemberDto> findAll(String keyword) {
        List<MemberDto> members = memberMapper.findAll(keyword).stream().map(MemberDto::from).toList();
        log.info("회원 목록 조회 완료. keyword={}, resultCount={}", keyword, members.size());
        return members;
    }

    @Transactional
    public MemberDto update(long id, MemberDto.UpdateRequest request) {
        Member member = requiredMember(id);
        member.setName(request.name());
        member.setPhone(request.phone());
        member.setAddress(request.address());
        if (request.password() != null && !request.password().isBlank()) {
            member.setPassword(passwordEncoder.encode(request.password()));
        }
        memberMapper.update(member);
        log.info("회원 정보 수정 DB 반영 완료. memberId={}, email={}", member.getId(), member.getEmail());
        return MemberDto.from(requiredMember(id));
    }

    @Transactional
    public void delete(long id) {
        if (memberMapper.delete(id) == 0) {
            throw new NoSuchElementException("회원을 찾을 수 없습니다.");
        }
        log.info("회원 삭제 DB 반영 완료. memberId={}", id);
    }

    Member requiredMember(long id) {
        Member member = memberMapper.findById(id);
        if (member == null) {
            throw new NoSuchElementException("회원을 찾을 수 없습니다.");
        }
        return member;
    }
}
