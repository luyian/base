package com.base.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.system.entity.Config;
import org.apache.ibatis.annotations.Mapper;

/**
 * 全局变量 Mapper 接口
 */
@Mapper
public interface ConfigMapper extends BaseMapper<Config> {
}
