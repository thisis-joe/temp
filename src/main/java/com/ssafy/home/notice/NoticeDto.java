package com.ssafy.home.notice;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record NoticeDto(Long id, String title, String content, Long writerId, Integer viewCount, LocalDateTime createdAt) {
    static NoticeDto from(Notice notice) {
        return new NoticeDto(notice.getId(), notice.getTitle(), notice.getContent(), notice.getWriterId(), notice.getViewCount(), notice.getCreatedAt());
    }

    public record Request(@NotBlank String title, @NotBlank String content) {}
}
