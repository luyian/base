package com.base.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.stock.entity.Watchlist;
import org.apache.ibatis.annotations.Mapper;

/**
 * 自选股票 Mapper 接口
 *
 * @author base
 */
@Mapper
public interface WatchlistMapper extends BaseMapper<Watchlist> {
}
