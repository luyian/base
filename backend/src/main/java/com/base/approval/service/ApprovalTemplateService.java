package com.base.approval.service;

import com.base.approval.entity.ApprovalTemplate;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 审批模板服务接口
 *
 * @author base
 */
public interface ApprovalTemplateService {

    /**
     * 分页查询模板列表
     */
    IPage<ApprovalTemplate> pageList(int pageNum, int pageSize, String platform, Integer status);

    /**
     * 根据ID查询模板
     */
    ApprovalTemplate getById(Long id);

    /**
     * 根据模板编码和平台查询启用的模板
     */
    ApprovalTemplate getByCodeAndPlatform(String templateCode, String platform);

    /**
     * 新增模板
     */
    void save(ApprovalTemplate template);

    /**
     * 更新模板
     */
    void update(ApprovalTemplate template);

    /**
     * 删除模板
     */
    void deleteById(Long id);
}
