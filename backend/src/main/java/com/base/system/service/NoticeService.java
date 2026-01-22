package com.base.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.system.dto.notice.MyNoticeResponse;
import com.base.system.dto.notice.NoticeQueryRequest;
import com.base.system.dto.notice.NoticeResponse;
import com.base.system.dto.notice.NoticeSaveRequest;

import java.util.List;

/**
 * 通知公告服务接口
 *
 * @author base
 * @since 2026-01-13
 */
public interface NoticeService {

    /**
     * 分页查询通知公告列表
     *
     * @param request 查询请求参数
     * @return 分页结果
     */
    Page<NoticeResponse> pageNotices(NoticeQueryRequest request);

    /**
     * 根据ID获取通知公告详情
     *
     * @param id 通知ID
     * @return 通知详情
     */
    NoticeResponse getNoticeById(Long id);

    /**
     * 新增通知公告
     *
     * @param request 保存请求参数
     */
    void addNotice(NoticeSaveRequest request);

    /**
     * 编辑通知公告
     *
     * @param request 保存请求参数
     */
    void updateNotice(NoticeSaveRequest request);

    /**
     * 删除通知公告
     *
     * @param id 通知ID
     */
    void deleteNotice(Long id);

    /**
     * 批量删除通知公告
     *
     * @param ids 通知ID列表
     */
    void batchDeleteNotices(List<Long> ids);

    /**
     * 发布通知公告
     *
     * @param id 通知ID
     */
    void publishNotice(Long id);

    /**
     * 获取最新通知列表（用于首页展示）
     *
     * @param limit 数量限制
     * @return 通知列表
     */
    List<NoticeResponse> getLatestNotices(Integer limit);

    /**
     * 标记通知为已读（当前用户）
     *
     * @param noticeId 通知ID
     */
    void markAsRead(Long noticeId);

    /**
     * 获取当前用户未读通知数量
     *
     * @return 未读通知数量
     */
    Long getUnreadCount();

    /**
     * 获取当前用户的通知列表（分页，包含已读状态）
     *
     * @param request 查询请求参数
     * @return 分页结果
     */
    Page<MyNoticeResponse> getMyNotices(NoticeQueryRequest request);
}
