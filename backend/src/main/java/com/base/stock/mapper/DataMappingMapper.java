package com.base.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.stock.entity.DataMapping;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据映射配置 Mapper 接口
 *
 * @author base
 */
@Mapper
public interface DataMappingMapper extends BaseMapper<DataMapping> {
}
