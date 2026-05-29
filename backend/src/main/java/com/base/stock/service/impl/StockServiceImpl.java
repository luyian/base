package com.base.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.common.exception.BusinessException;

import java.util.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.util.ChineseConvertUtil;
import com.base.stock.dto.StockQueryRequest;
import com.base.stock.entity.StockInfo;
import com.base.stock.entity.StockKline;
import com.base.stock.mapper.StockInfoMapper;
import com.base.stock.mapper.StockKlineMapper;
import com.base.stock.service.StockService;
import com.base.stock.service.StockSyncService;
import com.base.system.dto.enums.EnumResponse;
import com.base.system.service.EnumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 股票查询服务实现类
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockInfoMapper stockInfoMapper;
    private final StockKlineMapper stockKlineMapper;
    private final StockSyncService stockSyncService;
    private final EnumService enumService;

    @Override
    public Page<StockInfo> pageStocks(StockQueryRequest request) {
        Page<StockInfo> pageParam = request.buildPage();
        LambdaQueryWrapper<StockInfo> wrapper = new LambdaQueryWrapper<>();

        String market = request.getMarket();
        String industry = request.getIndustry();
        String keyword = request.getKeyword();

        String stockType = request.getStockType();

        wrapper.eq(StockInfo::getDeleted, 0)
                .eq(market != null && !market.isEmpty(), StockInfo::getMarket, market)
                .eq(industry != null && !industry.isEmpty(), StockInfo::getIndustry, industry)
                .eq(stockType != null && !stockType.isEmpty(), StockInfo::getStockType, stockType);

        // 支持简繁体互查
        if (keyword != null && !keyword.isEmpty()) {
            String traditional = ChineseConvertUtil.toTraditional(keyword);
            String simplified = ChineseConvertUtil.toSimplified(keyword);

            wrapper.and(w -> {
                // 按股票代码查询
                w.like(StockInfo::getStockCode, keyword);
                // 按原始关键字查询股票名称
                w.or().like(StockInfo::getStockName, keyword);
                // 按繁体关键字查询（如果与原始不同）
                if (!traditional.equals(keyword)) {
                    w.or().like(StockInfo::getStockName, traditional);
                }
                // 按简体关键字查询（如果与原始不同）
                if (!simplified.equals(keyword)) {
                    w.or().like(StockInfo::getStockName, simplified);
                }
            });
        }

        wrapper.orderByAsc(StockInfo::getStockCode);
        Page<StockInfo> result = stockInfoMapper.selectPage(pageParam, wrapper);

        // 填充行业中文名称
        fillIndustryCn(result.getRecords());

        return result;
    }

    @Override
    public StockInfo getByStockCode(String stockCode) {
        LambdaQueryWrapper<StockInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StockInfo::getStockCode, stockCode)
                .eq(StockInfo::getDeleted, 0);
        return stockInfoMapper.selectOne(wrapper);
    }

    @Override
    public List<StockKline> listKlineData(String stockCode, LocalDate startDate, LocalDate endDate) {
        // 异步更新股票详情信息
        asyncUpdateStockInfo(stockCode);

        LambdaQueryWrapper<StockKline> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StockKline::getStockCode, stockCode)
                .ge(startDate != null, StockKline::getTradeDate, startDate)
                .le(endDate != null, StockKline::getTradeDate, endDate)
                .orderByAsc(StockKline::getTradeDate);
        return stockKlineMapper.selectList(wrapper);
    }

    /**
     * 异步更新股票详情信息
     *
     * @param stockCode 股票代码
     */
    @Async
    public void asyncUpdateStockInfo(String stockCode) {
        try {
            log.debug("异步更新股票详情，stockCode: {}", stockCode);
            stockSyncService.syncStockInfo(stockCode);
        } catch (Exception e) {
            log.warn("异步更新股票详情失败，stockCode: {}, error: {}", stockCode, e.getMessage());
        }
    }

    @Override
    public List<Map<String, String>> listIndustryOptions() {
        List<EnumResponse> enumList = enumService.listByType("stock_industry");
        return enumList.stream().map(item -> {
            Map<String, String> option = new HashMap<>(2);
            option.put("value", item.getEnumCode());
            option.put("label", item.getEnumValue());
            return option;
        }).collect(Collectors.toList());
    }

    @Override
    public List<StockInfo> searchStocks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return stockInfoMapper.selectList(
            new QueryWrapper<StockInfo>()
                .like("stock_code", keyword.trim())
                .or()
                .like("stock_name", keyword.trim())
                .last("limit 20")
        );
    }

    @Override
    public StockInfo createStock(StockInfo stockInfo) {
        // 检查股票是否已存在
        StockInfo existing = stockInfoMapper.selectById(stockInfo.getStockCode());
        if (existing != null) {
            throw new BusinessException("股票代码已存在");
        }
        // 设置默认值
        if (stockInfo.getMarket() == null) {
            String code = stockInfo.getStockCode();
            if (code != null) {
                if (code.startsWith("6")) {
                    stockInfo.setMarket("SH");
                } else if (code.startsWith("0") || code.startsWith("3")) {
                    stockInfo.setMarket("SZ");
                } else if (code.startsWith("HK") || code.startsWith("0")) {
                    stockInfo.setMarket("HK");
                }
            }
        }
        stockInfoMapper.insert(stockInfo);
        return stockInfo;
    }

    @Override
    public StockInfo updateStock(String stockCode, StockInfo stockInfo) {
        StockInfo existing = getByStockCode(stockCode);
        if (existing == null) {
            throw new BusinessException("股票不存在");
        }
        existing.setStockName(stockInfo.getStockName());
        if (stockInfo.getStockCode() != null && !stockInfo.getStockCode().equals(stockCode)) {
            // 检查新股票代码是否已存在
            StockInfo duplicate = getByStockCode(stockInfo.getStockCode());
            if (duplicate != null) {
                throw new BusinessException("股票代码已存在");
            }
            existing.setStockCode(stockInfo.getStockCode());
        }
        if (stockInfo.getMarket() != null) {
            existing.setMarket(stockInfo.getMarket());
        }
        if (stockInfo.getIndustry() != null) {
            existing.setIndustry(stockInfo.getIndustry());
        }
        stockInfoMapper.updateById(existing);
        return existing;
    }

    /**
     * 获取行业中英文映射
     *
     * @return 英文代码 -> 中文名称
     */
    private Map<String, String> getIndustryMap() {
        List<EnumResponse> enumList = enumService.listByType("stock_industry");
        return enumList.stream()
                .collect(Collectors.toMap(EnumResponse::getEnumCode, EnumResponse::getEnumValue, (v1, v2) -> v1));
    }

    /**
     * 填充行业中文名称
     *
     * @param stockList 股票列表
     */
    private void fillIndustryCn(List<StockInfo> stockList) {
        if (stockList == null || stockList.isEmpty()) {
            return;
        }
        Map<String, String> industryMap = getIndustryMap();
        for (StockInfo stock : stockList) {
            if (stock.getIndustry() != null) {
                stock.setIndustryCn(industryMap.getOrDefault(stock.getIndustry(), stock.getIndustry()));
            }
        }
    }
}
