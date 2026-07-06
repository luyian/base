package com.base.ai.image;

/**
 * AI 图片生成请求上下文。
 *
 * @author base
 */
public class AiImageContext {

    private String userMessage;

    private String prompt;

    private String editInstruction;

    private String referenceImageUrl;

    private String referenceImagePrompt;

    private String referenceImageRevisedPrompt;

    private String model;

    private String size;

    private String baseUrl;

    private String adapter;

    private String apiKey;

    private Integer timeout;

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getEditInstruction() {
        return editInstruction;
    }

    public void setEditInstruction(String editInstruction) {
        this.editInstruction = editInstruction;
    }

    public String getReferenceImageUrl() {
        return referenceImageUrl;
    }

    public void setReferenceImageUrl(String referenceImageUrl) {
        this.referenceImageUrl = referenceImageUrl;
    }

    public String getReferenceImagePrompt() {
        return referenceImagePrompt;
    }

    public void setReferenceImagePrompt(String referenceImagePrompt) {
        this.referenceImagePrompt = referenceImagePrompt;
    }

    public String getReferenceImageRevisedPrompt() {
        return referenceImageRevisedPrompt;
    }

    public void setReferenceImageRevisedPrompt(String referenceImageRevisedPrompt) {
        this.referenceImageRevisedPrompt = referenceImageRevisedPrompt;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAdapter() {
        return adapter;
    }

    public void setAdapter(String adapter) {
        this.adapter = adapter;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
