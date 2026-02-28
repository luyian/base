package com.base.stock.fund.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.stock.fund.entity.FundWatchlist;
import org.apache.ibatis.annotations.Mapper;

/**
 * 基金自选 Mapper 接口
 *
 * @author base
 */
@Mapper
public interface FundWatchlistMapper extends BaseMapper<FundWatchlist> {
}
