package com.base.common.export.provider;

import com.base.common.export.engine.ExportContext;

import java.util.List;
import java.util.Map;

/**
 * 导出数据提供者接口
 *
 * @author base
 * @since 2026-02-04
 */
public interface ExportDataProvider {

    /**
     * 获取数据总数
     *
     * @param context 导出上下文
     * @return 数据总数
     */
    long count(ExportContext context);

    /**
     * 分批获取数据
     *
     * @param context 导出上下文
     * @param offset  偏移量
     * @param limit   每批数量
     * @return 数据列表
     */
    List<Map<String, Object>> fetchData(ExportContext context, long offset, int limit);

    /**
     * 是否支持该数据源类型
     *
     * @param dataSourceType 数据源类型
     * @return 是否支持
     */
    boolean supports(String dataSourceType);
}
