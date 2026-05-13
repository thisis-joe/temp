package com.ssafy.home.member;

import com.ssafy.home.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

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
        return ApiResponse.ok(memberService.update(id, request));
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable long id) {
        memberService.delete(id);
        return ApiResponse.message("삭제되었습니다.");
    }
}
