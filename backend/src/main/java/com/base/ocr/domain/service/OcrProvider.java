package com.base.ocr.domain.service;

import com.base.ocr.domain.model.BankCardResult;
import com.base.ocr.domain.model.IdCardResult;
import com.base.ocr.domain.model.InvoiceResult;

/**
 * OCR 识别提供者接口（领域服务）
 * <p>
 * 定义在 domain 层，由 infrastructure 层各云厂商实现。
 * </p>
 *
 * @author base
 */
public interface OcrProvider {

    /**
     * 供应商标识
     *
     * @return 名称标识（如 tencent、baidu、aliyun）
     */
    String getName();

    /**
     * 供应商中文名称
     *
     * @return 中文名称
     */
    String getDisplayName();

    /**
     * 身份证识别
     *
     * @param imageData 图片二进制数据
     * @param side      识别面：front-正面 back-背面
     * @return 识别结果，失败返回 null
     */
    IdCardResult recognizeIdCard(byte[] imageData, String side);

    /**
     * 发票识别
     *
     * @param imageData 图片二进制数据
     * @return 识别结果，失败返回 null
     */
    InvoiceResult recognizeInvoice(byte[] imageData);

    /**
     * 银行卡识别
     *
     * @param imageData 图片二进制数据
     * @return 识别结果，失败返回 null
     */
    BankCardResult recognizeBankCard(byte[] imageData);
}
