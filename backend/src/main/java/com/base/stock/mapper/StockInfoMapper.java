package com.base.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.stock.entity.StockInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 股票基础信息 Mapper 接口
 *
 * @author base
 */
@Mapper
public interface StockInfoMapper extends BaseMapper<StockInfo> {
}
