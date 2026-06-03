package com.base.ocr.domain.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 身份证识别结果（值对象）
 *
 * @author base
 */
@Data
public class IdCardResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 姓名 */
    private String name;

    /** 性别 */
    private String gender;

    /** 民族 */
    private String nation;

    /** 出生日期 */
    private String birthday;

    /** 住址 */
    private String address;

    /** 身份证号码 */
    private String idNumber;

    /** 签发机关（背面） */
    private String authority;

    /** 有效期起始日期（背面） */
    private String validDateStart;

    /** 有效期截止日期（背面） */
    private String validDateEnd;

    /** 识别面：front-正面 back-背面 */
    private String side;

    /** 识别供应商 */
    private String provider;

    /** 识别置信度（0-100） */
    private Integer confidence;
}
