package com.base.stock.recommend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.base.stock.recommend.entity.RecommendStock;
import com.base.stock.recommend.mapper.RecommendStockMapper;
import com.base.stock.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 推荐股票服务实现类
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendServiceImpl extends ServiceImpl<RecommendStockMapper, RecommendStock> implements RecommendService {

    @Override
    public Page<RecommendStock> pageRecommend(LocalDate recommendDate, int page, int size) {
        Page<RecommendStock> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<RecommendStock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecommendStock::getRecommendDate, recommendDate)
                .orderByDesc(RecommendStock::getTotalScore);
        return this.page(pageParam, wrapper);
    }

    @Override
    public List<RecommendStock> listRecommend(LocalDate recommendDate, Integer limit) {
        LambdaQueryWrapper<RecommendStock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecommendStock::getRecommendDate, recommendDate)
                .orderByDesc(RecommendStock::getTotalScore);
        if (limit != null && limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
        return this.list(wrapper);
    }

    @Override
    public RecommendStock getByStockCodeAndDate(String stockCode, LocalDate recommendDate) {
        return this.getOne(
                new LambdaQueryWrapper<RecommendStock>()
                        .eq(RecommendStock::getStockCode, stockCode)
                        .eq(RecommendStock::getRecommendDate, recommendDate)
        );
    }

    @Override
    public LocalDate getLatestRecommendDate() {
        RecommendStock latest = this.getOne(
                new LambdaQueryWrapper<RecommendStock>()
                        .orderByDesc(RecommendStock::getRecommendDate)
                        .last("LIMIT 1")
        );
        return latest != null ? latest.getRecommendDate() : null;
    }
}
