package com.base.system.dto;

import lombok.Data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 扫描文档工作区（内存态，不落数据库）
 * <p>
 * 由后端 ConcurrentHashMap&lt;docId, ScanDocWorkArea&gt; 维护；
 * 服务重启后进行中的工作区丢失并由临时目录清理兜底。
 * </p>
 *
 * @author base
 * @since 2026-08-25
 */
@Data
public class ScanDocWorkArea {

    /**
     * 工作区唯一编号（= docNo，形如 PDF20260825093015001）
     */
    private String docId;

    /**
     * 展示用编号（与 docId 相同）
     */
    private String docNo;

    /**
     * 用户自定义命名
     */
    private String docName;

    /**
     * 创建人用户ID（用于临时目录按用户隔离）
     */
    private Long userId;

    /**
     * 状态：0-进行中 1-已完成
     */
    private int status;

    /**
     * 已添加图片列表（线程安全，顺序即默认页序）
     */
    private List<ScanDocImage> images = new CopyOnWriteArrayList<>();
}