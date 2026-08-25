package com.base.system.dto;

import lombok.Data;

/**
 * 扫描文档工作区中的单张图片
 *
 * @author base
 * @since 2026-08-25
 */
@Data
public class ScanDocImage {

    /**
     * 图片唯一标识
     */
    private String id;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 临时文件绝对路径
     */
    private String tempPath;

    /**
     * 排序号（越小越靠前，决定 PDF 页序）
     */
    private int sort;

    /**
     * 文件大小（字节）
     */
    private long size;
}