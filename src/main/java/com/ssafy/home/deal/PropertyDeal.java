package com.ssafy.home.deal;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PropertyDeal {
    private Long id;
    private String dealType;
    private String lawdCd;
    private String umdNm;
    private String houseName;
    private String houseType;
    private String jibun;
    private String roadName;
    private Integer buildYear;
    private BigDecimal exclusiveArea;
    private BigDecimal landArea;
    private Integer dealYear;
    private Integer dealMonth;
    private Integer dealDay;
    private Long dealAmount;
    private Long deposit;
    private Long monthlyRent;
    private String floor;
    private String dealGbn;
    private String rawXml;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDealType() { return dealType; }
    public void setDealType(String dealType) { this.dealType = dealType; }
    public String getLawdCd() { return lawdCd; }
    public void setLawdCd(String lawdCd) { this.lawdCd = lawdCd; }
    public String getUmdNm() { return umdNm; }
    public void setUmdNm(String umdNm) { this.umdNm = umdNm; }
    public String getHouseName() { return houseName; }
    public void setHouseName(String houseName) { this.houseName = houseName; }
    public String getHouseType() { return houseType; }
    public void setHouseType(String houseType) { this.houseType = houseType; }
    public String getJibun() { return jibun; }
    public void setJibun(String jibun) { this.jibun = jibun; }
    public String getRoadName() { return roadName; }
    public void setRoadName(String roadName) { this.roadName = roadName; }
    public Integer getBuildYear() { return buildYear; }
    public void setBuildYear(Integer buildYear) { this.buildYear = buildYear; }
    public BigDecimal getExclusiveArea() { return exclusiveArea; }
    public void setExclusiveArea(BigDecimal exclusiveArea) { this.exclusiveArea = exclusiveArea; }
    public BigDecimal getLandArea() { return landArea; }
    public void setLandArea(BigDecimal landArea) { this.landArea = landArea; }
    public Integer getDealYear() { return dealYear; }
    public void setDealYear(Integer dealYear) { this.dealYear = dealYear; }
    public Integer getDealMonth() { return dealMonth; }
    public void setDealMonth(Integer dealMonth) { this.dealMonth = dealMonth; }
    public Integer getDealDay() { return dealDay; }
    public void setDealDay(Integer dealDay) { this.dealDay = dealDay; }
    public Long getDealAmount() { return dealAmount; }
    public void setDealAmount(Long dealAmount) { this.dealAmount = dealAmount; }
    public Long getDeposit() { return deposit; }
    public void setDeposit(Long deposit) { this.deposit = deposit; }
    public Long getMonthlyRent() { return monthlyRent; }
    public void setMonthlyRent(Long monthlyRent) { this.monthlyRent = monthlyRent; }
    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }
    public String getDealGbn() { return dealGbn; }
    public void setDealGbn(String dealGbn) { this.dealGbn = dealGbn; }
    public String getRawXml() { return rawXml; }
    public void setRawXml(String rawXml) { this.rawXml = rawXml; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
