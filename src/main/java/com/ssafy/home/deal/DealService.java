package com.ssafy.home.deal;

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
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@EnableConfigurationProperties(PublicDataProperties.class)
public class DealService {
    private final DealMapper dealMapper;
    private final RestClient restClient;
    private final PublicDataProperties properties;

    public DealService(DealMapper dealMapper, RestClient restClient, PublicDataProperties properties) {
        this.dealMapper = dealMapper;
        this.restClient = restClient;
        this.properties = properties;
    }

    @Transactional
    public List<PropertyDeal> fetchAndSave(DealType type, String lawdCd, String dealYmd, int numOfRows) {
        validateSearch(lawdCd, dealYmd);
        // UriComponentsBuilder를 사용하면 파라미터가 많아져도 URL을 안전하게 조립할 수 있다.
        String url = UriComponentsBuilder.fromHttpUrl(type.url())
                .queryParam("serviceKey", properties.serviceKey())
                .queryParam("LAWD_CD", lawdCd)
                .queryParam("DEAL_YMD", dealYmd)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", Math.max(1, Math.min(numOfRows, 500)))
                .build(false)
                .toUriString();
        String xml = restClient.get().uri(url).retrieve().body(String.class);
        List<PropertyDeal> deals = parse(type, lawdCd, xml);
        // MyBatis Mapper를 통해 파싱된 데이터를 한 건씩 insert한다.
        deals.forEach(dealMapper::insert);
        return deals;
    }

    public List<PropertyDeal> search(String dealType, String lawdCd, String dong, String houseName, String dealYmd) {
        return dealMapper.search(blankToNull(dealType), blankToNull(lawdCd), blankToNull(dong), blankToNull(houseName), blankToNull(dealYmd));
    }

    public int countByMonth(DealType type, String lawdCd, String dealYmd) {
        return dealMapper.countByTypeAndMonth(type.name(), lawdCd, dealYmd);
    }

    private List<PropertyDeal> parse(DealType type, String lawdCd, String xml) {
        try {
            // 공공데이터 응답은 XML이므로 DOM 파서로 item 태그 목록을 읽는다.
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xml)));
            String resultCode = text(document.getDocumentElement(), "resultCode");
            if (!"000".equals(resultCode) && !"00".equals(resultCode)) {
                throw new IllegalArgumentException("공공데이터 API 오류: " + resultCode + " " + text(document.getDocumentElement(), "resultMsg"));
            }
            NodeList items = document.getElementsByTagName("item");
            return java.util.stream.IntStream.range(0, items.getLength())
                    .mapToObj(i -> (Element) items.item(i))
                    .map(item -> toDeal(type, lawdCd, item))
                    .toList();
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("실거래가 XML 응답을 해석하지 못했습니다.", e);
        }
    }

    private PropertyDeal toDeal(DealType type, String lawdCd, Element item) {
        PropertyDeal deal = new PropertyDeal();
        // API마다 태그명이 조금씩 달라서 공통 도메인(PropertyDeal)에 맞춰 변환한다.
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
            throw new IllegalArgumentException("LAWD_CD는 법정동코드 앞 5자리여야 합니다.");
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
