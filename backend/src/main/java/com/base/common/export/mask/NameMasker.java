package com.base.common.export.mask;

import com.base.common.export.constant.MaskTypeEnum;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 姓名脱敏器
 * 规则：保留第一个字，其余用*代替
 * 示例：张** 或 李*
 *
 * @author base
 * @since 2026-02-04
 */
@Component
public class NameMasker implements DataMasker {

    @Override
    public String mask(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }
        if (value.length() == 1) {
            return value;
        }
        if (value.length() == 2) {
            return value.substring(0, 1) + "*";
        }
        StringBuilder sb = new StringBuilder(value.substring(0, 1));
        for (int i = 1; i < value.length(); i++) {
            sb.append("*");
        }
        return sb.toString();
    }

    @Override
    public String getMaskType() {
        return MaskTypeEnum.NAME.getCode();
    }
}
