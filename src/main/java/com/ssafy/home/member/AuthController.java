package com.ssafy.home.member;

import com.ssafy.home.common.ApiResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    public static final String LOGIN_MEMBER_ID = "LOGIN_MEMBER_ID";
    private final MemberService memberService;

    public AuthController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    ApiResponse<MemberDto> register(@RequestBody @Valid MemberDto.RegisterRequest request, HttpSession session) {
        MemberDto member = memberService.register(request);
        session.setAttribute(LOGIN_MEMBER_ID, member.id());
        return ApiResponse.created(member);
    }

    @PostMapping("/login")
    ApiResponse<MemberDto> login(@RequestBody @Valid MemberDto.LoginRequest request, HttpSession session) {
        Member member = memberService.login(request);
        session.setAttribute(LOGIN_MEMBER_ID, member.getId());
        return ApiResponse.ok(MemberDto.from(member));
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(HttpSession session) {
        session.invalidate();
        return ApiResponse.message("로그아웃되었습니다.");
    }

    @GetMapping("/me")
    ApiResponse<MemberDto> me(HttpSession session) {
        Long id = (Long) session.getAttribute(LOGIN_MEMBER_ID);
        if (id == null) {
            throw new SecurityException("로그인이 필요합니다.");
        }
        return ApiResponse.ok(memberService.find(id));
    }
}
