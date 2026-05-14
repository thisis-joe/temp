package com.ssafy.home.favorite;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;
    private final OperationLogService operationLogService;

    @GetMapping
    ApiResponse<List<FavoriteDto>> list(HttpSession session) {
        return ApiResponse.ok(favoriteService.list(memberId(session)));
    }

    @PostMapping
    ApiResponse<FavoriteDto> add(@RequestBody @Valid FavoriteDto.Request request, HttpSession session) {
        long memberId = memberId(session);
        FavoriteDto favorite = favoriteService.add(memberId, request);
        operationLogService.record("favorites", "CREATE",
                "관심지역 등록 완료. memberId=%d, favoriteId=%d, lawdCd=%s, sido=%s, sigungu=%s, dong=%s"
                        .formatted(memberId, favorite.id(), favorite.lawdCd(), favorite.sidoNm(), favorite.sigunguNm(), favorite.dongNm()));
        return ApiResponse.created(favorite);
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable long id, HttpSession session) {
        long memberId = memberId(session);
        favoriteService.delete(memberId, id);
        operationLogService.record("favorites", "DELETE",
                "관심지역 삭제 완료. memberId=%d, favoriteId=%d".formatted(memberId, id));
        return ApiResponse.message("삭제되었습니다.");
    }

    private static long memberId(HttpSession session) {
        Long id = (Long) session.getAttribute(AuthController.LOGIN_MEMBER_ID);
        if (id == null) {
            throw new SecurityException("로그인이 필요합니다.");
        }
        return id;
    }
}
