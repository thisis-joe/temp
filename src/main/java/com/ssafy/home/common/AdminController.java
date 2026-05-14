package com.ssafy.home.common;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private static final Map<String, TableConfig> TABLES = Map.of(
            "members", new TableConfig("members", "회원", List.of("id", "email", "name", "phone", "role", "created_at"), List.of("email", "name", "phone"), "id desc"),
            "property_deals", new TableConfig("property_deals", "수집 실거래", List.of("id", "deal_type", "lawd_cd", "umd_nm", "house_name", "deal_year", "deal_month", "deal_amount", "deposit", "monthly_rent", "created_at"), List.of("deal_type", "lawd_cd", "umd_nm", "house_name"), "id desc"),
            "houseinfos", new TableConfig("houseinfos", "주거 단지", List.of("apt_seq", "sgg_cd", "umd_cd", "umd_nm", "jibun", "road_nm", "apt_nm", "build_year"), List.of("apt_seq", "sgg_cd", "umd_cd", "umd_nm", "road_nm", "apt_nm"), "apt_seq"),
            "housedeals", new TableConfig("housedeals", "단지 거래", List.of("no", "apt_seq", "apt_dong", "floor", "deal_year", "deal_month", "deal_day", "exclu_use_ar", "deal_amount"), List.of("apt_seq", "apt_dong", "deal_amount"), "no desc"),
            "dongcodes", new TableConfig("dongcodes", "법정동 코드", List.of("dong_code", "sido_name", "gugun_name", "dong_name"), List.of("dong_code", "sido_name", "gugun_name", "dong_name"), "dong_code"),
            "favorites", new TableConfig("favorites", "관심지역", List.of("id", "member_id", "sido_nm", "sigungu_nm", "dong_nm", "lawd_cd", "memo", "created_at"), List.of("sido_nm", "sigungu_nm", "dong_nm", "lawd_cd", "memo"), "id desc"),
            "notices", new TableConfig("notices", "공지사항", List.of("id", "title", "writer_id", "view_count", "created_at", "updated_at"), List.of("title", "content"), "id desc")
    );

    private final JdbcTemplate jdbcTemplate;
    private final OperationLogService operationLogService;

    @GetMapping("/overview")
    ApiResponse<AdminOverview> overview() {
        Map<String, Long> counts = new LinkedHashMap<>();
        TABLES.forEach((key, table) -> counts.put(key, countForDashboard(table.name())));

        List<Map<String, Object>> dealTypes = jdbcTemplate.queryForList("""
                select deal_type, count(*) as deal_count
                from property_deals
                group by deal_type
                order by deal_type
                """);
        List<Map<String, Object>> recentDeals = jdbcTemplate.queryForList("""
                select id, deal_type, lawd_cd, umd_nm, house_name, deal_year, deal_month, deal_amount, deposit, monthly_rent, created_at
                from property_deals
                order by id desc
                limit 8
                """);
        String latestDealMonth = jdbcTemplate.queryForObject("""
                select coalesce(max(concat(deal_year, lpad(deal_month, 2, '0'))), '-') from property_deals
                """, String.class);

        AdminOverview overview = new AdminOverview(
                counts,
                dealTypes,
                recentDeals,
                operationLogService.find(null).stream().limit(8).toList(),
                latestDealMonth
        );
        return ApiResponse.ok(overview);
    }

    @GetMapping("/tables")
    ApiResponse<List<TableMeta>> tables() {
        return ApiResponse.ok(TABLES.entrySet().stream()
                .map(entry -> new TableMeta(entry.getKey(), entry.getValue().label(), entry.getValue().columns()))
                .toList());
    }

    @GetMapping("/rows")
    ApiResponse<TableResult> rows(
            @RequestParam(defaultValue = "members") String table,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "20") int size
    ) {
        TableConfig config = TABLES.getOrDefault(table, TABLES.get("members"));
        int limit = Math.max(5, Math.min(size, 100));
        WhereClause where = whereClause(config, keyword);

        Long total = jdbcTemplate.queryForObject("select count(*) from " + config.name() + where.sql(), Long.class, where.args().toArray());
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "select " + String.join(", ", config.columns()) + " from " + config.name() + where.sql() + " order by " + config.orderBy() + " limit ?",
                append(where.args(), limit).toArray()
        );

        return ApiResponse.ok(new TableResult(config.label(), config.columns(), total == null ? 0 : total, rows));
    }

    private static WhereClause whereClause(TableConfig config, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return new WhereClause("", List.of());
        }
        List<String> clauses = new ArrayList<>();
        List<Object> args = new ArrayList<>();
        for (String column : config.searchColumns()) {
            clauses.add("lower(coalesce(cast(" + column + " as char), '')) like ?");
            args.add("%" + keyword.toLowerCase() + "%");
        }
        return new WhereClause(" where " + String.join(" or ", clauses), args);
    }

    private long count(String sql) {
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count == null ? 0 : count;
    }

    private long countForDashboard(String tableName) {
        if ("housedeals".equals(tableName)) {
            Long count = jdbcTemplate.queryForObject("""
                    select coalesce(table_rows, 0)
                    from information_schema.tables
                    where table_schema = database()
                      and table_name = ?
                    """, Long.class, tableName);
            return count == null ? 0 : count;
        }
        return count("select count(*) from " + tableName);
    }

    private static List<Object> append(List<Object> values, Object value) {
        List<Object> result = new ArrayList<>(values);
        result.add(value);
        return result;
    }

    public record AdminOverview(
            Map<String, Long> counts,
            List<Map<String, Object>> dealTypes,
            List<Map<String, Object>> recentDeals,
            List<OperationLog> recentLogs,
            String latestDealMonth
    ) {
    }

    public record TableMeta(String key, String label, List<String> columns) {
    }

    public record TableResult(String label, List<String> columns, long total, List<Map<String, Object>> rows) {
    }

    private record TableConfig(String name, String label, List<String> columns, List<String> searchColumns, String orderBy) {
    }

    private record WhereClause(String sql, List<Object> args) {
    }
}
