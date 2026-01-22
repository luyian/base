package com.base.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.annotation.Log;
import com.base.common.result.Result;
import com.base.system.dto.notice.MyNoticeResponse;
import com.base.system.dto.notice.NoticeQueryRequest;
import com.base.system.dto.notice.NoticeResponse;
import com.base.system.dto.notice.NoticeSaveRequest;
import com.base.system.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知公告控制器
 *
 * @author base
 * @since 2026-01-13
 */
@Tag(name = "通知公告管理")
@RestController
@RequestMapping("/system/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "分页查询通知公告列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:notice:list')")
    public Result<Page<NoticeResponse>> pageNotices(NoticeQueryRequest request) {
        return Result.success(noticeService.pageNotices(request));
    }

    @Operation(summary = "根据ID获取通知公告详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:notice:query')")
    public Result<NoticeResponse> getNoticeById(@PathVariable Long id) {
        return Result.success(noticeService.getNoticeById(id));
    }

    @Operation(summary = "新增通知公告")
    @PostMapping
    @PreAuthorize("hasAuthority('system:notice:add')")
    @Log(title = "通知公告管理", content = "新增通知公告", type = "INSERT")
    public Result<Void> addNotice(@Validated @RequestBody NoticeSaveRequest request) {
        noticeService.addNotice(request);
        return Result.success();
    }

    @Operation(summary = "编辑通知公告")
    @PutMapping
    @PreAuthorize("hasAuthority('system:notice:edit')")
    @Log(title = "通知公告管理", content = "编辑通知公告", type = "UPDATE")
    public Result<Void> updateNotice(@Validated @RequestBody NoticeSaveRequest request) {
        noticeService.updateNotice(request);
        return Result.success();
    }

    @Operation(summary = "删除通知公告")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:notice:delete')")
    @Log(title = "通知公告管理", content = "删除通知公告", type = "DELETE")
    public Result<Void> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return Result.success();
    }

    @Operation(summary = "批量删除通知公告")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('system:notice:delete')")
    @Log(title = "通知公告管理", content = "批量删除通知公告", type = "DELETE")
    public Result<Void> batchDeleteNotices(@RequestBody List<Long> ids) {
        noticeService.batchDeleteNotices(ids);
        return Result.success();
    }

    @Operation(summary = "发布通知公告")
    @PutMapping("/{id}/publish")
    @PreAuthorize("hasAuthority('system:notice:edit')")
    @Log(title = "通知公告管理", content = "发布通知公告", type = "UPDATE")
    public Result<Void> publishNotice(@PathVariable Long id) {
        noticeService.publishNotice(id);
        return Result.success();
    }

    @Operation(summary = "获取最新通知列表")
    @GetMapping("/latest")
    public Result<List<NoticeResponse>> getLatestNotices(@RequestParam(defaultValue = "5") Integer limit) {
        return Result.success(noticeService.getLatestNotices(limit));
    }

    @Operation(summary = "标记通知为已读")
    @PostMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        noticeService.markAsRead(id);
        return Result.success();
    }

    @Operation(summary = "获取未读通知数量")
    @GetMapping("/unread/count")
    public Result<Long> getUnreadCount() {
        return Result.success(noticeService.getUnreadCount());
    }

    @Operation(summary = "获取我的通知列表")
    @GetMapping("/my")
    public Result<Page<MyNoticeResponse>> getMyNotices(NoticeQueryRequest request) {
        return Result.success(noticeService.getMyNotices(request));
    }
}
