package com.base.common.export.mask;

import com.base.common.export.constant.MaskTypeEnum;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 银行卡脱敏器
 * 规则：保留前4位和后4位，中间用****代替
 * 示例：6222****1234
 *
 * @author base
 * @since 2026-02-04
 */
@Component
public class BankCardMasker implements DataMasker {

    @Override
    public String mask(String value) {
        if (!StringUtils.hasText(value) || value.length() < 8) {
            return value;
        }
        return value.substring(0, 4) + "****" + value.substring(value.length() - 4);
    }

    @Override
    public String getMaskType() {
        return MaskTypeEnum.BANK_CARD.getCode();
    }
}
