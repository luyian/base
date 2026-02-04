package com.base.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.system.dto.log.LoginLogQueryRequest;
import com.base.system.dto.log.LoginLogResponse;
import com.base.system.entity.LoginLog;

import java.util.List;

/**
 * 登录日志服务接口
 */
public interface LoginLogService {

    /**
     * 分页查询登录日志列表
     */
    Page<LoginLogResponse> pageLoginLogs(LoginLogQueryRequest request);

    /**
     * 根据ID获取登录日志详情
     */
    LoginLogResponse getLoginLogById(Long id);

    /**
     * 删除登录日志
     */
    void deleteLoginLog(Long id);

    /**
     * 批量删除登录日志
     */
    void batchDeleteLoginLogs(List<Long> ids);

    /**
     * 清空登录日志
     */
    void clearLoginLogs();

    /**
     * 保存登录日志
     */
    void saveLoginLog(LoginLog loginLog);

    /**
     * 导出查询 - 获取总数
     */
    long exportCount(LoginLogQueryRequest request);

    /**
     * 导出查询 - 分页获取数据
     */
    Page<LoginLogResponse> exportPage(LoginLogQueryRequest request);
}
