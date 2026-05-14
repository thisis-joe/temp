package com.ssafy.home.notice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeService {
    private final NoticeMapper noticeMapper;

    @Transactional
    public NoticeDto create(Long writerId, NoticeDto.Request request) {
        Notice notice = new Notice();
        notice.setTitle(request.title());
        notice.setContent(request.content());
        notice.setWriterId(writerId);
        noticeMapper.insert(notice);
        log.info("공지사항 등록 DB 저장 완료. noticeId={}, writerId={}, title={}", notice.getId(), writerId, notice.getTitle());
        return NoticeDto.from(required(notice.getId()));
    }

    public List<NoticeDto> list(String keyword) {
        List<NoticeDto> notices = noticeMapper.findAll(keyword).stream().map(NoticeDto::from).toList();
        log.info("공지사항 목록 조회 완료. keyword={}, resultCount={}", keyword, notices.size());
        return notices;
    }

    @Transactional
    public NoticeDto detail(long id) {
        noticeMapper.increaseViewCount(id);
        Notice notice = required(id);
        log.info("공지사항 상세 조회 완료. noticeId={}, viewCount={}", id, notice.getViewCount());
        return NoticeDto.from(notice);
    }

    @Transactional
    public NoticeDto update(long id, NoticeDto.Request request) {
        Notice notice = required(id);
        notice.setTitle(request.title());
        notice.setContent(request.content());
        noticeMapper.update(notice);
        log.info("공지사항 수정 DB 반영 완료. noticeId={}, title={}", id, request.title());
        return NoticeDto.from(required(id));
    }

    @Transactional
    public void delete(long id) {
        if (noticeMapper.delete(id) == 0) {
            throw new NoSuchElementException("공지사항을 찾을 수 없습니다.");
        }
        log.info("공지사항 삭제 DB 반영 완료. noticeId={}", id);
    }

    private Notice required(long id) {
        Notice notice = noticeMapper.findById(id);
        if (notice == null) {
            throw new NoSuchElementException("공지사항을 찾을 수 없습니다.");
        }
        return notice;
    }
}
