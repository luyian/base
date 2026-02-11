package com.base;

import com.base.common.feishu.service.FeishuMessageService;
import com.base.common.feishu.service.FeishuTokenService;
import com.base.common.feishu.dto.FeishuSendMessageRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.system.entity.UserOauth;
import com.base.system.mapper.UserOauthMapper;
import com.base.system.BaseSystemApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

/**
 * 飞书消息发送功能测试
 * 需要确保飞书配置正确且 admin 用户已绑定 open_id
 *
 * @author base
 * @since 2026-02-11
 */
@SpringBootTest(classes = BaseSystemApplication.class)
public class FeishuMessageTest {

    @Autowired
    private FeishuTokenService feishuTokenService;

    @Autowired
    private FeishuMessageService feishuMessageService;

    @Autowired
    private UserOauthMapper userOauthMapper;

    /**
     * 测试获取 tenant_access_token
     */
    @Test
    public void testGetToken() {
        String token = feishuTokenService.getTenantAccessToken();
        System.out.println("获取到 tenant_access_token: " + token);
        assert token != null && !token.isEmpty() : "Token 不应为空";
    }

    /**
     * 测试查询 admin 用户绑定的飞书 open_id
     */
    @Test
    public void testQueryBindInfo() {
        LambdaQueryWrapper<UserOauth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOauth::getOauthType, "feishu");
        UserOauth userOauth = userOauthMapper.selectOne(wrapper);
        if (userOauth != null) {
            System.out.println("用户ID: " + userOauth.getUserId());
            System.out.println("飞书 open_id: " + userOauth.getOauthId());
            System.out.println("飞书用户名: " + userOauth.getOauthName());
        } else {
            System.out.println("未找到飞书绑定记录，请先在个人中心绑定飞书 open_id");
        }
    }

    /**
     * 测试通过 open_id 直接发送文字消息
     */
    @Test
    public void testSendTextByOpenId() {
        // 查询绑定的 open_id
        String openId = getBindOpenId();
        if (openId == null) {
            System.out.println("跳过：未找到飞书绑定记录");
            return;
        }

        FeishuSendMessageRequest request = new FeishuSendMessageRequest();
        request.setReceiveId(openId);
        request.setReceiveIdType("open_id");
        request.setMsgType("text");
        request.setContent("{\"text\": \"这是一条来自系统的测试消息 - 直接指定 open_id 发送\"}");

        String messageId = feishuMessageService.sendMessage(request);
        System.out.println("发送成功，messageId: " + messageId);
    }

    /**
     * 测试通过系统 userId 发送文字消息（自动查找绑定的 open_id）
     */
    @Test
    public void testSendTextByUserId() {
        // 查询绑定记录获取 userId
        LambdaQueryWrapper<UserOauth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOauth::getOauthType, "feishu");
        UserOauth userOauth = userOauthMapper.selectOne(wrapper);
        if (userOauth == null) {
            System.out.println("跳过：未找到飞书绑定记录");
            return;
        }

        Long userId = userOauth.getUserId();
        String messageId = feishuMessageService.sendMessageToUser(
                userId, "text", "{\"text\": \"这是一条来自系统的测试消息 - 通过 userId 发送\"}");
        System.out.println("发送成功，userId: " + userId + "，messageId: " + messageId);
    }

    /**
     * 获取已绑定的飞书 open_id
     */
    private String getBindOpenId() {
        LambdaQueryWrapper<UserOauth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOauth::getOauthType, "feishu");
        UserOauth userOauth = userOauthMapper.selectOne(wrapper);
        return userOauth != null ? userOauth.getOauthId() : null;
    }

    /**
     * 测试上传图片并发送图片消息
     * 需要在 D:/upload/ 目录下放一张测试图片 test.png
     */
    @Test
    public void testSendImage() throws IOException {
        String openId = getBindOpenId();
        if (openId == null) {
            System.out.println("跳过：未找到飞书绑定记录");
            return;
        }

        // 读取本地测试图片
        File imageFile = new File("D:/upload/test.png");
        if (!imageFile.exists()) {
            System.out.println("跳过：测试图片不存在，请在 D:/upload/ 下放一张 test.png");
            return;
        }

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", imageFile.getName(), "image/png",
                new FileInputStream(imageFile));

        // 第一步：上传图片到飞书
        String imageKey = feishuMessageService.uploadImage(multipartFile);
        System.out.println("图片上传成功，imageKey: " + imageKey);

        // 第二步：发送图片消息
        FeishuSendMessageRequest request = new FeishuSendMessageRequest();
        request.setReceiveId(openId);
        request.setReceiveIdType("open_id");
        request.setMsgType("image");
        request.setContent("{\"image_key\": \"" + imageKey + "\"}");

        String messageId = feishuMessageService.sendMessage(request);
        System.out.println("图片消息发送成功，messageId: " + messageId);
    }

    /**
     * 测试上传文件并发送文件消息
     * 需要在 D:/upload/ 目录下放一个测试文件 test.pdf
     */
    @Test
    public void testSendFile() throws IOException {
        String openId = getBindOpenId();
        if (openId == null) {
            System.out.println("跳过：未找到飞书绑定记录");
            return;
        }

        // 读取本地测试文件
        File testFile = new File("D:/upload/test.pdf");
        if (!testFile.exists()) {
            System.out.println("跳过：测试文件不存在，请在 D:/upload/ 下放一个 test.pdf");
            return;
        }

        String contentType = Files.probeContentType(testFile.toPath());
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", testFile.getName(),
                contentType != null ? contentType : "application/octet-stream",
                new FileInputStream(testFile));

        // 第一步：上传文件到飞书
        String fileKey = feishuMessageService.uploadFile(multipartFile, "stream");
        System.out.println("文件上传成功，fileKey: " + fileKey);

        // 第二步：发送文件消息
        FeishuSendMessageRequest request = new FeishuSendMessageRequest();
        request.setReceiveId(openId);
        request.setReceiveIdType("open_id");
        request.setMsgType("file");
        request.setContent("{\"file_key\": \"" + fileKey + "\"}");

        String messageId = feishuMessageService.sendMessage(request);
        System.out.println("文件消息发送成功，messageId: " + messageId);
    }
}
