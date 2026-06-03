package com.base.ocr.interfaces.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * OCR 识别请求参数
 *
 * @author base
 */
@Data
public class OcrRecognizeRequest {

    /** 指定供应商（可选，为空则使用默认主数据源） */
    private String provider;
}
