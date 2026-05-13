package com.ssafy.home.member;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MemberService {
    private final MemberMapper memberMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public MemberService(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @Transactional
    public MemberDto register(MemberDto.RegisterRequest request) {
        if (memberMapper.findByEmail(request.email()) != null) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        Member member = new Member();
        member.setEmail(request.email());
        // 비밀번호는 원문 저장 금지. BCrypt 해시로 변환해서 DB에 저장한다.
        member.setPassword(passwordEncoder.encode(request.password()));
        member.setName(request.name());
        member.setPhone(request.phone());
        member.setAddress(request.address());
        member.setRole("USER");
        memberMapper.insert(member);
        return MemberDto.from(memberMapper.findById(member.getId()));
    }

    public Member login(MemberDto.LoginRequest request) {
        Member member = memberMapper.findByEmail(request.email());
        // 입력 비밀번호와 DB에 저장된 BCrypt 해시를 비교한다.
        if (member == null || !passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new SecurityException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return member;
    }

    public MemberDto find(long id) {
        return MemberDto.from(requiredMember(id));
    }

    public List<MemberDto> findAll(String keyword) {
        return memberMapper.findAll(keyword).stream().map(MemberDto::from).toList();
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
        return MemberDto.from(requiredMember(id));
    }

    @Transactional
    public void delete(long id) {
        if (memberMapper.delete(id) == 0) {
            throw new NoSuchElementException("회원을 찾을 수 없습니다.");
        }
    }

    Member requiredMember(long id) {
        Member member = memberMapper.findById(id);
        if (member == null) {
            throw new NoSuchElementException("회원을 찾을 수 없습니다.");
        }
        return member;
    }
}
