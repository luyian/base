package com.base.common.export.mask;

import com.base.common.export.constant.MaskTypeEnum;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 邮箱脱敏器
 * 规则：保留@前的前3个字符，其余用***代替
 * 示例：abc***@example.com
 *
 * @author base
 * @since 2026-02-04
 */
@Component
public class EmailMasker implements DataMasker {

    @Override
    public String mask(String value) {
        if (!StringUtils.hasText(value) || !value.contains("@")) {
            return value;
        }
        int atIndex = value.indexOf("@");
        if (atIndex <= 3) {
            return value.substring(0, 1) + "***" + value.substring(atIndex);
        }
        return value.substring(0, 3) + "***" + value.substring(atIndex);
    }

    @Override
    public String getMaskType() {
        return MaskTypeEnum.EMAIL.getCode();
    }
}
