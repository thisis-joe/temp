package com.ssafy.home.favorite;

import jakarta.validation.constraints.NotBlank;

public record FavoriteDto(
        Long id,
        Long memberId,
        String sidoNm,
        String sigunguNm,
        String dongNm,
        @NotBlank String lawdCd,
        String memo
) {
    static FavoriteDto from(Favorite favorite) {
        return new FavoriteDto(
                favorite.getId(),
                favorite.getMemberId(),
                favorite.getSidoNm(),
                favorite.getSigunguNm(),
                favorite.getDongNm(),
                favorite.getLawdCd(),
                favorite.getMemo()
        );
    }

    public record Request(String sidoNm, String sigunguNm, String dongNm, @NotBlank String lawdCd, String memo) {}
}
