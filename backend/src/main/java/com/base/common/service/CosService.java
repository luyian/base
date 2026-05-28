package com.base.common.service;

import com.base.system.service.ConfigService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import java.net.URL;
import java.util.Date;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 腾讯云 COS 文件存储服务
 * <p>
 * 配置从 sys_config 表读取，通过 ConfigService 获取（带 Redis 缓存）。
 * COSClient 延迟初始化，首次调用时创建。
 * </p>
 */
@Slf4j
@Service
public class CosService {

    private final ConfigService configService;

    private volatile COSClient cosClient;
    private volatile String cachedSecretId;

    public CosService(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * 上传文件
     *
     * @param fileBytes 文件内容
     * @param fileGroup 分组（如 default、avatar、images）
     * @param fileExt   扩展名（如 jpg、pdf），不含点号
     * @return COS 对象 key，如 "default/2026/05/28/a1b2c3d4.jpg"
     */
    public String uploadFile(byte[] fileBytes, String fileGroup, String fileExt) {
        String key = buildKey(fileGroup, fileExt);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileBytes.length);
        PutObjectRequest request = new PutObjectRequest(
                getBucket(), key, new ByteArrayInputStream(fileBytes), metadata);
        getClient().putObject(request);
        log.info("COS 上传成功: {}", key);
        return key;
    }

    /**
     * 下载文件
     *
     * @param key COS 对象 key
     * @return 文件字节数组
     */
    public byte[] downloadFile(String key) {
        GetObjectRequest request = new GetObjectRequest(getBucket(), key);
        COSObject cosObject = getClient().getObject(request);
        try (InputStream is = cosObject.getObjectContent();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("COS 下载失败: {}", key, e);
            throw new RuntimeException("文件下载失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param key COS 对象 key
     */
    public void deleteFile(String key) {
        if (key == null || key.isEmpty()) {
            return;
        }
        try {
            getClient().deleteObject(getBucket(), key);
            log.info("COS 删除成功: {}", key);
        } catch (Exception e) {
            log.warn("COS 删除失败: {}, 原因: {}", key, e.getMessage());
        }
    }

    /**
     * 获取文件预签名访问 URL（1 小时有效）
     *
     * @param key COS 对象 key
     * @return 带签名的临时访问 URL
     */
    public String getFileUrl(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }
        Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000L);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(getBucket(), key);
        request.setExpiration(expiration);
        URL url = getClient().generatePresignedUrl(request);
        return url.toString();
    }

    /**
     * 获取或创建 COSClient（延迟初始化，配置变更时自动重建）
     */
    private COSClient getClient() {
        String secretId = getConfigValue("cos.secret.id");
        if (cosClient == null || !secretId.equals(cachedSecretId)) {
            synchronized (this) {
                if (cosClient == null || !secretId.equals(cachedSecretId)) {
                    if (cosClient != null) {
                        cosClient.shutdown();
                    }
                    String secretKey = getConfigValue("cos.secret.key");
                    String region = getConfigValue("cos.region");
                    COSCredentials credentials = new BasicCOSCredentials(secretId, secretKey);
                    ClientConfig clientConfig = new ClientConfig(new Region(region));
                    clientConfig.setHttpProtocol(HttpProtocol.https);
                    cosClient = new COSClient(credentials, clientConfig);
                    cachedSecretId = secretId;
                    log.info("COSClient 初始化完成, region: {}", region);
                }
            }
        }
        return cosClient;
    }

    private String getBucket() {
        return getConfigValue("cos.bucket");
    }

    private String getConfigValue(String key) {
        String value = configService.getConfigValueByKey(key);
        if (value == null || value.isEmpty()) {
            throw new RuntimeException("COS 配置缺失: " + key);
        }
        return value;
    }

    /**
     * 构建存储路径: fileGroup/yyyy/MM/dd/uuid.ext
     */
    private String buildKey(String fileGroup, String fileExt) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String ext = (fileExt != null && !fileExt.isEmpty()) ? "." + fileExt : "";
        return fileGroup + "/" + datePath + "/" + uuid + ext;
    }
}
