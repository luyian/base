package com.base.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import com.base.system.dto.log.LoginLogQueryRequest;
import com.base.system.dto.log.LoginLogResponse;
import com.base.system.entity.LoginLog;
import com.base.system.mapper.LoginLogMapper;
import com.base.system.service.LoginLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录日志服务实现类
 */
@Service
public class LoginLogServiceImpl implements LoginLogService {

    @Resource
    private LoginLogMapper loginLogMapper;

    @Override
    public Page<LoginLogResponse> pageLoginLogs(LoginLogQueryRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<LoginLog> wrapper = buildQueryWrapper(request);

        // 分页查询
        Page<LoginLog> page = new Page<>(request.getCurrent(), request.getSize());
        Page<LoginLog> loginLogPage = loginLogMapper.selectPage(page, wrapper);

        // 转换为响应对象
        Page<LoginLogResponse> responsePage = new Page<>(loginLogPage.getCurrent(), loginLogPage.getSize(), loginLogPage.getTotal());
        List<LoginLogResponse> responseList = loginLogPage.getRecords().stream().map(loginLog -> {
            LoginLogResponse response = new LoginLogResponse();
            BeanUtils.copyProperties(loginLog, response);
            return response;
        }).collect(Collectors.toList());
        responsePage.setRecords(responseList);

        return responsePage;
    }

    @Override
    public LoginLogResponse getLoginLogById(Long id) {
        LoginLog loginLog = loginLogMapper.selectById(id);
        if (loginLog == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        LoginLogResponse response = new LoginLogResponse();
        BeanUtils.copyProperties(loginLog, response);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLoginLog(Long id) {
        LoginLog loginLog = loginLogMapper.selectById(id);
        if (loginLog == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        loginLogMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteLoginLogs(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        loginLogMapper.deleteBatchIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearLoginLogs() {
        loginLogMapper.delete(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveLoginLog(LoginLog loginLog) {
        loginLogMapper.insert(loginLog);
    }

    @Override
    public long exportCount(LoginLogQueryRequest request) {
        LambdaQueryWrapper<LoginLog> wrapper = buildQueryWrapper(request);
        return loginLogMapper.selectCount(wrapper);
    }

    @Override
    public Page<LoginLogResponse> exportPage(LoginLogQueryRequest request) {
        return pageLoginLogs(request);
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<LoginLog> buildQueryWrapper(LoginLogQueryRequest request) {
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(request.getUsername()), LoginLog::getUsername, request.getUsername())
                .like(StringUtils.hasText(request.getLoginIp()), LoginLog::getLoginIp, request.getLoginIp())
                .eq(request.getStatus() != null, LoginLog::getStatus, request.getStatus());

        if (StringUtils.hasText(request.getStartTime())) {
            LocalDateTime startTime = LocalDateTime.parse(request.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            wrapper.ge(LoginLog::getCreateTime, startTime);
        }
        if (StringUtils.hasText(request.getEndTime())) {
            LocalDateTime endTime = LocalDateTime.parse(request.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            wrapper.le(LoginLog::getCreateTime, endTime);
        }

        wrapper.orderByDesc(LoginLog::getCreateTime);
        return wrapper;
    }
}
