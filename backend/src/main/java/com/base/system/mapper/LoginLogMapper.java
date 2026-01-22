package com.base.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.system.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志 Mapper 接口
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {
}
