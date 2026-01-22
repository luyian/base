package com.base.system.dto.notice;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 我的通知响应结果
 *
 * @author base
 * @since 2026-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MyNoticeResponse extends NoticeResponse {

    /**
     * 是否已读
     */
    private Boolean isRead;
}
