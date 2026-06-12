package com.base.dev.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.dev.dto.BranchQueryRequest;
import com.base.dev.dto.BranchResponse;
import com.base.dev.dto.BranchSaveRequest;
import com.base.dev.dto.BranchStatsResponse;

/**
 * 代码分支管理服务接口
 *
 * @author base
 */
public interface DevBranchService {

    /**
     * 分页查询分支列表（按上线时间倒序）
     *
     * @param request 查询请求参数
     * @return 分页结果
     */
    Page<BranchResponse> pageBranches(BranchQueryRequest request);

    /**
     * 新增分支记录
     *
     * @param request 保存请求参数
     */
    void addBranch(BranchSaveRequest request);

    /**
     * 编辑分支记录
     *
     * @param request 保存请求参数
     */
    void updateBranch(BranchSaveRequest request);

    /**
     * 删除分支记录
     *
     * @param id 记录ID
     */
    void deleteBranch(Long id);

    /**
     * 获取当前生产分支
     *
     * @return 生产分支名称
     */
    String getCurrentProdBranch();

    /**
     * 更新当前生产分支
     *
     * @param prodBranch 新的生产分支名称
     */
    void updateCurrentProdBranch(String prodBranch);

    /**
     * 获取分支统计信息
     *
     * @return 统计数据
     */
    BranchStatsResponse getStats();

    /**
     * 完成分支记录
     *
     * @param id 记录ID
     */
    void completeBranch(Long id);
}
