package com.ssafy.home.notice;

import com.ssafy.home.common.ApiResponse;
import com.ssafy.home.member.AuthController;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {
    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping
    ApiResponse<List<NoticeDto>> list(@RequestParam(required = false) String keyword) {
        return ApiResponse.ok(noticeService.list(keyword));
    }

    @GetMapping("/{id}")
    ApiResponse<NoticeDto> detail(@PathVariable long id) {
        return ApiResponse.ok(noticeService.detail(id));
    }

    @PostMapping
    ApiResponse<NoticeDto> create(@RequestBody @Valid NoticeDto.Request request, HttpSession session) {
        return ApiResponse.created(noticeService.create(memberIdOrNull(session), request));
    }

    @PutMapping("/{id}")
    ApiResponse<NoticeDto> update(@PathVariable long id, @RequestBody @Valid NoticeDto.Request request) {
        return ApiResponse.ok(noticeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable long id) {
        noticeService.delete(id);
        return ApiResponse.message("삭제되었습니다.");
    }

    private static Long memberIdOrNull(HttpSession session) {
        return (Long) session.getAttribute(AuthController.LOGIN_MEMBER_ID);
    }
}
