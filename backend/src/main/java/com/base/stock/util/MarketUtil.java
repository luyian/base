package com.base.stock.util;

/**
 * 市场（交易所）推断与转换工具类
 *
 * <p>统一股票代码到市场的推断逻辑，覆盖沪市(SH)、深市(SZ)、北证(BJ)、港股(HK)，
 * 避免各处重复实现导致的判断不一致（尤其是北证支持缺失）。</p>
 *
 * <p>A股代码前缀规则：</p>
 * <ul>
 *     <li>沪市 SH：60/68(科创板) 开头</li>
 *     <li>深市 SZ：00/30 开头</li>
 *     <li>北证 BJ：4(43)/8(83/87/88) 开头，或 920 开头</li>
 *     <li>港股 HK：纯 5 位数字，或 HK 前缀</li>
 * </ul>
 *
 * @author base
 */
public final class MarketUtil {

    /** 沪市 */
    public static final String SH = "SH";
    /** 深市 */
    public static final String SZ = "SZ";
    /** 北证（北京证券交易所） */
    public static final String BJ = "BJ";
    /** 港股 */
    public static final String HK = "HK";

    private MarketUtil() {
    }

    /**
     * 根据股票代码推断所属市场
     *
     * @param code 股票代码
     * @return 市场标识（SH/SZ/BJ/HK），无法识别时默认返回 SZ
     */
    public static String inferMarket(String code) {
        if (code == null || code.trim().isEmpty()) {
            return SZ;
        }
        String c = code.trim().toUpperCase();
        // 港股：HK 前缀或纯 5 位数字（如 00700、09988）
        if (c.startsWith("HK")) {
            return HK;
        }
        if (c.length() == 5 && isAllDigits(c)) {
            return HK;
        }
        // 北证：43 开头（基础层）、83/87/88 开头（创新层）、920 开头（新发）
        if (c.startsWith("43") || c.startsWith("83") || c.startsWith("87")
                || c.startsWith("88") || c.startsWith("920")) {
            return BJ;
        }
        // 沪市：60(主板)、68(科创板)
        if (c.startsWith("60") || c.startsWith("68")) {
            return SH;
        }
        // 深市：00(主板)、30(创业板)
        if (c.startsWith("00") || c.startsWith("30")) {
            return SZ;
        }
        return SZ;
    }

    /**
     * 是否为北证代码
     *
     * @param code 股票代码
     * @return true 表示北证
     */
    public static boolean isBeijing(String code) {
        return BJ.equals(inferMarket(code));
    }

    /**
     * 转换为东方财富 secid 的市场前缀
     *
     * <p>东方财富市场编码：1=上海，0=深圳/北京。</p>
     *
     * @param market 市场标识（SH/SZ/BJ/HK）
     * @return secid 前缀（"1." 或 "0."）
     */
    public static String toEastMoneySecidPrefix(String market) {
        return SH.equalsIgnoreCase(market) ? "1." : "0.";
    }

    /**
     * 转换为腾讯财经接口的市场前缀
     *
     * <p>腾讯接口前缀：sh/sz/bj/hk。</p>
     *
     * @param market 市场标识（SH/SZ/BJ/HK）
     * @return 接口前缀（小写）
     */
    public static String toTencentPrefix(String market) {
        if (SH.equalsIgnoreCase(market)) {
            return "sh";
        }
        if (BJ.equalsIgnoreCase(market)) {
            return "bj";
        }
        if (HK.equalsIgnoreCase(market)) {
            return "hk";
        }
        return "sz";
    }

    /**
     * 判断字符串是否全部为数字
     *
     * @param str 待判断字符串
     * @return true 表示全为数字
     */
    private static boolean isAllDigits(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
