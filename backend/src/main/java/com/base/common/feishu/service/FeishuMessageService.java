package com.base.common.feishu.service;

import com.base.common.feishu.dto.FeishuSendMessageRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * 飞书消息发送服务接口
 *
 * @author base
 * @since 2026-02-11
 */
public interface FeishuMessageService {

    /**
     * 发送消息（指定接收者 ID）
     *
     * @param request 发送消息请求
     * @return 消息 ID
     */
    String sendMessage(FeishuSendMessageRequest request);

    /**
     * 发送消息给系统用户（通过 userId 查找绑定的飞书 open_id）
     *
     * @param userId  系统用户 ID
     * @param msgType 消息类型
     * @param content 消息内容 JSON 字符串
     * @return 消息 ID
     */
    String sendMessageToUser(Long userId, String msgType, String content);

    /**
     * 上传图片到飞书，返回 image_key
     *
     * @param file 图片文件
     * @return image_key
     */
    String uploadImage(MultipartFile file);

    /**
     * 上传文件到飞书，返回 file_key
     *
     * @param file     文件
     * @param fileType 文件类型（opus/mp4/pdf/doc/xls/ppt/stream）
     * @return file_key
     */
    String uploadFile(MultipartFile file, String fileType);
}
