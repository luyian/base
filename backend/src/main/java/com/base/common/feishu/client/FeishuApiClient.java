package com.base.common.feishu.client;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.base.common.exception.BusinessException;
import com.base.common.feishu.config.FeishuConfig;
import com.base.common.feishu.dto.FeishuBaseResponse;
import com.base.common.feishu.service.FeishuTokenService;
import com.base.common.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 飞书 API 底层调用客户端
 * 封装 HTTP 请求，自动注入 tenant_access_token，统一响应解析
 *
 * @author base
 * @since 2026-02-11
 */
@Slf4j
@Component
public class FeishuApiClient {

    @Autowired
    private FeishuConfig feishuConfig;

    @Autowired
    private FeishuTokenService feishuTokenService;

    /**
     * 发送 POST 请求（JSON 格式）
     *
     * @param path 接口路径（不含 baseUrl）
     * @param body 请求体
     * @return 响应 JSON 对象
     */
    public JSONObject post(String path, Object body) {
        return post(path, body, (Map<String, Object>) null);
    }

    /**
     * 发送 POST 请求（JSON 格式），支持查询参数
     *
     * @param path   接口路径（不含 baseUrl）
     * @param body   请求体
     * @param params 查询参数
     * @return 响应 JSON 对象
     */
    public JSONObject post(String path, Object body, Map<String, Object> params) {
        String url = buildUrl(path, params);
        Map<String, String> headers = buildHeaders();

        String response = HttpClientUtil.postWithRetry(url, body, headers, feishuConfig.getRetry());
        JSONObject jsonObject = JSONObject.parseObject(response);
        checkResponse(jsonObject, path);
        return jsonObject;
    }

    /**
     * 发送 POST 请求并解析为指定类型
     *
     * @param path         接口路径
     * @param body         请求体
     * @param responseType 响应类型
     * @param <T>          泛型
     * @return 响应对象
     */
    public <T extends FeishuBaseResponse> T post(String path, Object body, Class<T> responseType) {
        String url = buildUrl(path, null);
        Map<String, String> headers = buildHeaders();

        String response = HttpClientUtil.postWithRetry(url, body, headers, feishuConfig.getRetry());
        T result = JSON.parseObject(response, responseType);
        if (!result.isSuccess()) {
            log.error("飞书 API 调用失败，path: {}，code: {}，msg: {}", path, result.getCode(), result.getMsg());
            throw new BusinessException("飞书 API 调用失败: " + result.getMsg());
        }
        return result;
    }

    /**
     * 上传图片到飞书
     *
     * @param file 图片文件
     * @return 响应 JSON 对象（包含 image_key）
     */
    public JSONObject uploadImage(MultipartFile file) {
        String url = feishuConfig.getBaseUrl() + "/im/v1/images";
        String token = feishuTokenService.getTenantAccessToken();

        try {
            HttpRequest request = HttpUtil.createPost(url)
                    .timeout(feishuConfig.getTimeout())
                    .header("Authorization", "Bearer " + token)
                    .form("image_type", "message")
                    .form("image", file.getBytes(), file.getOriginalFilename());

            HttpResponse response = request.execute();
            JSONObject jsonObject = JSONObject.parseObject(response.body());
            checkResponse(jsonObject, "/im/v1/images");
            return jsonObject;
        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            log.error("上传图片到飞书失败", e);
            throw new BusinessException("上传图片到飞书失败: " + e.getMessage());
        }
    }

    /**
     * 上传文件到飞书
     *
     * @param file     文件
     * @param fileType 文件类型（opus/mp4/pdf/doc/xls/ppt/stream）
     * @return 响应 JSON 对象（包含 file_key）
     */
    public JSONObject uploadFile(MultipartFile file, String fileType) {
        String url = feishuConfig.getBaseUrl() + "/im/v1/files";
        String token = feishuTokenService.getTenantAccessToken();

        try {
            HttpRequest request = HttpUtil.createPost(url)
                    .timeout(feishuConfig.getTimeout())
                    .header("Authorization", "Bearer " + token)
                    .form("file_type", fileType)
                    .form("file_name", file.getOriginalFilename())
                    .form("file", file.getBytes(), file.getOriginalFilename());

            HttpResponse response = request.execute();
            JSONObject jsonObject = JSONObject.parseObject(response.body());
            checkResponse(jsonObject, "/im/v1/files");
            return jsonObject;
        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            log.error("上传文件到飞书失败", e);
            throw new BusinessException("上传文件到飞书失败: " + e.getMessage());
        }
    }

    /**
     * 构建完整 URL
     *
     * @param path   接口路径
     * @param params 查询参数
     * @return 完整 URL
     */
    private String buildUrl(String path, Map<String, Object> params) {
        StringBuilder url = new StringBuilder(feishuConfig.getBaseUrl()).append(path);
        if (params != null && !params.isEmpty()) {
            url.append("?");
            params.forEach((key, value) -> url.append(key).append("=").append(value).append("&"));
            // 去掉末尾的 &
            url.setLength(url.length() - 1);
        }
        return url.toString();
    }

    /**
     * 构建请求头（携带 Token）
     *
     * @return 请求头
     */
    private Map<String, String> buildHeaders() {
        String token = feishuTokenService.getTenantAccessToken();
        Map<String, String> headers = new HashMap<>(4);
        headers.put("Authorization", "Bearer " + token);
        return headers;
    }

    /**
     * 检查飞书 API 响应
     *
     * @param response 响应 JSON
     * @param path     接口路径
     */
    private void checkResponse(JSONObject response, String path) {
        int code = response.getIntValue("code");
        if (code != 0) {
            String msg = response.getString("msg");
            log.error("飞书 API 调用失败，path: {}，code: {}，msg: {}", path, code, msg);
            throw new BusinessException("飞书 API 调用失败: " + msg);
        }
    }
}
