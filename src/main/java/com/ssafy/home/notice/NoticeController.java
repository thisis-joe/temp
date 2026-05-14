package com.ssafy.home.notice;

import com.ssafy.home.common.ApiResponse;
import com.ssafy.home.common.OperationLogService;
import com.ssafy.home.member.AuthController;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;
    private final OperationLogService operationLogService;

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
        NoticeDto notice = noticeService.create(memberIdOrNull(session), request);
        operationLogService.record("notices", "CREATE",
                "공지사항 등록 완료. noticeId=%d, writerId=%s, title=%s"
                        .formatted(notice.id(), notice.writerId(), notice.title()));
        return ApiResponse.created(notice);
    }

    @PutMapping("/{id}")
    ApiResponse<NoticeDto> update(@PathVariable long id, @RequestBody @Valid NoticeDto.Request request) {
        NoticeDto notice = noticeService.update(id, request);
        operationLogService.record("notices", "UPDATE",
                "공지사항 수정 완료. noticeId=%d, title=%s".formatted(notice.id(), notice.title()));
        return ApiResponse.ok(notice);
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable long id) {
        noticeService.delete(id);
        operationLogService.record("notices", "DELETE", "공지사항 삭제 완료. noticeId=%d".formatted(id));
        return ApiResponse.message("삭제되었습니다.");
    }

    private static Long memberIdOrNull(HttpSession session) {
        return (Long) session.getAttribute(AuthController.LOGIN_MEMBER_ID);
    }
}
