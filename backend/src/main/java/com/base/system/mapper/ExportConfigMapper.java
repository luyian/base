package com.base.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.system.export.entity.ExportConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 导出配置 Mapper 接口
 *
 * @author base
 * @since 2026-02-04
 */
@Mapper
public interface ExportConfigMapper extends BaseMapper<ExportConfig> {
}
