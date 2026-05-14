package com.ssafy.home.deal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Service
@EnableConfigurationProperties(PublicDataProperties.class)
@RequiredArgsConstructor
@Slf4j
public class DealService {
    private final DealMapper dealMapper;
    private final RestClient restClient;
    private final PublicDataProperties properties;

    @Transactional
    public List<PropertyDeal> fetchAndSave(DealType type, String lawdCd, String dealYmd, int numOfRows) {
        validateSearch(lawdCd, dealYmd);
        int requestedRows = Math.max(1, Math.min(numOfRows, 500));
        String query = UriComponentsBuilder.newInstance()
                .queryParam("LAWD_CD", lawdCd)
                .queryParam("DEAL_YMD", dealYmd)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", requestedRows)
                .build()
                .toUriString();
        URI uri = URI.create(type.url() + "?serviceKey=" + properties.serviceKey() + "&" + query.substring(1));
        log.info("공공데이터 실거래 호출 시작. type={}, lawdCd={}, dealYmd={}, requestedRows={}, endpoint={}",
                type, lawdCd, dealYmd, requestedRows, type.url());
        String xml = restClient.get().uri(uri).retrieve().body(String.class);
        List<PropertyDeal> deals = parse(type, lawdCd, xml);
        int deleted = dealMapper.deleteByTypeAndMonth(type.name(), lawdCd, dealYmd);
        deals.forEach(dealMapper::insert);
        log.info("공공데이터 실거래 저장 완료. type={}, lawdCd={}, dealYmd={}, deletedRows={}, insertedRows={}",
                type, lawdCd, dealYmd, deleted, deals.size());
        return deals;
    }

    @Transactional
    public List<DealFetchResult> fetchAllAndSave(String lawdCd, String dealYmd, int numOfRows) {
        validateSearch(lawdCd, dealYmd);
        log.info("공공데이터 4개 API 통합 수집 시작. lawdCd={}, dealYmd={}, requestedRowsPerApi={}", lawdCd, dealYmd, numOfRows);
        List<DealFetchResult> results = Arrays.stream(DealType.values())
                .map(type -> new DealFetchResult(type, fetchAndSave(type, lawdCd, dealYmd, numOfRows).size()))
                .toList();
        int savedRows = results.stream().mapToInt(DealFetchResult::savedCount).sum();
        log.info("공공데이터 4개 API 통합 수집 종료. lawdCd={}, dealYmd={}, apiCount={}, savedRows={}, detail={}",
                lawdCd, dealYmd, results.size(), savedRows, results);
        return results;
    }

    public List<PropertyDeal> search(String dealType, String lawdCd, String dong, String houseName, String dealYmd) {
        List<PropertyDeal> deals = dealMapper.search(blankToNull(dealType), blankToNull(lawdCd), blankToNull(dong), blankToNull(houseName), blankToNull(dealYmd));
        log.info("실거래 DB 검색 완료. dealType={}, lawdCd={}, dong={}, houseName={}, dealYmd={}, resultCount={}",
                dealType, lawdCd, dong, houseName, dealYmd, deals.size());
        return deals;
    }

    public List<DealSummary> summarize(String lawdCd, String dealYmd) {
        validateSearch(lawdCd, dealYmd);
        List<DealSummary> summaries = dealMapper.summarize(lawdCd, dealYmd);
        log.info("실거래 월간 시세 요약 조회 완료. lawdCd={}, dealYmd={}, summaryCount={}", lawdCd, dealYmd, summaries.size());
        return summaries;
    }

    public int countByMonth(DealType type, String lawdCd, String dealYmd) {
        return dealMapper.countByTypeAndMonth(type.name(), lawdCd, dealYmd);
    }

    private List<PropertyDeal> parse(DealType type, String lawdCd, String xml) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xml)));
            String resultCode = text(document.getDocumentElement(), "resultCode");
            if (!"000".equals(resultCode) && !"00".equals(resultCode)) {
                throw new IllegalArgumentException("공공데이터 API 오류: " + resultCode + " " + text(document.getDocumentElement(), "resultMsg"));
            }
            NodeList items = document.getElementsByTagName("item");
            List<PropertyDeal> deals = java.util.stream.IntStream.range(0, items.getLength())
                    .mapToObj(i -> (Element) items.item(i))
                    .map(item -> toDeal(type, lawdCd, item))
                    .toList();
            log.info("공공데이터 XML 파싱 완료. type={}, lawdCd={}, itemCount={}", type, lawdCd, deals.size());
            return deals;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("실거래가 XML 응답을 해석하지 못했습니다.", e);
        }
    }

    private PropertyDeal toDeal(DealType type, String lawdCd, Element item) {
        PropertyDeal deal = new PropertyDeal();
        deal.setDealType(type.name());
        deal.setLawdCd(firstText(item, "sggCd", null, lawdCd));
        deal.setUmdNm(text(item, "umdNm"));
        deal.setHouseName(firstText(item, "aptNm", "mhouseNm", null));
        deal.setHouseType(firstText(item, "houseType", null, type.name().startsWith("APT") ? "아파트" : "연립다세대"));
        deal.setJibun(text(item, "jibun"));
        deal.setRoadName(text(item, "roadNm"));
        deal.setBuildYear(toInteger(text(item, "buildYear")));
        deal.setExclusiveArea(toDecimal(text(item, "excluUseAr")));
        deal.setLandArea(toDecimal(text(item, "landAr")));
        deal.setDealYear(toInteger(text(item, "dealYear")));
        deal.setDealMonth(toInteger(text(item, "dealMonth")));
        deal.setDealDay(toInteger(text(item, "dealDay")));
        deal.setDealAmount(toLong(text(item, "dealAmount")));
        deal.setDeposit(toLong(text(item, "deposit")));
        deal.setMonthlyRent(toLong(text(item, "monthlyRent")));
        deal.setFloor(text(item, "floor"));
        deal.setDealGbn(text(item, "dealingGbn"));
        deal.setRawXml(elementToString(item));
        return deal;
    }

    private static void validateSearch(String lawdCd, String dealYmd) {
        if (lawdCd == null || !lawdCd.matches("\\d{5}")) {
            throw new IllegalArgumentException("LAWD_CD는 법정동 코드 앞 5자리여야 합니다.");
        }
        if (dealYmd == null || !dealYmd.matches("\\d{6}")) {
            throw new IllegalArgumentException("DEAL_YMD는 yyyyMM 형식이어야 합니다.");
        }
    }

    private static String text(Element element, String tag) {
        NodeList nodes = element.getElementsByTagName(tag);
        if (nodes.getLength() == 0 || nodes.item(0).getTextContent() == null) {
            return null;
        }
        String value = nodes.item(0).getTextContent().trim();
        return value.isBlank() ? null : value;
    }

    private static String firstText(Element element, String first, String second, String fallback) {
        String value = text(element, first);
        if (value == null && second != null) {
            value = text(element, second);
        }
        return value == null ? fallback : value;
    }

    private static Integer toInteger(String value) {
        return value == null ? null : Integer.valueOf(value.replace(",", "").trim());
    }

    private static Long toLong(String value) {
        return value == null ? null : Long.valueOf(value.replace(",", "").trim());
    }

    private static BigDecimal toDecimal(String value) {
        return value == null ? null : new BigDecimal(value.replace(",", "").trim());
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private static String elementToString(Element element) {
        try {
            var transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(element), new StreamResult(writer));
            return new String(writer.toString().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }
}
