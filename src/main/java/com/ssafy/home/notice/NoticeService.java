package com.ssafy.home.notice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class NoticeService {
    private final NoticeMapper noticeMapper;

    public NoticeService(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    @Transactional
    public NoticeDto create(Long writerId, NoticeDto.Request request) {
        Notice notice = new Notice();
        notice.setTitle(request.title());
        notice.setContent(request.content());
        notice.setWriterId(writerId);
        noticeMapper.insert(notice);
        return NoticeDto.from(required(notice.getId()));
    }

    public List<NoticeDto> list(String keyword) {
        return noticeMapper.findAll(keyword).stream().map(NoticeDto::from).toList();
    }

    @Transactional
    public NoticeDto detail(long id) {
        noticeMapper.increaseViewCount(id);
        return NoticeDto.from(required(id));
    }

    @Transactional
    public NoticeDto update(long id, NoticeDto.Request request) {
        Notice notice = required(id);
        notice.setTitle(request.title());
        notice.setContent(request.content());
        noticeMapper.update(notice);
        return NoticeDto.from(required(id));
    }

    @Transactional
    public void delete(long id) {
        if (noticeMapper.delete(id) == 0) {
            throw new NoSuchElementException("공지사항을 찾을 수 없습니다.");
        }
    }

    private Notice required(long id) {
        Notice notice = noticeMapper.findById(id);
        if (notice == null) {
            throw new NoSuchElementException("공지사항을 찾을 수 없습니다.");
        }
        return notice;
    }
}
