package com.base.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.ai.entity.SysAiConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 大模型配置 Mapper
 *
 * @author base
 * @since 2026-03-17
 */
@Mapper
public interface SysAiConfigMapper extends BaseMapper<SysAiConfig> {
}
