package com.base.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 分布式流水号生成工具
 * <p>
 * 基于 Redis 自增实现，保证分布式环境下唯一且有序。
 * 生成格式：前缀 + 日期 + 序号（如 ORD20260609000001）
 * </p>
 *
 * @author base
 * @since 2026-06-09
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SerialNumberUtil {

    private static final String SERIAL_KEY_PREFIX = "serial_number:";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 生成流水号（默认6位序号）
     * <p>
     * 格式：前缀 + yyyyMMdd + 6位序号
     * 示例：ORD20260609000001
     * </p>
     *
     * @param prefix 业务前缀（如 ORD、PAY、INV）
     * @return 流水号
     */
    public String generate(String prefix) {
        return generate(prefix, 6);
    }

    /**
     * 生成流水号（自定义序号位数）
     *
     * @param prefix      业务前缀
     * @param serialWidth 序号位数
     * @return 流水号
     */
    public String generate(String prefix, int serialWidth) {
        String dateStr = LocalDate.now().format(DATE_FORMAT);
        String redisKey = SERIAL_KEY_PREFIX + prefix + ":" + dateStr;

        Long sequence = redisTemplate.opsForValue().increment(redisKey, 1);
        if (sequence == null) {
            throw new RuntimeException("流水号生成失败：Redis 自增返回空值");
        }

        // 首次生成时设置过期时间为次日凌晨后2小时（留余量）
        if (sequence == 1) {
            redisTemplate.expire(redisKey, 26, TimeUnit.HOURS);
        }

        String serialNumber = String.format("%0" + serialWidth + "d", sequence);
        return prefix + dateStr + serialNumber;
    }

    /**
     * 生成带时间戳的流水号
     * <p>
     * 格式：前缀 + yyyyMMddHHmmss + 4位序号
     * 示例：PAY20260609143025_0001
     * </p>
     *
     * @param prefix 业务前缀
     * @return 流水号
     */
    public String generateWithTimestamp(String prefix) {
        String datetimeStr = LocalDateTime.now().format(DATETIME_FORMAT);
        String redisKey = SERIAL_KEY_PREFIX + prefix + ":ts:" + datetimeStr;

        Long sequence = redisTemplate.opsForValue().increment(redisKey, 1);
        if (sequence == null) {
            throw new RuntimeException("流水号生成失败：Redis 自增返回空值");
        }

        // 设置短过期时间，避免 key 堆积
        if (sequence == 1) {
            redisTemplate.expire(redisKey, 5, TimeUnit.MINUTES);
        }

        String serialNumber = String.format("%04d", sequence);
        return prefix + datetimeStr + serialNumber;
    }

    /**
     * 获取当前序号值（不自增）
     *
     * @param prefix 业务前缀
     * @return 当前序号值，未初始化时返回 0
     */
    public long getCurrentSequence(String prefix) {
        String dateStr = LocalDate.now().format(DATE_FORMAT);
        String redisKey = SERIAL_KEY_PREFIX + prefix + ":" + dateStr;

        Object value = redisTemplate.opsForValue().get(redisKey);
        if (value == null) {
            return 0;
        }
        return Long.parseLong(value.toString());
    }
}
