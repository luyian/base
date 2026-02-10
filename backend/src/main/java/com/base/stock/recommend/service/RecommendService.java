package com.base.stock.recommend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.base.stock.recommend.dto.RecommendQueryRequest;
import com.base.stock.recommend.entity.RecommendStock;

import java.time.LocalDate;
import java.util.List;

/**
 * 推荐股票服务接口
 *
 * @author base
 */
public interface RecommendService extends IService<RecommendStock> {

    /**
     * 分页查询推荐股票列表
     *
     * @param request 查询请求参数
     * @return 分页结果
     */
    Page<RecommendStock> pageRecommend(RecommendQueryRequest request);

    /**
     * 查询推荐股票列表（不分页）
     *
     * @param recommendDate 推荐日期
     * @param limit         限制数量
     * @return 推荐列表
     */
    List<RecommendStock> listRecommend(LocalDate recommendDate, Integer limit);

    /**
     * 根据股票代码和日期查询推荐记录
     *
     * @param stockCode     股票代码
     * @param recommendDate 推荐日期
     * @return 推荐记录
     */
    RecommendStock getByStockCodeAndDate(String stockCode, LocalDate recommendDate);

    /**
     * 查询最新的推荐日期
     *
     * @return 最新推荐日期
     */
    LocalDate getLatestRecommendDate();
}
