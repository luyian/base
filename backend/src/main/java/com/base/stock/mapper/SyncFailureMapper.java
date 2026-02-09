package com.base.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.stock.entity.SyncFailure;
import org.apache.ibatis.annotations.Mapper;

/**
 * 股票同步失败记录 Mapper 接口
 *
 * @author base
 */
@Mapper
public interface SyncFailureMapper extends BaseMapper<SyncFailure> {
}
