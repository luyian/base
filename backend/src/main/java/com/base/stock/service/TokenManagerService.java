package com.base.stock.service;

import com.base.stock.entity.ApiToken;

import java.util.List;

/**
 * Token 管理服务接口
 *
 * @author base
 */
public interface TokenManagerService {

    /**
     * 获取下一个可用 Token（返回 Token 值）
     *
     * @param provider 服务商
     * @return Token 值
     */
    String getNextToken(String provider);

    /**
     * 获取下一个可用 Token（返回 Token 实体）
     *
     * @param provider 服务商
     * @return Token 实体
     */
    ApiToken getNextTokenEntity(String provider);

    /**
     * 标记 Token 已使用
     *
     * @param tokenId Token ID
     */
    void markTokenUsed(Long tokenId);

    /**
     * 记录 Token 请求失败
     * 失败次数超过3次自动作废
     *
     * @param tokenId Token ID
     */
    void recordTokenFailure(Long tokenId);

    /**
     * 重置 Token 失败计数（请求成功时调用）
     *
     * @param tokenId Token ID
     */
    void resetTokenFailure(Long tokenId);

    /**
     * 作废 Token
     *
     * @param tokenId Token ID
     */
    void disableToken(Long tokenId);

    /**
     * 启用 Token
     *
     * @param tokenId Token ID
     */
    void enableToken(Long tokenId);

    /**
     * 添加 Token
     *
     * @param apiToken Token 实体
     * @return Token ID
     */
    Long addToken(ApiToken apiToken);

    /**
     * 更新 Token
     *
     * @param apiToken Token 实体
     */
    void updateToken(ApiToken apiToken);

    /**
     * 删除 Token
     *
     * @param tokenId Token ID
     */
    void deleteToken(Long tokenId);

    /**
     * 根据 ID 获取 Token
     *
     * @param tokenId Token ID
     * @return Token 实体
     */
    ApiToken getTokenById(Long tokenId);

    /**
     * 查询 Token 列表
     *
     * @param provider 服务商（可选）
     * @param status   状态（可选）
     * @return Token 列表
     */
    List<ApiToken> listTokens(String provider, Integer status);

    /**
     * 重置每日计数
     *
     * @param provider 服务商
     */
    void resetDailyCount(String provider);

    /**
     * 获取所有可用的 Token 列表
     * 用于并发HTTP执行器初始化Token池
     *
     * @param provider 服务商
     * @return 可用的 Token 列表
     */
    List<ApiToken> getAvailableTokens(String provider);
}
