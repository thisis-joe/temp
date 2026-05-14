package com.ssafy.home.member;

import com.ssafy.home.common.ApiResponse;
import com.ssafy.home.common.OperationLogService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    public static final String LOGIN_MEMBER_ID = "LOGIN_MEMBER_ID";
    private final MemberService memberService;
    private final OperationLogService operationLogService;

    @PostMapping("/register")
    ApiResponse<MemberDto> register(@RequestBody @Valid MemberDto.RegisterRequest request, HttpSession session) {
        MemberDto member = memberService.register(request);
        session.setAttribute(LOGIN_MEMBER_ID, member.id());
        operationLogService.record("members", "REGISTER",
                "회원 가입 및 자동 로그인 완료. memberId=%d, email=%s, sessionId=%s"
                        .formatted(member.id(), member.email(), session.getId()));
        return ApiResponse.created(member);
    }

    @PostMapping("/login")
    ApiResponse<MemberDto> login(@RequestBody @Valid MemberDto.LoginRequest request, HttpSession session) {
        Member member = memberService.login(request);
        session.setAttribute(LOGIN_MEMBER_ID, member.getId());
        operationLogService.record("members", "LOGIN",
                "로그인 완료. memberId=%d, email=%s, sessionId=%s"
                        .formatted(member.getId(), member.getEmail(), session.getId()));
        return ApiResponse.ok(MemberDto.from(member));
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(HttpSession session) {
        Long id = (Long) session.getAttribute(LOGIN_MEMBER_ID);
        String sessionId = session.getId();
        session.invalidate();
        operationLogService.record("members", "LOGOUT",
                "로그아웃 완료. memberId=%s, previousSessionId=%s"
                        .formatted(id == null ? "anonymous" : id, sessionId));
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
