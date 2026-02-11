package com.base.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.message.entity.Subscription;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息订阅 Mapper 接口
 *
 * @author base
 */
@Mapper
public interface SubscriptionMapper extends BaseMapper<Subscription> {
}
