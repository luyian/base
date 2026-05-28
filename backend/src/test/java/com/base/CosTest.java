package com.base;

import com.base.common.service.CosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * 腾讯云 COS 集成测试
 */
@SpringBootTest
public class CosTest {

    @Autowired
    private CosService cosService;

    @Test
    void testUploadBytes() {
        byte[] content = "Hello COS!".getBytes();
        String key = cosService.uploadFile(content, "test", "txt");
        System.out.println("上传成功, key: " + key);
        System.out.println("访问地址: " + cosService.getFileUrl(key));
    }

    @Test
    void testUploadFile() throws IOException {
        File localFile = new File("D:\\img\\1.jpg");
        if (!localFile.exists()) {
            System.out.println("文件不存在: " + localFile.getAbsolutePath());
            return;
        }
        byte[] fileBytes = Files.readAllBytes(localFile.toPath());
        String key = cosService.uploadFile(fileBytes, "test", "jpg");
        System.out.println("上传成功, key: " + key);
        System.out.println("访问地址: " + cosService.getFileUrl(key));
    }

    @Test
    void testDownload() {
        String key = "test/hello.txt";
        byte[] data = cosService.downloadFile(key);
        System.out.println("下载成功, 内容: " + new String(data));
    }

    @Test
    void testDownloadImage() throws IOException {
        String key = "test/photo.jpg";
        byte[] data = cosService.downloadFile(key);
        File localFile = new File("D:\\img\\download.jpg");
        Files.write(localFile.toPath(), data);
        System.out.println("下载成功: " + localFile.getAbsolutePath() + " | 大小: " + data.length + " bytes");
    }

    @Test
    void testDelete() {
        String key = "test/hello.txt";
        cosService.deleteFile(key);
        System.out.println("删除成功: " + key);
    }

    @Test
    void testGetFileUrl() {
        String key = "test/photo.jpg";
        String url = cosService.getFileUrl(key);
        System.out.println("访问地址: " + url);
    }
}
