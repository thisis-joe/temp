package com.ssafy.home.favorite;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class FavoriteService {
    private final FavoriteMapper favoriteMapper;

    public FavoriteService(FavoriteMapper favoriteMapper) {
        this.favoriteMapper = favoriteMapper;
    }

    @Transactional
    public FavoriteDto add(long memberId, FavoriteDto.Request request) {
        Favorite favorite = new Favorite();
        favorite.setMemberId(memberId);
        favorite.setSidoNm(request.sidoNm());
        favorite.setSigunguNm(request.sigunguNm());
        favorite.setDongNm(request.dongNm());
        favorite.setLawdCd(request.lawdCd());
        favorite.setMemo(request.memo());
        favoriteMapper.insert(favorite);
        return FavoriteDto.from(favorite);
    }

    public List<FavoriteDto> list(long memberId) {
        return favoriteMapper.findByMemberId(memberId).stream().map(FavoriteDto::from).toList();
    }

    @Transactional
    public void delete(long memberId, long id) {
        if (favoriteMapper.delete(id, memberId) == 0) {
            throw new NoSuchElementException("관심지역을 찾을 수 없습니다.");
        }
    }
}
