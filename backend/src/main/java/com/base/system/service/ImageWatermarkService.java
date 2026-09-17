package com.base.system.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 图片去水印服务
 *
 * @author base
 * @since 2026-09-17
 */
public interface ImageWatermarkService {

    /**
     * 去除图片水印
     *
     * @param file    源图片
     * @param region  手动框选区域 "x,y,w,h"，为空则自动识别已知 AI 平台水印
     * @param backend 擦除后端（cv2/migan/lama）
     * @return 去水印结果，含 sourceFile 与 targetFile 文件信息
     */
    Map<String, Object> removeWatermark(MultipartFile file, String region, String backend);
}