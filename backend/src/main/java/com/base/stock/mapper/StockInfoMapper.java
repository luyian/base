package com.base.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.stock.entity.StockInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 股票基础信息 Mapper 接口
 *
 * @author base
 */
@Mapper
public interface StockInfoMapper extends BaseMapper<StockInfo> {

    /**
     * 批量插入或更新股票信息
     * 使用 INSERT ... ON DUPLICATE KEY UPDATE 实现 upsert
     *
     * @param list 股票信息列表
     * @return 影响行数
     */
    int batchUpsert(@Param("list") List<StockInfo> list);
}
