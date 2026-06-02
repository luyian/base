package com.base.common.feishu.approval;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 飞书审批表单构建器
 * 根据模板 formMapping 配置将业务数据转换为飞书审批表单 JSON
 *
 * @author base
 */
@Slf4j
public class FeishuApprovalFormBuilder {

    private final JSONArray formData = new JSONArray();

    /**
     * 根据映射配置和业务数据自动构建表单
     *
     * @param formMapping  字段映射 JSON 字符串
     * @param businessData 业务数据
     * @return 飞书表单 JSON 字符串
     */
    public static String buildForm(String formMapping, Map<String, Object> businessData) {
        if (formMapping == null || formMapping.isEmpty()) {
            return "[]";
        }
        List<JSONObject> mappings = JSON.parseArray(formMapping, JSONObject.class);
        FeishuApprovalFormBuilder builder = new FeishuApprovalFormBuilder();
        for (JSONObject mapping : mappings) {
            String controlId = mapping.getString("controlId");
            String controlType = mapping.getString("controlType");
            String businessField = mapping.getString("businessField");
            Object value = businessData.get(businessField);
            if (value == null) {
                continue;
            }
            switch (controlType) {
                case "input":
                case "textarea":
                    builder.text(controlId, controlType, String.valueOf(value));
                    break;
                case "number":
                    builder.number(controlId, value);
                    break;
                case "amount":
                    String currency = mapping.getString("currency");
                    builder.amount(controlId, value, currency != null ? currency : "CNY");
                    break;
                case "date":
                    builder.date(controlId, value);
                    break;
                default:
                    builder.text(controlId, controlType, String.valueOf(value));
                    break;
            }
        }
        return builder.build();
    }

    /**
     * 添加文本/多行文本控件
     */
    public FeishuApprovalFormBuilder text(String controlId, String type, String value) {
        JSONObject item = new JSONObject();
        item.put("id", controlId);
        item.put("type", type);
        item.put("value", value);
        formData.add(item);
        return this;
    }

    /**
     * 添加数字控件
     */
    public FeishuApprovalFormBuilder number(String controlId, Object value) {
        JSONObject item = new JSONObject();
        item.put("id", controlId);
        item.put("type", "number");
        item.put("value", String.valueOf(value));
        formData.add(item);
        return this;
    }

    /**
     * 添加金额控件
     */
    public FeishuApprovalFormBuilder amount(String controlId, Object value, String currency) {
        JSONObject item = new JSONObject();
        item.put("id", controlId);
        item.put("type", "amount");
        item.put("value", String.valueOf(value));
        item.put("currency", currency);
        formData.add(item);
        return this;
    }

    /**
     * 添加日期控件
     */
    public FeishuApprovalFormBuilder date(String controlId, Object value) {
        JSONObject item = new JSONObject();
        item.put("id", controlId);
        item.put("type", "date");
        if (value instanceof Date) {
            item.put("value", new SimpleDateFormat("yyyy-MM-dd").format((Date) value));
        } else {
            item.put("value", String.valueOf(value));
        }
        formData.add(item);
        return this;
    }

    /**
     * 构建最终表单 JSON 字符串
     */
    public String build() {
        return formData.toJSONString();
    }
}
