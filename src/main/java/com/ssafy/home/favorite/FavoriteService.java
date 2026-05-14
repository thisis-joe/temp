package com.ssafy.home.favorite;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteService {
    private final FavoriteMapper favoriteMapper;

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
        log.info("관심지역 등록 DB 저장 완료. memberId={}, favoriteId={}, lawdCd={}", memberId, favorite.getId(), favorite.getLawdCd());
        return FavoriteDto.from(favorite);
    }

    public List<FavoriteDto> list(long memberId) {
        List<FavoriteDto> favorites = favoriteMapper.findByMemberId(memberId).stream().map(FavoriteDto::from).toList();
        log.info("관심지역 목록 조회 완료. memberId={}, resultCount={}", memberId, favorites.size());
        return favorites;
    }

    @Transactional
    public void delete(long memberId, long id) {
        if (favoriteMapper.delete(id, memberId) == 0) {
            throw new NoSuchElementException("관심지역을 찾을 수 없습니다.");
        }
        log.info("관심지역 삭제 DB 반영 완료. memberId={}, favoriteId={}", memberId, id);
    }
}
