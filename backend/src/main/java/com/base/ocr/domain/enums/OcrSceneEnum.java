package com.base.ocr.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * OCR 识别场景枚举
 *
 * @author base
 */
@Getter
@AllArgsConstructor
public enum OcrSceneEnum {

    /** 身份证识别 */
    ID_CARD("id_card", "身份证识别"),

    /** 发票识别 */
    INVOICE("invoice", "发票识别"),

    /** 银行卡识别 */
    BANK_CARD("bank_card", "银行卡识别");

    /** 场景编码 */
    private final String code;

    /** 场景名称 */
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 场景编码
     * @return 枚举值，未匹配返回 null
     */
    public static OcrSceneEnum getByCode(String code) {
        for (OcrSceneEnum scene : values()) {
            if (scene.getCode().equals(code)) {
                return scene;
            }
        }
        return null;
    }
}
