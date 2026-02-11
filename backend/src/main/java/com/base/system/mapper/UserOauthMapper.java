package com.base.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.system.entity.UserOauth;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户第三方登录绑定 Mapper 接口
 */
@Mapper
public interface UserOauthMapper extends BaseMapper<UserOauth> {
}
