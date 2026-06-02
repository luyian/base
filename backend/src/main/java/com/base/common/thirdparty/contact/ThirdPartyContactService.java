package com.base.common.thirdparty.contact;

import com.base.common.thirdparty.ThirdPartyPlatform;

/**
 * 第三方通讯录同步服务接口
 *
 * @author base
 */
public interface ThirdPartyContactService {

    /**
     * 获取平台标识
     *
     * @return 平台枚举
     */
    ThirdPartyPlatform getPlatform();

    /**
     * 全量同步用户
     *
     * @return 同步的用户数量
     */
    int syncAllUsers();

    /**
     * 根据平台用户ID获取用户信息
     *
     * @param openId 平台用户ID
     * @return 用户信息
     */
    ThirdPartyUser getUserByOpenId(String openId);
}
