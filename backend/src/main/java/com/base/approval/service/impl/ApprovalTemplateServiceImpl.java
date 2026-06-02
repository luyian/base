package com.base.approval.service.impl;

import com.base.approval.entity.ApprovalTemplate;
import com.base.approval.mapper.ApprovalTemplateMapper;
import com.base.approval.service.ApprovalTemplateService;
import com.base.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 审批模板服务实现
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalTemplateServiceImpl implements ApprovalTemplateService {

    private final ApprovalTemplateMapper approvalTemplateMapper;

    @Override
    public IPage<ApprovalTemplate> pageList(int pageNum, int pageSize, String platform, Integer status) {
        LambdaQueryWrapper<ApprovalTemplate> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(platform)) {
            wrapper.eq(ApprovalTemplate::getPlatform, platform);
        }
        if (status != null) {
            wrapper.eq(ApprovalTemplate::getStatus, status);
        }
        wrapper.orderByDesc(ApprovalTemplate::getCreateTime);
        return approvalTemplateMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public ApprovalTemplate getById(Long id) {
        return approvalTemplateMapper.selectById(id);
    }

    @Override
    public ApprovalTemplate getByCodeAndPlatform(String templateCode, String platform) {
        LambdaQueryWrapper<ApprovalTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalTemplate::getTemplateCode, templateCode)
                .eq(ApprovalTemplate::getPlatform, platform)
                .eq(ApprovalTemplate::getStatus, 1);
        return approvalTemplateMapper.selectOne(wrapper);
    }

    @Override
    public void save(ApprovalTemplate template) {
        LambdaQueryWrapper<ApprovalTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalTemplate::getTemplateCode, template.getTemplateCode())
                .eq(ApprovalTemplate::getPlatform, template.getPlatform());
        if (approvalTemplateMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("同平台下模板编码已存在: " + template.getTemplateCode());
        }
        approvalTemplateMapper.insert(template);
    }

    @Override
    public void update(ApprovalTemplate template) {
        if (approvalTemplateMapper.selectById(template.getId()) == null) {
            throw new BusinessException("模板不存在");
        }
        approvalTemplateMapper.updateById(template);
    }

    @Override
    public void deleteById(Long id) {
        if (approvalTemplateMapper.selectById(id) == null) {
            throw new BusinessException("模板不存在");
        }
        approvalTemplateMapper.deleteById(id);
    }
}
