package com.base.stock.fund.service;

import com.base.stock.fund.dto.FundConfigRequest;
import com.base.stock.fund.dto.FundValuationResponse;
import com.base.stock.fund.entity.FundConfig;

import java.util.List;

/**
 * 基金服务接口
 *
 * @author base
 */
public interface FundService {

    /**
     * 查询当前用户的基金列表
     *
     * @return 基金列表
     */
    List<FundConfig> listFunds();

    /**
     * 根据ID查询基金详情（包含持仓）
     *
     * @param id 基金ID
     * @return 基金详情
     */
    FundConfig getFundById(Long id);

    /**
     * 创建基金
     *
     * @param request 基金配置请求
     * @return 基金ID
     */
    Long createFund(FundConfigRequest request);

    /**
     * 更新基金
     *
     * @param id      基金ID
     * @param request 基金配置请求
     */
    void updateFund(Long id, FundConfigRequest request);

    /**
     * 删除基金
     *
     * @param id 基金ID
     */
    void deleteFund(Long id);

    /**
     * 获取单个基金的实时估值
     *
     * @param fundId 基金ID
     * @return 估值响应
     */
    FundValuationResponse getValuation(Long fundId);

    /**
     * 批量获取基金的实时估值
     *
     * @param fundIds 基金ID列表
     * @return 估值响应列表
     */
    List<FundValuationResponse> batchGetValuation(List<Long> fundIds);

    /**
     * 获取当前用户所有基金的实时估值
     *
     * @return 估值响应列表
     */
    List<FundValuationResponse> getAllValuation();

    /**
     * 获取单个基金的缓存估值
     *
     * @param fundId 基金ID
     * @return 缓存的估值响应，无缓存返回null
     */
    FundValuationResponse getCachedValuation(Long fundId);

    /**
     * 查询基金列表（带缓存估值）
     *
     * @return 基金估值列表（使用Redis缓存的估值数据）
     */
    List<FundValuationResponse> listFundsWithCachedValuation();
}
