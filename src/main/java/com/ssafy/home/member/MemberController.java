package com.ssafy.home.member;

import com.ssafy.home.common.ApiResponse;
import com.ssafy.home.common.OperationLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final OperationLogService operationLogService;

    @GetMapping
    ApiResponse<List<MemberDto>> members(@RequestParam(required = false) String keyword) {
        return ApiResponse.ok(memberService.findAll(keyword));
    }

    @GetMapping("/{id}")
    ApiResponse<MemberDto> member(@PathVariable long id) {
        return ApiResponse.ok(memberService.find(id));
    }

    @PutMapping("/{id}")
    ApiResponse<MemberDto> update(@PathVariable long id, @RequestBody @Valid MemberDto.UpdateRequest request) {
        MemberDto member = memberService.update(id, request);
        operationLogService.record("members", "UPDATE",
                "회원 정보 수정 완료. memberId=%d, email=%s".formatted(member.id(), member.email()));
        return ApiResponse.ok(member);
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable long id) {
        memberService.delete(id);
        operationLogService.record("members", "DELETE", "회원 삭제 완료. memberId=%d".formatted(id));
        return ApiResponse.message("삭제되었습니다.");
    }
}
