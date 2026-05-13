package com.ssafy.home.favorite;

import com.ssafy.home.common.ApiResponse;
import com.ssafy.home.member.AuthController;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    ApiResponse<List<FavoriteDto>> list(HttpSession session) {
        return ApiResponse.ok(favoriteService.list(memberId(session)));
    }

    @PostMapping
    ApiResponse<FavoriteDto> add(@RequestBody @Valid FavoriteDto.Request request, HttpSession session) {
        return ApiResponse.created(favoriteService.add(memberId(session), request));
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable long id, HttpSession session) {
        favoriteService.delete(memberId(session), id);
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
