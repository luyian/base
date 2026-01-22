package com.base.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.base.system.dto.monitor.CacheInfoResponse;
import com.base.system.dto.monitor.CacheKeyResponse;
import com.base.system.dto.monitor.ServerInfoResponse;
import com.base.system.service.MonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 系统监控服务实现类
 *
 * @author base
 * @date 2026-01-14
 */
@Slf4j
@Service
public class MonitorServiceImpl implements MonitorService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final DecimalFormat DF = new DecimalFormat("0.00");

    @Override
    public ServerInfoResponse getServerInfo() {
        ServerInfoResponse response = new ServerInfoResponse();

        try {
            SystemInfo systemInfo = new SystemInfo();
            HardwareAbstractionLayer hardware = systemInfo.getHardware();
            OperatingSystem os = systemInfo.getOperatingSystem();

            // CPU信息
            response.setCpu(getCpuInfo(hardware.getProcessor()));

            // 内存信息
            response.setMemory(getMemoryInfo(hardware.getMemory()));

            // JVM信息
            response.setJvm(getJvmInfo());

            // 系统信息
            response.setSystem(getSystemInfo(os));

            // 磁盘信息
            response.setDisks(getDiskInfo(os.getFileSystem()));

        } catch (Exception e) {
            log.error("获取服务器信息失败", e);
        }

        return response;
    }

    @Override
    public CacheInfoResponse getCacheInfo() {
        CacheInfoResponse response = new CacheInfoResponse();

        try {
            Properties info = redisTemplate.execute((RedisCallback<Properties>) RedisServerCommands::info);
            Properties commandStats = redisTemplate.execute((RedisCallback<Properties>) connection ->
                    connection.info("commandstats"));

            if (info != null) {
                // Redis版本
                response.setVersion(info.getProperty("redis_version"));

                // 运行模式
                String mode = "standalone";
                if ("cluster".equals(info.getProperty("redis_mode"))) {
                    mode = "cluster";
                } else if ("sentinel".equals(info.getProperty("redis_mode"))) {
                    mode = "sentinel";
                }
                response.setMode(mode);

                // 端口
                response.setPort(info.getProperty("tcp_port"));

                // 客户端连接数
                String clients = info.getProperty("connected_clients");
                response.setClients(clients != null ? Integer.parseInt(clients) : 0);

                // 已用内存（转换为MB）
                String usedMemory = info.getProperty("used_memory");
                if (usedMemory != null) {
                    response.setUsedMemory(Double.parseDouble(DF.format(Long.parseLong(usedMemory) / 1024.0 / 1024.0)));
                }

                // 最大内存（转换为MB）
                String maxMemory = info.getProperty("maxmemory");
                if (maxMemory != null && !"0".equals(maxMemory)) {
                    response.setMaxMemory(Double.parseDouble(DF.format(Long.parseLong(maxMemory) / 1024.0 / 1024.0)));
                } else {
                    response.setMaxMemory(0.0);
                }

                // 内存使用率
                if (response.getMaxMemory() > 0) {
                    double percent = (response.getUsedMemory() / response.getMaxMemory()) * 100;
                    response.setMemoryUsedPercent(Double.parseDouble(DF.format(percent)));
                } else {
                    response.setMemoryUsedPercent(0.0);
                }

                // 键总数
                Long dbSize = redisTemplate.execute((RedisCallback<Long>) RedisServerCommands::dbSize);
                response.setKeyCount(dbSize != null ? dbSize : 0L);

                // 命中率统计
                String hits = info.getProperty("keyspace_hits");
                String misses = info.getProperty("keyspace_misses");
                long hitsCount = hits != null ? Long.parseLong(hits) : 0L;
                long missesCount = misses != null ? Long.parseLong(misses) : 0L;
                response.setHits(hitsCount);
                response.setMisses(missesCount);

                long total = hitsCount + missesCount;
                if (total > 0) {
                    double hitRate = (hitsCount * 100.0) / total;
                    response.setHitRate(Double.parseDouble(DF.format(hitRate)));
                } else {
                    response.setHitRate(0.0);
                }

                // 运行时长（秒）
                String uptime = info.getProperty("uptime_in_seconds");
                if (uptime != null) {
                    long uptimeSeconds = Long.parseLong(uptime);
                    response.setUptime(uptimeSeconds);
                    response.setUptimeFormatted(formatUptime(uptimeSeconds));
                }
            }

        } catch (Exception e) {
            log.error("获取缓存信息失败", e);
        }

        return response;
    }

    @Override
    public List<String> getCacheKeys(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern != null ? pattern : "*");
            return keys != null ? new ArrayList<>(keys) : new ArrayList<>();
        } catch (Exception e) {
            log.error("获取缓存键列表失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public CacheKeyResponse getCacheValue(String key) {
        CacheKeyResponse response = new CacheKeyResponse();
        response.setKey(key);

        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                // 将对象转换为JSON字符串
                if (value instanceof String) {
                    response.setValue((String) value);
                } else {
                    response.setValue(JSON.toJSONString(value));
                }
            }

            // 获取过期时间
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            response.setTtl(expire != null ? expire : -1L);

        } catch (Exception e) {
            log.error("获取缓存值失败: {}", key, e);
        }

        return response;
    }

    @Override
    public Boolean deleteCacheKey(String key) {
        try {
            return redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("删除缓存键失败: {}", key, e);
            return false;
        }
    }

    @Override
    public Boolean clearCache() {
        try {
            Set<String> keys = redisTemplate.keys("*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
            return true;
        } catch (Exception e) {
            log.error("清空缓存失败", e);
            return false;
        }
    }

    /**
     * 获取CPU信息
     */
    private ServerInfoResponse.CpuInfo getCpuInfo(CentralProcessor processor) {
        ServerInfoResponse.CpuInfo cpuInfo = new ServerInfoResponse.CpuInfo();

        // CPU核心数
        cpuInfo.setCoreCount(processor.getLogicalProcessorCount());

        // CPU型号
        cpuInfo.setModel(processor.getProcessorIdentifier().getName());

        // CPU使用率
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        try {
            // 等待1秒以获取准确的CPU使用率
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;

        double usedPercent = 100.0 * (totalCpu - idle) / totalCpu;
        cpuInfo.setUsedPercent(Double.parseDouble(DF.format(usedPercent)));

        return cpuInfo;
    }

    /**
     * 获取内存信息
     */
    private ServerInfoResponse.MemoryInfo getMemoryInfo(GlobalMemory memory) {
        ServerInfoResponse.MemoryInfo memoryInfo = new ServerInfoResponse.MemoryInfo();

        long total = memory.getTotal();
        long available = memory.getAvailable();
        long used = total - available;

        // 转换为GB
        memoryInfo.setTotal(Double.parseDouble(DF.format(total / 1024.0 / 1024.0 / 1024.0)));
        memoryInfo.setUsed(Double.parseDouble(DF.format(used / 1024.0 / 1024.0 / 1024.0)));
        memoryInfo.setAvailable(Double.parseDouble(DF.format(available / 1024.0 / 1024.0 / 1024.0)));

        // 使用率
        double usedPercent = (used * 100.0) / total;
        memoryInfo.setUsedPercent(Double.parseDouble(DF.format(usedPercent)));

        return memoryInfo;
    }

    /**
     * 获取JVM信息
     */
    private ServerInfoResponse.JvmInfo getJvmInfo() {
        ServerInfoResponse.JvmInfo jvmInfo = new ServerInfoResponse.JvmInfo();

        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();
        long used = total - free;
        long max = runtime.maxMemory();

        // 转换为MB
        jvmInfo.setTotal(Double.parseDouble(DF.format(total / 1024.0 / 1024.0)));
        jvmInfo.setUsed(Double.parseDouble(DF.format(used / 1024.0 / 1024.0)));
        jvmInfo.setAvailable(Double.parseDouble(DF.format((max - used) / 1024.0 / 1024.0)));

        // 使用率
        double usedPercent = (used * 100.0) / max;
        jvmInfo.setUsedPercent(Double.parseDouble(DF.format(usedPercent)));

        // JVM版本
        jvmInfo.setVersion(System.getProperty("java.version"));

        // JVM启动时间和运行时长
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        long startTime = runtimeMXBean.getStartTime();
        long uptime = runtimeMXBean.getUptime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        jvmInfo.setStartTime(LocalDateTime.now().minusSeconds(uptime / 1000).format(formatter));
        jvmInfo.setRunTime(formatUptime(uptime / 1000));

        return jvmInfo;
    }

    /**
     * 获取系统信息
     */
    private ServerInfoResponse.SystemInfo getSystemInfo(OperatingSystem os) {
        ServerInfoResponse.SystemInfo systemInfo = new ServerInfoResponse.SystemInfo();

        // 操作系统
        systemInfo.setOs(os.toString());

        // 系统架构
        systemInfo.setArch(System.getProperty("os.arch"));

        // 主机名和IP地址
        try {
            InetAddress addr = InetAddress.getLocalHost();
            systemInfo.setHostName(addr.getHostName());
            systemInfo.setIpAddress(addr.getHostAddress());
        } catch (UnknownHostException e) {
            log.error("获取主机信息失败", e);
            systemInfo.setHostName("Unknown");
            systemInfo.setIpAddress("Unknown");
        }

        return systemInfo;
    }

    /**
     * 获取磁盘信息
     */
    private List<ServerInfoResponse.DiskInfo> getDiskInfo(FileSystem fileSystem) {
        List<ServerInfoResponse.DiskInfo> diskInfoList = new ArrayList<>();

        List<OSFileStore> fileStores = fileSystem.getFileStores();
        for (OSFileStore fs : fileStores) {
            long total = fs.getTotalSpace();
            long usable = fs.getUsableSpace();
            long used = total - usable;

            // 过滤掉总空间为0的磁盘
            if (total == 0) {
                continue;
            }

            ServerInfoResponse.DiskInfo diskInfo = new ServerInfoResponse.DiskInfo();
            diskInfo.setPath(fs.getMount());
            diskInfo.setName(fs.getName());

            // 转换为GB
            diskInfo.setTotal(Double.parseDouble(DF.format(total / 1024.0 / 1024.0 / 1024.0)));
            diskInfo.setUsed(Double.parseDouble(DF.format(used / 1024.0 / 1024.0 / 1024.0)));
            diskInfo.setAvailable(Double.parseDouble(DF.format(usable / 1024.0 / 1024.0 / 1024.0)));

            // 使用率
            double usedPercent = (used * 100.0) / total;
            diskInfo.setUsedPercent(Double.parseDouble(DF.format(usedPercent)));

            diskInfoList.add(diskInfo);
        }

        return diskInfoList;
    }

    /**
     * 格式化运行时长
     *
     * @param seconds 秒数
     * @return 格式化后的时长
     */
    private String formatUptime(long seconds) {
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("天");
        }
        if (hours > 0) {
            sb.append(hours).append("小时");
        }
        if (minutes > 0) {
            sb.append(minutes).append("分钟");
        }
        if (secs > 0 || sb.length() == 0) {
            sb.append(secs).append("秒");
        }

        return sb.toString();
    }
}
