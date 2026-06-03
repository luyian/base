package com.base.ocr.domain.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 银行卡识别结果（值对象）
 *
 * @author base
 */
@Data
public class BankCardResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 银行卡号 */
    private String cardNumber;

    /** 银行名称 */
    private String bankName;

    /** 卡片类型（借记卡、信用卡） */
    private String cardType;

    /** 持卡人姓名 */
    private String holderName;

    /** 有效期（信用卡，格式 MM/YY） */
    private String validDate;

    /** 识别供应商 */
    private String provider;

    /** 识别置信度（0-100） */
    private Integer confidence;
}
