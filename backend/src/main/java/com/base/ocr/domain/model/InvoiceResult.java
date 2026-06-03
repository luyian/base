package com.base.ocr.domain.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 发票识别结果（值对象）
 *
 * @author base
 */
@Data
public class InvoiceResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 发票类型 */
    private String invoiceType;

    /** 发票代码 */
    private String invoiceCode;

    /** 发票号码 */
    private String invoiceNumber;

    /** 开票日期 */
    private String invoiceDate;

    /** 校验码 */
    private String checkCode;

    /** 购买方名称 */
    private String buyerName;

    /** 购买方纳税人识别号 */
    private String buyerTaxId;

    /** 销售方名称 */
    private String sellerName;

    /** 销售方纳税人识别号 */
    private String sellerTaxId;

    /** 合计金额（不含税） */
    private String totalAmount;

    /** 合计税额 */
    private String totalTax;

    /** 价税合计（大写） */
    private String amountInWords;

    /** 价税合计（小写） */
    private String amountInFigures;

    /** 识别供应商 */
    private String provider;

    /** 识别置信度（0-100） */
    private Integer confidence;
}
