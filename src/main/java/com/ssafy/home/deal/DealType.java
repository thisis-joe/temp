package com.ssafy.home.deal;

public enum DealType {
    APT_TRADE("https://apis.data.go.kr/1613000/RTMSDataSvcAptTradeDev/getRTMSDataSvcAptTradeDev"),
    APT_RENT("https://apis.data.go.kr/1613000/RTMSDataSvcAptRent/getRTMSDataSvcAptRent"),
    RH_TRADE("https://apis.data.go.kr/1613000/RTMSDataSvcRHTrade/getRTMSDataSvcRHTrade"),
    RH_RENT("https://apis.data.go.kr/1613000/RTMSDataSvcRHRent/getRTMSDataSvcRHRent");

    private final String url;

    DealType(String url) {
        this.url = url;
    }

    public String url() {
        return url;
    }
}
