package com.base.system.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件转换服务接口
 *
 * @author base
 * @since 2026-06-10
 */
public interface FileConvertService {

    /**
     * PDF 转 Word
     *
     * @param file PDF 文件
     * @return 包含源文件和转换后文件信息的 Map
     */
    Map<String, Object> pdfToWord(MultipartFile file);

    /**
     * PDF 转 Markdown
     *
     * @param file PDF 文件
     * @return 包含源文件和转换后文件信息的 Map
     */
    Map<String, Object> pdfToMarkdown(MultipartFile file);
}
