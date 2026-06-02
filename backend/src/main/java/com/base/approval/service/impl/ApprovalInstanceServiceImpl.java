package com.base.approval.service.impl;

import com.base.approval.dto.ApprovalInstanceResponse;
import com.base.approval.dto.ApprovalSubmitRequest;
import com.base.approval.entity.ApprovalInstance;
import com.base.approval.entity.ApprovalTemplate;
import com.base.approval.enums.ApprovalInstanceStatus;
import com.base.approval.mapper.ApprovalInstanceMapper;
import com.base.approval.service.ApprovalInstanceService;
import com.base.approval.service.ApprovalTemplateService;
import com.base.common.exception.BusinessException;
import com.base.common.feishu.approval.FeishuApprovalFormBuilder;
import com.base.common.thirdparty.ThirdPartyPlatform;
import com.base.common.thirdparty.approval.ApprovalCreateRequest;
import com.base.common.thirdparty.approval.ApprovalCreateResponse;
import com.base.common.thirdparty.approval.ApprovalInstanceInfo;
import com.base.common.thirdparty.approval.ApprovalStatusEnum;
import com.base.common.thirdparty.approval.ThirdPartyApprovalService;
import com.base.common.util.SecurityUtils;
import com.base.system.entity.SysUser;
import com.base.system.entity.UserOauth;
import com.base.system.mapper.SysUserMapper;
import com.base.system.mapper.UserOauthMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 审批实例服务实现
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalInstanceServiceImpl implements ApprovalInstanceService {

    private final ApprovalInstanceMapper approvalInstanceMapper;
    private final ApprovalTemplateService approvalTemplateService;
    private final List<ThirdPartyApprovalService> approvalServiceList;
    private final UserOauthMapper userOauthMapper;
    private final SysUserMapper sysUserMapper;

    private final Map<String, ThirdPartyApprovalService> approvalServiceMap = new HashMap<>();

    @javax.annotation.PostConstruct
    public void init() {
        for (ThirdPartyApprovalService service : approvalServiceList) {
            approvalServiceMap.put(service.getPlatform().getCode(), service);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApprovalInstanceResponse submitApproval(ApprovalSubmitRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();

        // 查询启用的模板（默认飞书平台）
        ApprovalTemplate template = approvalTemplateService.getByCodeAndPlatform(
                request.getTemplateCode(), ThirdPartyPlatform.FEISHU.getCode());
        if (template == null) {
            throw new BusinessException("未找到启用的审批模板: " + request.getTemplateCode());
        }

        // 幂等检查
        String idempotentKey = request.getTemplateCode() + ":" + request.getBusinessKey();
        LambdaQueryWrapper<ApprovalInstance> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(ApprovalInstance::getIdempotentKey, idempotentKey);
        ApprovalInstance existing = approvalInstanceMapper.selectOne(checkWrapper);
        if (existing != null) {
            throw new BusinessException("该业务已发起过审批，请勿重复提交");
        }

        // 查找发起人绑定的平台用户ID
        String openId = getOpenId(userId, template.getPlatform());

        // 构建表单数据
        String formData = FeishuApprovalFormBuilder.buildForm(
                template.getFormMapping(),
                request.getFormData() != null ? request.getFormData() : new HashMap<>());

        // 入库
        ApprovalInstance instance = new ApprovalInstance();
        instance.setTemplateId(template.getId());
        instance.setTemplateCode(template.getTemplateCode());
        instance.setPlatform(template.getPlatform());
        instance.setBusinessKey(request.getBusinessKey());
        instance.setBusinessType(request.getBusinessType());
        instance.setTitle(request.getTitle());
        instance.setApplicantUserId(userId);
        instance.setApplicantOpenId(openId);
        instance.setFormData(formData);
        instance.setStatus(ApprovalInstanceStatus.PENDING.getCode());
        instance.setIdempotentKey(idempotentKey);
        instance.setSubmittedAt(LocalDateTime.now());
        approvalInstanceMapper.insert(instance);

        // 调用平台 API 发起审批
        ThirdPartyApprovalService approvalService = getApprovalService(template.getPlatform());
        ApprovalCreateRequest createRequest = new ApprovalCreateRequest();
        createRequest.setApprovalCode(template.getPlatformApprovalCode());
        createRequest.setApplicantOpenId(openId);
        createRequest.setFormData(formData);
        createRequest.setIdempotentKey(idempotentKey);

        try {
            ApprovalCreateResponse resp = approvalService.createApproval(createRequest);
            instance.setPlatformInstanceId(resp.getPlatformInstanceId());
            approvalInstanceMapper.updateById(instance);
            log.info("审批发起成功，instanceId: {}，platformInstanceId: {}",
                    instance.getId(), resp.getPlatformInstanceId());
        } catch (Exception e) {
            log.error("审批发起失败，instanceId: {}", instance.getId(), e);
            approvalInstanceMapper.deleteById(instance.getId());
            throw new BusinessException("审批发起失败: " + e.getMessage());
        }

        return toResponse(instance);
    }

    @Override
    public void cancelApproval(Long instanceId) {
        ApprovalInstance instance = approvalInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw new BusinessException("审批实例不存在");
        }
        if (!ApprovalInstanceStatus.PENDING.getCode().equals(instance.getStatus())) {
            throw new BusinessException("只能撤销审批中的实例");
        }
        ThirdPartyApprovalService approvalService = getApprovalService(instance.getPlatform());
        ApprovalTemplate template = approvalTemplateService.getById(instance.getTemplateId());

        approvalService.cancelApproval(instance.getPlatformInstanceId(),
                template.getPlatformApprovalCode(), instance.getApplicantOpenId());

        instance.setStatus(ApprovalInstanceStatus.CANCELED.getCode());
        instance.setCompletedAt(LocalDateTime.now());
        approvalInstanceMapper.updateById(instance);
    }

    @Override
    public void updateStatusFromCallback(ThirdPartyPlatform platform, String platformInstanceId,
                                         String platformStatus, String comment) {
        LambdaQueryWrapper<ApprovalInstance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalInstance::getPlatform, platform.getCode())
                .eq(ApprovalInstance::getPlatformInstanceId, platformInstanceId);
        ApprovalInstance instance = approvalInstanceMapper.selectOne(wrapper);
        if (instance == null) {
            log.warn("收到回调但未找到审批实例: platform={}, instanceId={}", platform, platformInstanceId);
            return;
        }
        // 已终态不再更新
        ApprovalInstanceStatus currentStatus = ApprovalInstanceStatus.valueOf(instance.getStatus());
        if (currentStatus.isTerminal()) {
            log.debug("审批实例已处于终态，跳过回调: id={}, status={}", instance.getId(), instance.getStatus());
            return;
        }

        ApprovalStatusEnum mappedStatus = ApprovalStatusEnum.fromCode(platformStatus);
        if (mappedStatus != null) {
            instance.setStatus(mappedStatus.getCode());
        }
        instance.setPlatformStatus(platformStatus);
        instance.setResultComment(comment);
        if (mappedStatus != null && mappedStatus != ApprovalStatusEnum.PENDING) {
            instance.setCompletedAt(LocalDateTime.now());
        }
        approvalInstanceMapper.updateById(instance);
        log.info("审批状态回调更新: id={}, status={}", instance.getId(), platformStatus);
    }

    @Override
    public void syncApprovalStatus(Long instanceId) {
        ApprovalInstance instance = approvalInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw new BusinessException("审批实例不存在");
        }
        if (instance.getPlatformInstanceId() == null) {
            throw new BusinessException("审批实例尚未关联平台实例");
        }
        ThirdPartyApprovalService approvalService = getApprovalService(instance.getPlatform());
        ApprovalInstanceInfo info = approvalService.getApprovalStatus(instance.getPlatformInstanceId());

        if (info.getStatus() != null) {
            instance.setStatus(info.getStatus().getCode());
            instance.setPlatformStatus(info.getStatus().getCode());
        }
        if (info.getComment() != null) {
            instance.setResultComment(info.getComment());
        }
        if (info.getCompletedAt() != null) {
            instance.setCompletedAt(info.getCompletedAt());
        }
        approvalInstanceMapper.updateById(instance);
    }

    @Override
    public IPage<ApprovalInstanceResponse> pageList(int pageNum, int pageSize, String status,
                                                     String businessType, Long applicantUserId) {
        LambdaQueryWrapper<ApprovalInstance> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(ApprovalInstance::getStatus, status);
        }
        if (StringUtils.hasText(businessType)) {
            wrapper.eq(ApprovalInstance::getBusinessType, businessType);
        }
        if (applicantUserId != null) {
            wrapper.eq(ApprovalInstance::getApplicantUserId, applicantUserId);
        }
        wrapper.orderByDesc(ApprovalInstance::getCreateTime);
        IPage<ApprovalInstance> page = approvalInstanceMapper.selectPage(
                new Page<>(pageNum, pageSize), wrapper);

        return page.convert(this::toResponse);
    }

    @Override
    public ApprovalInstanceResponse getDetail(Long instanceId) {
        ApprovalInstance instance = approvalInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw new BusinessException("审批实例不存在");
        }
        return toResponse(instance);
    }

    @Override
    public ApprovalInstanceResponse getByBusinessKey(String businessKey) {
        LambdaQueryWrapper<ApprovalInstance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalInstance::getBusinessKey, businessKey)
                .orderByDesc(ApprovalInstance::getCreateTime)
                .last("LIMIT 1");
        ApprovalInstance instance = approvalInstanceMapper.selectOne(wrapper);
        return instance != null ? toResponse(instance) : null;
    }

    private String getOpenId(Long userId, String platform) {
        LambdaQueryWrapper<UserOauth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOauth::getUserId, userId)
                .eq(UserOauth::getOauthType, platform);
        UserOauth userOauth = userOauthMapper.selectOne(wrapper);
        if (userOauth == null) {
            throw new BusinessException("当前用户未绑定" +
                    ThirdPartyPlatform.fromCode(platform).getDesc() + "账号");
        }
        return userOauth.getOauthId();
    }

    private ThirdPartyApprovalService getApprovalService(String platform) {
        ThirdPartyApprovalService service = approvalServiceMap.get(platform);
        if (service == null) {
            throw new BusinessException("不支持的审批平台: " + platform);
        }
        return service;
    }

    private ApprovalInstanceResponse toResponse(ApprovalInstance instance) {
        ApprovalInstanceResponse resp = new ApprovalInstanceResponse();
        resp.setId(instance.getId());
        resp.setTemplateCode(instance.getTemplateCode());
        resp.setPlatform(instance.getPlatform());
        resp.setPlatformInstanceId(instance.getPlatformInstanceId());
        resp.setBusinessKey(instance.getBusinessKey());
        resp.setBusinessType(instance.getBusinessType());
        resp.setTitle(instance.getTitle());
        resp.setApplicantUserId(instance.getApplicantUserId());
        resp.setFormData(instance.getFormData());
        resp.setStatus(instance.getStatus());
        resp.setResultComment(instance.getResultComment());
        resp.setSubmittedAt(instance.getSubmittedAt());
        resp.setCompletedAt(instance.getCompletedAt());
        resp.setCreateTime(instance.getCreateTime());

        // 填充模板名称
        ApprovalTemplate template = approvalTemplateService.getById(instance.getTemplateId());
        if (template != null) {
            resp.setTemplateName(template.getTemplateName());
        }
        // 填充发起人姓名
        SysUser user = sysUserMapper.selectById(instance.getApplicantUserId());
        if (user != null) {
            resp.setApplicantName(user.getNickname());
        }
        return resp;
    }
}
