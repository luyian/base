package com.base.common.security;

import org.springframework.util.StringUtils;

/**
 * 敏感数据脱敏工具类
 *
 * @author base
 * @since 2026-01-15
 */
public class SensitiveDataUtil {

    private SensitiveDataUtil() {
    }

    /**
     * 手机号脱敏
     * 示例：138****8888
     *
     * @param phone 手机号
     * @return 脱敏后的手机号
     */
    public static String maskPhone(String phone) {
        if (!StringUtils.hasText(phone) || phone.length() < 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 邮箱脱敏
     * 示例：t***@example.com
     *
     * @param email 邮箱
     * @return 脱敏后的邮箱
     */
    public static String maskEmail(String email) {
        if (!StringUtils.hasText(email) || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) {
            return email;
        }
        return email.charAt(0) + "***" + email.substring(atIndex);
    }

    /**
     * 身份证号脱敏
     * 示例：110***********1234
     *
     * @param idCard 身份证号
     * @return 脱敏后的身份证号
     */
    public static String maskIdCard(String idCard) {
        if (!StringUtils.hasText(idCard) || idCard.length() < 15) {
            return idCard;
        }
        return idCard.substring(0, 3) + "***********" + idCard.substring(idCard.length() - 4);
    }

    /**
     * 银行卡号脱敏
     * 示例：6222 **** **** 1234
     *
     * @param bankCard 银行卡号
     * @return 脱敏后的银行卡号
     */
    public static String maskBankCard(String bankCard) {
        if (!StringUtils.hasText(bankCard) || bankCard.length() < 8) {
            return bankCard;
        }
        return bankCard.substring(0, 4) + " **** **** " + bankCard.substring(bankCard.length() - 4);
    }

    /**
     * 姓名脱敏
     * 示例：张*、张**
     *
     * @param name 姓名
     * @return 脱敏后的姓名
     */
    public static String maskName(String name) {
        if (!StringUtils.hasText(name)) {
            return name;
        }
        if (name.length() == 2) {
            return name.charAt(0) + "*";
        }
        if (name.length() > 2) {
            StringBuilder sb = new StringBuilder();
            sb.append(name.charAt(0));
            for (int i = 1; i < name.length() - 1; i++) {
                sb.append("*");
            }
            sb.append(name.charAt(name.length() - 1));
            return sb.toString();
        }
        return name;
    }

    /**
     * 地址脱敏
     * 示例：北京市朝阳区****
     *
     * @param address 地址
     * @param sensitiveSize 敏感信息长度
     * @return 脱敏后的地址
     */
    public static String maskAddress(String address, int sensitiveSize) {
        if (!StringUtils.hasText(address)) {
            return address;
        }
        int length = address.length();
        if (length <= sensitiveSize) {
            return "****";
        }
        return address.substring(0, length - sensitiveSize) + "****";
    }

    /**
     * 密码脱敏（全部替换为*）
     *
     * @param password 密码
     * @return 脱敏后的密码
     */
    public static String maskPassword(String password) {
        if (!StringUtils.hasText(password)) {
            return password;
        }
        return "******";
    }
}
