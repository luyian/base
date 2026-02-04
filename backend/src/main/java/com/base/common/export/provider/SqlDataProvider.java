package com.base.common.export.provider;

import com.base.common.export.constant.DataSourceTypeEnum;
import com.base.common.export.engine.ExportContext;
import com.base.system.export.entity.ExportConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL 数据提供者
 * 通过自定义 SQL 获取数据
 *
 * @author base
 * @since 2026-02-04
 */
@Component
public class SqlDataProvider implements ExportDataProvider {

    private static final Logger log = LoggerFactory.getLogger(SqlDataProvider.class);

    private static final Pattern COUNT_PATTERN = Pattern.compile(
            "SELECT\\s+.*?\\s+FROM",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public long count(ExportContext context) {
        ExportConfig config = context.getConfig();
        String sql = config.getDataSourceSql();
        if (!StringUtils.hasText(sql)) {
            return 0;
        }

        try {
            // 将原 SQL 转换为 count SQL
            String countSql = buildCountSql(sql);
            Long count = jdbcTemplate.queryForObject(countSql, Long.class);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("执行 count SQL 失败", e);
            return 0;
        }
    }

    @Override
    public List<Map<String, Object>> fetchData(ExportContext context, long offset, int limit) {
        ExportConfig config = context.getConfig();
        String sql = config.getDataSourceSql();
        if (!StringUtils.hasText(sql)) {
            return Collections.emptyList();
        }

        try {
            // 添加分页
            String pageSql = buildPageSql(sql, offset, limit);
            return jdbcTemplate.queryForList(pageSql);
        } catch (Exception e) {
            log.error("执行分页 SQL 失败", e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean supports(String dataSourceType) {
        return DataSourceTypeEnum.SQL.getCode().equals(dataSourceType);
    }

    /**
     * 构建 count SQL
     */
    private String buildCountSql(String sql) {
        // 移除 ORDER BY 子句
        String cleanSql = removeOrderBy(sql);
        // 替换 SELECT ... FROM 为 SELECT COUNT(*) FROM
        Matcher matcher = COUNT_PATTERN.matcher(cleanSql);
        if (matcher.find()) {
            return matcher.replaceFirst("SELECT COUNT(*) FROM");
        }
        return "SELECT COUNT(*) FROM (" + cleanSql + ") t";
    }

    /**
     * 构建分页 SQL
     */
    private String buildPageSql(String sql, long offset, int limit) {
        return sql + " LIMIT " + offset + ", " + limit;
    }

    /**
     * 移除 ORDER BY 子句
     */
    private String removeOrderBy(String sql) {
        return sql.replaceAll("(?i)\\s+ORDER\\s+BY\\s+[\\w\\s,\\.]+$", "");
    }
}
