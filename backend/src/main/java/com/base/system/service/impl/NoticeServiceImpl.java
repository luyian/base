package com.base.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import com.base.common.util.SecurityUtils;
import com.base.system.dto.notice.MyNoticeResponse;
import com.base.system.dto.notice.NoticeQueryRequest;
import com.base.system.dto.notice.NoticeResponse;
import com.base.system.dto.notice.NoticeSaveRequest;
import com.base.system.entity.Notice;
import com.base.system.entity.NoticeRead;
import com.base.system.mapper.NoticeMapper;
import com.base.system.mapper.NoticeReadMapper;
import com.base.system.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 通知公告服务实现类
 *
 * @author base
 * @since 2026-01-13
 */
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;
    private final NoticeReadMapper noticeReadMapper;

    private static final Map<Integer, String> TYPE_MAP = new HashMap<>();
    private static final Map<Integer, String> LEVEL_MAP = new HashMap<>();
    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();

    static {
        TYPE_MAP.put(1, "通知");
        TYPE_MAP.put(2, "公告");

        LEVEL_MAP.put(1, "普通");
        LEVEL_MAP.put(2, "重要");
        LEVEL_MAP.put(3, "紧急");

        STATUS_MAP.put(0, "草稿");
        STATUS_MAP.put(1, "已发布");
    }

    @Override
    public Page<NoticeResponse> pageNotices(NoticeQueryRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(request.getTitle()), Notice::getTitle, request.getTitle())
                .eq(request.getType() != null, Notice::getType, request.getType())
                .eq(request.getLevel() != null, Notice::getLevel, request.getLevel())
                .eq(request.getStatus() != null, Notice::getStatus, request.getStatus())
                .orderByDesc(Notice::getCreateTime);

        // 分页查询
        Page<Notice> page = noticeMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()),
                wrapper
        );

        // 转换为响应对象
        Page<NoticeResponse> responsePage = new Page<>();
        BeanUtil.copyProperties(page, responsePage, "records");
        responsePage.setRecords(
                page.getRecords().stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList())
        );

        return responsePage;
    }

    @Override
    public NoticeResponse getNoticeById(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "通知公告不存在");
        }
        return convertToResponse(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addNotice(NoticeSaveRequest request) {
        Notice notice = new Notice();
        BeanUtil.copyProperties(request, notice);

        // 如果状态为已发布，设置发布时间
        if (request.getStatus() == 1) {
            notice.setPublishTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        noticeMapper.insert(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotice(NoticeSaveRequest request) {
        // 检查通知是否存在
        Notice existNotice = noticeMapper.selectById(request.getId());
        if (existNotice == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "通知公告不存在");
        }

        Notice notice = new Notice();
        BeanUtil.copyProperties(request, notice);

        // 如果从草稿变为已发布，设置发布时间
        if (existNotice.getStatus() == 0 && request.getStatus() == 1) {
            notice.setPublishTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        noticeMapper.updateById(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotice(Long id) {
        noticeMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteNotices(List<Long> ids) {
        noticeMapper.deleteBatchIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishNotice(Long id) {
        // 检查通知是否存在
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "通知公告不存在");
        }

        // 更新状态为已发布
        notice.setStatus(1);
        notice.setPublishTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        noticeMapper.updateById(notice);
    }

    @Override
    public List<NoticeResponse> getLatestNotices(Integer limit) {
        // 查询最新的已发布通知
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getStatus, 1)
                .orderByDesc(Notice::getPublishTime)
                .last("LIMIT " + limit);

        List<Notice> notices = noticeMapper.selectList(wrapper);
        return notices.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 转换为响应对象
     */
    private NoticeResponse convertToResponse(Notice notice) {
        NoticeResponse response = new NoticeResponse();
        BeanUtil.copyProperties(notice, response);
        response.setTypeName(TYPE_MAP.get(notice.getType()));
        response.setLevelName(LEVEL_MAP.get(notice.getLevel()));
        response.setStatusName(STATUS_MAP.get(notice.getStatus()));
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long noticeId) {
        // 获取当前用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户未登录");
        }

        // 检查通知是否存在
        Notice notice = noticeMapper.selectById(noticeId);
        if (notice == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "通知公告不存在");
        }

        // 检查是否已经标记为已读
        LambdaQueryWrapper<NoticeRead> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NoticeRead::getNoticeId, noticeId)
                .eq(NoticeRead::getUserId, userId);
        Long count = noticeReadMapper.selectCount(wrapper);

        // 如果未读，则插入已读记录
        if (count == 0) {
            NoticeRead noticeRead = new NoticeRead();
            noticeRead.setNoticeId(noticeId);
            noticeRead.setUserId(userId);
            noticeRead.setReadTime(LocalDateTime.now());
            noticeReadMapper.insert(noticeRead);
        }
    }

    @Override
    public Long getUnreadCount() {
        // 获取当前用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return 0L;
        }

        // 查询已发布的通知总数
        LambdaQueryWrapper<Notice> noticeWrapper = new LambdaQueryWrapper<>();
        noticeWrapper.eq(Notice::getStatus, 1);
        Long totalCount = noticeMapper.selectCount(noticeWrapper);

        // 查询当前用户已读的通知数
        LambdaQueryWrapper<NoticeRead> readWrapper = new LambdaQueryWrapper<>();
        readWrapper.eq(NoticeRead::getUserId, userId);
        Long readCount = noticeReadMapper.selectCount(readWrapper);

        // 未读数量 = 总数 - 已读数
        return totalCount - readCount;
    }

    @Override
    public Page<MyNoticeResponse> getMyNotices(NoticeQueryRequest request) {
        // 获取当前用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户未登录");
        }

        // 构建查询条件（只查询已发布的通知）
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getStatus, 1)
                .like(StrUtil.isNotBlank(request.getTitle()), Notice::getTitle, request.getTitle())
                .eq(request.getType() != null, Notice::getType, request.getType())
                .eq(request.getLevel() != null, Notice::getLevel, request.getLevel())
                .orderByDesc(Notice::getPublishTime);

        // 分页查询
        Page<Notice> page = noticeMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()),
                wrapper
        );

        // 查询当前用户已读的通知ID集合
        LambdaQueryWrapper<NoticeRead> readWrapper = new LambdaQueryWrapper<>();
        readWrapper.eq(NoticeRead::getUserId, userId);
        List<NoticeRead> readList = noticeReadMapper.selectList(readWrapper);
        Set<Long> readNoticeIds = readList.stream()
                .map(NoticeRead::getNoticeId)
                .collect(Collectors.toSet());

        // 转换为响应对象
        Page<MyNoticeResponse> responsePage = new Page<>();
        BeanUtil.copyProperties(page, responsePage, "records");
        responsePage.setRecords(
                page.getRecords().stream()
                        .map(notice -> {
                            MyNoticeResponse response = new MyNoticeResponse();
                            BeanUtil.copyProperties(notice, response);
                            response.setTypeName(TYPE_MAP.get(notice.getType()));
                            response.setLevelName(LEVEL_MAP.get(notice.getLevel()));
                            response.setStatusName(STATUS_MAP.get(notice.getStatus()));
                            response.setIsRead(readNoticeIds.contains(notice.getId()));
                            return response;
                        })
                        .collect(Collectors.toList())
        );

        return responsePage;
    }
}
