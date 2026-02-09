package com.base.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.stock.entity.ApiToken;
import com.base.stock.mapper.ApiTokenMapper;
import com.base.stock.service.TokenManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Token 管理服务实现类
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenManagerServiceImpl implements TokenManagerService {

    @Autowired
    private final ApiTokenMapper apiTokenMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getNextToken(String provider) {
        ApiToken token = getNextTokenEntity(provider);
        return token.getTokenValue();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiToken getNextTokenEntity(String provider) {
        // 查询可用的 Token 列表
        LambdaQueryWrapper<ApiToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApiToken::getProvider, provider)
                .eq(ApiToken::getStatus, 1)
                .eq(ApiToken::getDeleted, 0)
                // 未过期或无过期时间
                .and(w -> w.isNull(ApiToken::getExpireTime)
                        .or()
                        .gt(ApiToken::getExpireTime, LocalDateTime.now()))
                // 按最后使用时间升序，优先使用最久未用的
                .orderByAsc(ApiToken::getLastUsedTime);

        List<ApiToken> tokens = apiTokenMapper.selectList(wrapper);

        if (tokens.isEmpty()) {
            log.error("没有可用的 Token，provider: {}", provider);
            throw new RuntimeException("没有可用的 Token");
        }

        // 遍历找到未超限的 Token
        for (ApiToken token : tokens) {
            // 检查每日限额
            if (token.getDailyLimit() > 0 && token.getDailyUsed() >= token.getDailyLimit()) {
                continue;
            }
            // 找到可用 Token，更新使用记录
            token.setLastUsedTime(LocalDateTime.now());
            token.setUseCount(token.getUseCount() + 1);
            token.setDailyUsed(token.getDailyUsed() + 1);
            apiTokenMapper.updateById(token);

            log.debug("获取 Token 成功，tokenId: {}, provider: {}", token.getId(), provider);
            return token;
        }

        log.error("所有 Token 已达到每日限额，provider: {}", provider);
        throw new RuntimeException("所有 Token 已达到每日限额");
    }

    @Override
    public void markTokenUsed(Long tokenId) {
        ApiToken token = apiTokenMapper.selectById(tokenId);
        if (token != null) {
            token.setLastUsedTime(LocalDateTime.now());
            token.setUseCount(token.getUseCount() + 1);
            token.setDailyUsed(token.getDailyUsed() + 1);
            apiTokenMapper.updateById(token);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordTokenFailure(Long tokenId) {
        ApiToken token = apiTokenMapper.selectById(tokenId);
        if (token != null) {
            int failCount = (token.getFailCount() == null ? 0 : token.getFailCount()) + 1;
            token.setFailCount(failCount);

            // 失败次数超过3次，自动作废
            if (failCount >= 3) {
                token.setStatus(0);
                log.warn("Token 连续失败 {} 次，已自动作废，tokenId: {}", failCount, tokenId);
            } else {
                log.warn("Token 请求失败，当前失败次数: {}，tokenId: {}", failCount, tokenId);
            }

            apiTokenMapper.updateById(token);
        }
    }

    @Override
    public void resetTokenFailure(Long tokenId) {
        ApiToken token = apiTokenMapper.selectById(tokenId);
        if (token != null && token.getFailCount() != null && token.getFailCount() > 0) {
            token.setFailCount(0);
            apiTokenMapper.updateById(token);
            log.debug("Token 失败计数已重置，tokenId: {}", tokenId);
        }
    }

    @Override
    public void disableToken(Long tokenId) {
        ApiToken token = apiTokenMapper.selectById(tokenId);
        if (token != null) {
            token.setStatus(0);
            apiTokenMapper.updateById(token);
            log.info("Token 已作废，tokenId: {}", tokenId);
        }
    }

    @Override
    public void enableToken(Long tokenId) {
        ApiToken token = apiTokenMapper.selectById(tokenId);
        if (token != null) {
            token.setStatus(1);
            apiTokenMapper.updateById(token);
            log.info("Token 已启用，tokenId: {}", tokenId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addToken(ApiToken apiToken) {
        if (apiToken.getStatus() == null) {
            apiToken.setStatus(1);
        }
        if (apiToken.getUseCount() == null) {
            apiToken.setUseCount(0);
        }
        if (apiToken.getDailyUsed() == null) {
            apiToken.setDailyUsed(0);
        }
        if (apiToken.getDailyLimit() == null) {
            apiToken.setDailyLimit(0);
        }
        if (apiToken.getFailCount() == null) {
            apiToken.setFailCount(0);
        }
        apiTokenMapper.insert(apiToken);
        log.info("Token 添加成功，tokenId: {}", apiToken.getId());
        return apiToken.getId();
    }

    @Override
    public void updateToken(ApiToken apiToken) {
        apiTokenMapper.updateById(apiToken);
        log.info("Token 更新成功，tokenId: {}", apiToken.getId());
    }

    @Override
    public void deleteToken(Long tokenId) {
        apiTokenMapper.deleteById(tokenId);
        log.info("Token 删除成功，tokenId: {}", tokenId);
    }

    @Override
    public ApiToken getTokenById(Long tokenId) {
        return apiTokenMapper.selectById(tokenId);
    }

    @Override
    public List<ApiToken> listTokens(String provider, Integer status) {
        LambdaQueryWrapper<ApiToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(provider != null, ApiToken::getProvider, provider)
                .eq(status != null, ApiToken::getStatus, status)
                .orderByDesc(ApiToken::getCreateTime);
        return apiTokenMapper.selectList(wrapper);
    }

    @Override
    public void resetDailyCount(String provider) {
        int count = apiTokenMapper.resetDailyUsed(provider);
        log.info("重置每日计数完成，provider: {}, 影响行数: {}", provider, count);
    }

    @Override
    public List<ApiToken> getAvailableTokens(String provider) {
        // 查询可用的 Token 列表
        LambdaQueryWrapper<ApiToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApiToken::getProvider, provider)
                .eq(ApiToken::getStatus, 1)
                .eq(ApiToken::getDeleted, 0)
                // 未过期或无过期时间
                .and(w -> w.isNull(ApiToken::getExpireTime)
                        .or()
                        .gt(ApiToken::getExpireTime, LocalDateTime.now()));

        List<ApiToken> tokens = apiTokenMapper.selectList(wrapper);

        // 过滤掉已达每日限额的 Token
        List<ApiToken> availableTokens = tokens.stream()
                .filter(token -> token.getDailyLimit() == 0 || token.getDailyUsed() < token.getDailyLimit())
                .collect(java.util.stream.Collectors.toList());

        log.info("获取可用 Token 列表，provider: {}, 总数: {}, 可用: {}",
                provider, tokens.size(), availableTokens.size());

        return availableTokens;
    }
}
