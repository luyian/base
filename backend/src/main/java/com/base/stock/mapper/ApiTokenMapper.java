package com.base.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.stock.entity.ApiToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * API Token Mapper 接口
 *
 * @author base
 */
@Mapper
public interface ApiTokenMapper extends BaseMapper<ApiToken> {

    /**
     * 重置所有 Token 的每日使用次数
     *
     * @param provider 服务商
     * @return 影响行数
     */
    @Update("UPDATE stk_api_token SET daily_used = 0 WHERE provider = #{provider} AND deleted = 0")
    int resetDailyUsed(@Param("provider") String provider);
}
