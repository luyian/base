package com.base.dev.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.dev.dto.BranchQueryRequest;
import com.base.dev.dto.BranchResponse;
import com.base.dev.dto.BranchSaveRequest;
import com.base.dev.dto.BranchStatsResponse;
import com.base.dev.entity.DevBranch;
import com.base.dev.enums.PriorityEnum;
import com.base.dev.mapper.DevBranchMapper;
import com.base.dev.service.DevBranchService;
import com.base.system.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.stream.Collectors;

/**
 * 代码分支管理服务实现类
 *
 * @author base
 */
@Slf4j
@Service
public class DevBranchServiceImpl implements DevBranchService {

    private static final String CONFIG_KEY_CURRENT_PROD = "branch.current_prod";

    @Autowired
    private DevBranchMapper devBranchMapper;

    @Autowired
    private ConfigService configService;

    @Override
    public Page<BranchResponse> pageBranches(BranchQueryRequest request) {
        Page<DevBranch> page = request.buildPage();

        LambdaQueryWrapper<DevBranch> wrapper = new LambdaQueryWrapper<>();
        // keyword 为标题或编号的 OR 模糊查询
        String keyword = request.getKeyword();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(DevBranch::getTitle, keyword).or().like(DevBranch::getCode, keyword));
        }
        if (StringUtils.hasText(request.getTitle())) {
            wrapper.like(DevBranch::getTitle, request.getTitle());
        }
        if (StringUtils.hasText(request.getCode())) {
            wrapper.like(DevBranch::getCode, request.getCode());
        }
        wrapper.eq(StringUtils.hasText(request.getProdBranch()), DevBranch::getProdBranch, request.getProdBranch());
        if (request.getStatus() != null) {
            if (request.getStatus() == 1) {
                wrapper.eq(DevBranch::getStatus, 1);
            } else {
                wrapper.and(w -> w.ne(DevBranch::getStatus, 1).or().isNull(DevBranch::getStatus));
            }
        }
        wrapper.orderByAsc(DevBranch::getOnlineTime);

        Page<DevBranch> resultPage = devBranchMapper.selectPage(page, wrapper);

        Page<BranchResponse> responsePage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        responsePage.setRecords(resultPage.getRecords().stream().map(this::toResponse).collect(java.util.stream.Collectors.toList()));
        return responsePage;
    }

    @Override
    public void addBranch(BranchSaveRequest request) {
        DevBranch entity = new DevBranch();
        BeanUtils.copyProperties(request, entity);

        // 获取当前生产分支
        String prodBranch = getCurrentProdBranch();
        entity.setProdBranch(prodBranch);

        // 开发分支：优先使用自定义值，否则自动生成
        if (StringUtils.hasText(request.getDevBranch())) {
            entity.setDevBranch(request.getDevBranch());
        } else {
            entity.setDevBranch("dev_from_" + prodBranch + "_" + request.getCode());
        }

        // 默认紧急程度为普通
        if (entity.getPriority() == null) {
            entity.setPriority(PriorityEnum.NORMAL.getCode());
        }

        devBranchMapper.insert(entity);
    }

    @Override
    public void updateBranch(BranchSaveRequest request) {
        DevBranch entity = devBranchMapper.selectById(request.getId());
        if (entity == null) {
            throw new RuntimeException("分支记录不存在");
        }
        entity.setCode(request.getCode());
        entity.setTitle(request.getTitle());
        entity.setPrdLink(request.getPrdLink());
        entity.setOnlineTime(request.getOnlineTime());
        entity.setPriority(request.getPriority());

        // 开发分支：有值直接用，为空则保留原值
        if (StringUtils.hasText(request.getDevBranch())) {
            entity.setDevBranch(request.getDevBranch());
        }

        devBranchMapper.updateById(entity);
    }

    @Override
    public void deleteBranch(Long id) {
        devBranchMapper.deleteById(id);
    }

    @Override
    public String getCurrentProdBranch() {
        String value = configService.getConfigValueByKey(CONFIG_KEY_CURRENT_PROD);
        if (StringUtils.hasText(value)) {
            value = value.trim();
            // 兼容处理：如果存的是 JSON 对象格式，提取 prodBranch 字段
            if (value.startsWith("{") && value.contains("prodBranch")) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    java.util.Map<String, String> map = mapper.readValue(value, java.util.Map.class);
                    value = map.getOrDefault("prodBranch", value);
                } catch (Exception e) {
                    log.debug("解析 prodBranch JSON 配置失败，使用原始值: {}", e.getMessage());
                }
            }
            // 去除多余引号
            value = value.replace("\"", "").replace("'", "").trim();
        }
        return StringUtils.hasText(value) ? value : "master";
    }

    @Override
    public void updateCurrentProdBranch(String prodBranch) {
        // 存储前去除引号
        String cleanValue = prodBranch.replace("\"", "").replace("'", "").trim();
        configService.saveOrUpdateByKey(CONFIG_KEY_CURRENT_PROD, cleanValue, "当前生产分支", "string");
    }

    @Override
    public BranchStatsResponse getStats() {
        BranchStatsResponse stats = new BranchStatsResponse();
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = today.with(DayOfWeek.SUNDAY);

        LambdaQueryWrapper<DevBranch> allWrapper = new LambdaQueryWrapper<>();
        stats.setTotal(devBranchMapper.selectCount(allWrapper));

        // 待上线（上线时间 > 今天）
        LambdaQueryWrapper<DevBranch> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.gt(DevBranch::getOnlineTime, today);
        stats.setPendingCount(devBranchMapper.selectCount(pendingWrapper));

        // 已上线（上线时间 <= 今天）
        LambdaQueryWrapper<DevBranch> doneWrapper = new LambdaQueryWrapper<>();
        doneWrapper.le(DevBranch::getOnlineTime, today);
        stats.setDoneCount(devBranchMapper.selectCount(doneWrapper));

        // 紧急
        LambdaQueryWrapper<DevBranch> urgentWrapper = new LambdaQueryWrapper<>();
        urgentWrapper.eq(DevBranch::getPriority, PriorityEnum.URGENT.getCode())
                     .gt(DevBranch::getOnlineTime, today);
        stats.setUrgentCount(devBranchMapper.selectCount(urgentWrapper));

        // 特急
        LambdaQueryWrapper<DevBranch> criticalWrapper = new LambdaQueryWrapper<>();
        criticalWrapper.eq(DevBranch::getPriority, PriorityEnum.CRITICAL.getCode())
                       .gt(DevBranch::getOnlineTime, today);
        stats.setCriticalCount(devBranchMapper.selectCount(criticalWrapper));

        // 本周待上线
        LambdaQueryWrapper<DevBranch> weekWrapper = new LambdaQueryWrapper<>();
        weekWrapper.between(DevBranch::getOnlineTime, weekStart, weekEnd);
        stats.setThisWeekCount(devBranchMapper.selectCount(weekWrapper));

        return stats;
    }

    @Override
    public void completeBranch(Long id) {
        DevBranch entity = devBranchMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("分支记录不存在");
        }
        entity.setStatus(1);
        devBranchMapper.updateById(entity);
    }

    private BranchResponse toResponse(DevBranch entity) {
        BranchResponse response = new BranchResponse();
        BeanUtils.copyProperties(entity, response);
        response.setPriorityDesc(PriorityEnum.getByCode(entity.getPriority()).getDesc());
        return response;
    }
}
