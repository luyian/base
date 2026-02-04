package com.base.common.export.converter;

import com.base.common.export.engine.ExportContext;
import com.base.system.export.entity.ExportField;

/**
 * 数据转换器接口
 *
 * @author base
 * @since 2026-02-04
 */
public interface DataConverter {

    /**
     * 转换数据
     *
     * @param value   原始值
     * @param field   字段配置
     * @param context 导出上下文
     * @return 转换后的值
     */
    Object convert(Object value, ExportField field, ExportContext context);

    /**
     * 获取转换器名称
     *
     * @return 转换器名称
     */
    String getName();
}
