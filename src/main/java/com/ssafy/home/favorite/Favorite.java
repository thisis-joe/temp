package com.ssafy.home.favorite;

import java.time.LocalDateTime;

public class Favorite {
    private Long id;
    private Long memberId;
    private String sidoNm;
    private String sigunguNm;
    private String dongNm;
    private String lawdCd;
    private String memo;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getSidoNm() { return sidoNm; }
    public void setSidoNm(String sidoNm) { this.sidoNm = sidoNm; }
    public String getSigunguNm() { return sigunguNm; }
    public void setSigunguNm(String sigunguNm) { this.sigunguNm = sigunguNm; }
    public String getDongNm() { return dongNm; }
    public void setDongNm(String dongNm) { this.dongNm = dongNm; }
    public String getLawdCd() { return lawdCd; }
    public void setLawdCd(String lawdCd) { this.lawdCd = lawdCd; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
