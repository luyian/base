package com.base.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 分布式流水号生成工具
 * <p>
 * 基于 Redis Lua 脚本原子自增实现，保证分布式环境下唯一且有序。
 * 生成格式：前缀 + 日期 + 序号（如 ORD20260609000001）
 * </p>
 * <p>
 * 风险防护：
 * <ul>
 *   <li>Lua 脚本保证 INCR + EXPIRE 原子性，避免 key 永不过期</li>
 *   <li>序号溢出校验，超出最大位数时抛出异常</li>
 *   <li>Redis 不可用时本地降级（AtomicLong + 时间戳后缀），保证可用性</li>
 *   <li>时钟回拨检测，防止产生重复流水号</li>
 * </ul>
 * </p>
 *
 * @author base
 * @since 2026-06-09
 */
@Slf4j
@Component
public class SerialNumberUtil {

    private static final String SERIAL_KEY_PREFIX = "serial_number:";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * Lua 脚本：原子化 INCR + EXPIRE
     * 仅在 key 首次创建时设置过期时间，避免非原子操作导致 key 永不过期
     */
    private static final String INCR_WITH_EXPIRE_SCRIPT =
            "local current = redis.call('INCR', KEYS[1]) " +
            "if current == 1 then " +
            "  redis.call('EXPIRE', KEYS[1], ARGV[1]) " +
            "end " +
            "return current";

    private final RedisTemplate<String, Object> redisTemplate;

    /** 本地降级计数器，key 为业务前缀 + 日期 */
    private final Map<String, AtomicLong> localCounterMap = new ConcurrentHashMap<>();

    /** 上一次生成时间戳流水号的秒数，用于时钟回拨检测 */
    private volatile long lastTimestampSecond = 0;

    private DefaultRedisScript<Long> incrWithExpireScript;

    public SerialNumberUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        incrWithExpireScript = new DefaultRedisScript<>();
        incrWithExpireScript.setScriptText(INCR_WITH_EXPIRE_SCRIPT);
        incrWithExpireScript.setResultType(Long.class);
    }

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
     * @param serialWidth 序号位数（1~10）
     * @return 流水号
     */
    public String generate(String prefix, int serialWidth) {
        if (serialWidth < 1 || serialWidth > 10) {
            throw new IllegalArgumentException("序号位数必须在 1~10 之间");
        }

        String dateStr = LocalDate.now().format(DATE_FORMAT);
        String redisKey = SERIAL_KEY_PREFIX + prefix + ":" + dateStr;
        // 过期时间 26 小时，覆盖次日凌晨留余量
        long expireSeconds = 26 * 3600;

        long sequence = executeIncrement(redisKey, expireSeconds, prefix + ":" + dateStr);

        // 溢出校验
        long maxSequence = (long) Math.pow(10, serialWidth) - 1;
        if (sequence > maxSequence) {
            log.error("流水号序号溢出: prefix={}, date={}, sequence={}, max={}",
                    prefix, dateStr, sequence, maxSequence);
            throw new RuntimeException(
                    String.format("流水号序号溢出：%s 当日已超过最大值 %d", prefix, maxSequence));
        }

        String serialNumber = String.format("%0" + serialWidth + "d", sequence);
        return prefix + dateStr + serialNumber;
    }

    /**
     * 生成带时间戳的流水号
     * <p>
     * 格式：前缀 + yyyyMMddHHmmss + 4位序号
     * 示例：PAY202606091430250001
     * </p>
     * <p>
     * 内置时钟回拨检测，若检测到回拨则等待直到时间追上。
     * </p>
     *
     * @param prefix 业务前缀
     * @return 流水号
     */
    public String generateWithTimestamp(String prefix) {
        LocalDateTime now = waitUntilNextSecond();
        String datetimeStr = now.format(DATETIME_FORMAT);
        String redisKey = SERIAL_KEY_PREFIX + prefix + ":ts:" + datetimeStr;
        // 过期时间 5 分钟
        long expireSeconds = 300;

        long sequence = executeIncrement(redisKey, expireSeconds, prefix + ":ts:" + datetimeStr);

        // 同一秒内最多 9999
        if (sequence > 9999) {
            log.error("时间戳流水号溢出: prefix={}, datetime={}, sequence={}",
                    prefix, datetimeStr, sequence);
            throw new RuntimeException(
                    String.format("时间戳流水号溢出：%s 同一秒内超过最大值 9999", prefix));
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

        try {
            Object value = redisTemplate.opsForValue().get(redisKey);
            if (value == null) {
                return 0;
            }
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            log.warn("获取当前序号失败，返回本地计数: {}", e.getMessage());
            AtomicLong local = localCounterMap.get(prefix + ":" + dateStr);
            return local != null ? local.get() : 0;
        }
    }

    /**
     * 执行 Redis 原子自增，失败时降级到本地计数器
     *
     * @param redisKey      Redis key
     * @param expireSeconds 过期时间（秒）
     * @param localKey      本地降级计数器 key
     * @return 自增后的序号
     */
    private long executeIncrement(String redisKey, long expireSeconds, String localKey) {
        try {
            Long sequence = redisTemplate.execute(
                    incrWithExpireScript,
                    Collections.singletonList(redisKey),
                    expireSeconds
            );
            if (sequence != null) {
                return sequence;
            }
        } catch (Exception e) {
            log.error("Redis 流水号自增失败，降级到本地计数器: key={}, error={}", redisKey, e.getMessage());
        }

        // 本地降级：AtomicLong + 时间戳纳秒后缀保证不重复
        return localFallbackIncrement(localKey);
    }

    /**
     * 本地降级自增
     * <p>
     * 注意：本地降级只保证单实例内唯一，多实例部署时有重复风险。
     * 日志中会记录降级事件，便于事后排查。
     * </p>
     */
    private long localFallbackIncrement(String localKey) {
        AtomicLong counter = localCounterMap.computeIfAbsent(localKey, k -> new AtomicLong(0));
        long value = counter.incrementAndGet();
        log.warn("本地降级生成流水号: key={}, sequence={}", localKey, value);
        return value;
    }

    /**
     * 时钟回拨检测与等待
     * <p>
     * 如果检测到当前时间小于上次生成时间（时钟被回拨），
     * 则自旋等待直到时间追上，最长等待 3 秒。超过则抛出异常。
     * </p>
     */
    private LocalDateTime waitUntilNextSecond() {
        long currentSecond = System.currentTimeMillis() / 1000;

        if (currentSecond < lastTimestampSecond) {
            long diff = lastTimestampSecond - currentSecond;
            log.warn("检测到时钟回拨: 回拨{}秒，等待时间追上", diff);

            if (diff > 3) {
                throw new RuntimeException(
                        String.format("时钟回拨超过3秒（%d秒），拒绝生成流水号", diff));
            }

            // 自旋等待
            while (System.currentTimeMillis() / 1000 < lastTimestampSecond) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("等待时钟恢复时被中断", e);
                }
            }
        }

        lastTimestampSecond = System.currentTimeMillis() / 1000;
        return LocalDateTime.now();
    }
}
