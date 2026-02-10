package com.base.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.system.export.entity.ExportField;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 导出字段配置 Mapper 接口
 *
 * @author base
 * @since 2026-02-04
 */
@Mapper
public interface ExportFieldMapper extends BaseMapper<ExportField> {

    /**
     * 批量插入导出字段配置
     *
     * @param list 字段配置列表
     * @return 插入数量
     */
    int batchInsert(@Param("list") List<ExportField> list);
}
