package com.base.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.system.entity.Enum;
import org.apache.ibatis.annotations.Mapper;

/**
 * 枚举 Mapper 接口
 */
@Mapper
public interface EnumMapper extends BaseMapper<Enum> {
}
