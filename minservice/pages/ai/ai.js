// pages/ai/ai.js - AI 助手页面
const aiApi = require('../../api/ai');
const app = getApp();

Page({
  data: {
    mode: 'stock',
    messages: [],
    inputValue: '',
    loading: false,
    scrollToId: '',
    quickQuestions: [],
    imageSize: '1024x1024',
    imageSizeOptions: [
      { label: '方图', value: '1024x1024' },
      { label: '横图', value: '1536x1024' },
      { label: '竖图', value: '1024x1536' }
    ],
    msgIdCounter: 0,
    userInfo: null
  },

  onLoad() {
    this.updateQuickQuestions();
    const userInfo = wx.getStorageSync('userInfo') || app.globalData.userInfo;
    this.setData({ userInfo });
  },

  switchMode(e) {
    const mode = e.currentTarget.dataset.mode;
    if (mode === this.data.mode) return;
    this.setData({
      mode,
      messages: [],
      inputValue: ''
    });
    this.updateQuickQuestions();
  },

  updateQuickQuestions() {
    const stockQuestions = [
      '查询贵州茅台行情',
      '今日北向资金流向',
      '行业板块涨跌排名',
      '今日强势股有哪些'
    ];
    const systemQuestions = [
      '查看服务器状态',
      '今日登录统计',
      '系统有什么新功能',
      '帮我分析数据'
    ];
    this.setData({
      quickQuestions: this.data.mode === 'stock' ? stockQuestions : systemQuestions
    });
  },

  onInput(e) {
    this.setData({ inputValue: e.detail.value });
  },

  onQuickTap(e) {
    const question = e.currentTarget.dataset.question;
    this.setData({ inputValue: question });
    this.onSend();
  },

  onImageSizeChange(e) {
    const index = Number(e.detail.value);
    const option = this.data.imageSizeOptions[index];
    if (!option) return;
    this.setData({ imageSize: option.value });
  },

  isImageAdjustMessage(message) {
    if (!message) return false;
    const adjustWords = [
      '调整', '修改', '改成', '换成', '优化', '重画', '重新生成',
      '基于上一张', '基于上图', '上一张', '上图', '这张图'
    ];
    return adjustWords.some(word => message.indexOf(word) > -1);
  },

  isImageGenerateMessage(message) {
    if (!message) return false;
    const hasImageWord = ['图片', '图像', '插画', '海报', '头像', '壁纸', '封面', '配图']
      .some(word => message.indexOf(word) > -1);
    const hasGenerateIntent = ['生成', '画', '绘制', '出图', '作图', '文生图', '做一张', '制作']
      .some(word => message.indexOf(word) > -1);
    return (hasImageWord && hasGenerateIntent)
      || message.indexOf('画一张') === 0
      || message.indexOf('生成一张') === 0
      || message.indexOf('出图') === 0;
  },

  extractLineValue(content, label) {
    const regexp = new RegExp(`^-\\s*${label}[:：]\\s*(.+)$`, 'm');
    const match = content.match(regexp);
    return match ? match[1].trim() : '';
  },

  extractImageContext(content) {
    if (!content) return null;
    const markdownImageMatch = content.match(/!\[[^\]]*]\(([^)]+)\)/);
    const openImageMatch = content.match(/\[打开原图]\(([^)]+)\)/);
    const imageUrl = (markdownImageMatch && markdownImageMatch[1])
      || (openImageMatch && openImageMatch[1])
      || '';
    if (!imageUrl) return null;
    return {
      referenceImageUrl: imageUrl.trim(),
      referenceImagePrompt: this.extractLineValue(content, '提示词'),
      referenceImageRevisedPrompt: this.extractLineValue(content, '优化提示词'),
      localImageUrl: '',
      displayImageUrl: imageUrl.trim(),
      imageLoadFailed: false
    };
  },

  buildDisplayContent(content) {
    if (!content) return '';
    return content
      .replace(/!\[[^\]]*]\([^)]+\)/g, '')
      .replace(/\n*\[打开原图]\([^)]+\)/g, '')
      .trim();
  },

  findLastImageContext() {
    for (let i = this.data.messages.length - 1; i >= 0; i--) {
      const msg = this.data.messages[i];
      if (msg.role !== 'assistant') continue;
      const imageContext = msg.imageContext || this.extractImageContext(msg.content);
      if (imageContext) {
        return imageContext;
      }
    }
    return null;
  },

  buildChatPayload(message) {
    const payload = {
      message: message,
      enableSkills: this.data.mode === 'stock'
    };
    if (this.isImageGenerateMessage(message) || this.isImageAdjustMessage(message)) {
      payload.imageSize = this.data.imageSize;
    }
    if (!this.isImageAdjustMessage(message)) {
      return payload;
    }
    const imageContext = this.findLastImageContext();
    if (!imageContext) {
      return payload;
    }
    return {
      ...payload,
      referenceImageUrl: imageContext.referenceImageUrl,
      referenceImagePrompt: imageContext.referenceImagePrompt,
      referenceImageRevisedPrompt: imageContext.referenceImageRevisedPrompt
    };
  },

  updateImageContext(messageId, patch) {
    const messages = this.data.messages.map(item => {
      if (item.id !== messageId || !item.imageContext) {
        return item;
      }
      return {
        ...item,
        imageContext: {
          ...item.imageContext,
          ...patch
        }
      };
    });
    this.setData({ messages });
  },

  downloadAiImage(messageId, imageUrl) {
    if (!imageUrl) return;
    if (imageUrl.indexOf('data:image/') === 0) {
      this.saveBase64Image(messageId, imageUrl);
      return;
    }
    const token = wx.getStorageSync('token');
    wx.downloadFile({
      url: `${app.globalData.baseUrl}/ai/image-proxy?url=${encodeURIComponent(imageUrl)}`,
      header: {
        Authorization: token ? `Bearer ${token}` : ''
      },
      success: (res) => {
        if (res.statusCode !== 200 || !res.tempFilePath) {
          console.warn('[AI Image] proxy download failed:', res);
          this.updateImageContext(messageId, {
            displayImageUrl: imageUrl,
            imageLoadFailed: false
          });
          return;
        }
        this.updateImageContext(messageId, {
          localImageUrl: res.tempFilePath,
          displayImageUrl: res.tempFilePath,
          imageLoadFailed: false
        });
      },
      fail: () => {
        console.warn('[AI Image] proxy download failed');
        this.updateImageContext(messageId, {
          displayImageUrl: imageUrl,
          imageLoadFailed: false
        });
      }
    });
  },

  saveBase64Image(messageId, dataUrl) {
    const commaIndex = dataUrl.indexOf(',');
    if (commaIndex < 0) return;
    const header = dataUrl.slice(0, commaIndex);
    const imageExt = header.indexOf('jpeg') > -1 || header.indexOf('jpg') > -1 ? 'jpg' : 'png';
    const filePath = `${wx.env.USER_DATA_PATH}/ai-image-${messageId}.${imageExt}`;
    wx.getFileSystemManager().writeFile({
      filePath,
      data: dataUrl.slice(commaIndex + 1),
      encoding: 'base64',
      success: () => {
        this.updateImageContext(messageId, {
          localImageUrl: filePath,
          displayImageUrl: filePath,
          imageLoadFailed: false
        });
      },
      fail: () => {
        wx.showToast({ title: '图片保存失败', icon: 'none' });
      }
    });
  },

  onAiImageLoad(e) {
    const messageId = Number(e.currentTarget.dataset.id);
    if (!messageId) return;
    this.updateImageContext(messageId, { imageLoadFailed: false });
  },

  onAiImageLoadError(e) {
    const messageId = Number(e.currentTarget.dataset.id);
    if (!messageId) return;
    const currentUrl = e.currentTarget.dataset.currentUrl;
    const originalUrl = e.currentTarget.dataset.originalUrl;
    if (originalUrl && currentUrl !== originalUrl) {
      this.updateImageContext(messageId, {
        displayImageUrl: originalUrl,
        imageLoadFailed: false
      });
      return;
    }
    this.updateImageContext(messageId, { imageLoadFailed: true });
  },

  previewAiImage(e) {
    const localUrl = e.currentTarget.dataset.localUrl;
    const originalUrl = e.currentTarget.dataset.originalUrl;
    const current = localUrl || originalUrl;
    if (!current) return;
    wx.previewImage({
      current,
      urls: [current]
    });
  },

  onSend() {
    const msg = this.data.inputValue.trim();
    if (!msg || this.data.loading) return;

    const userMsgId = this.data.msgIdCounter + 1;
    const userMsg = { id: userMsgId, role: 'user', content: msg };

    this.setData({
      messages: [...this.data.messages, userMsg],
      inputValue: '',
      loading: true,
      msgIdCounter: userMsgId,
      scrollToId: 'msg-loading'
    });

    aiApi.chat(this.buildChatPayload(msg)).then(res => {
      const answer = (res.data && res.data.answer) ? res.data.answer : '暂无回复';
      const aiMsgId = this.data.msgIdCounter + 1;
      const aiMsg = {
        id: aiMsgId,
        role: 'assistant',
        content: answer,
        displayContent: this.buildDisplayContent(answer),
        imageContext: this.extractImageContext(answer)
      };

      this.setData({
        messages: [...this.data.messages, aiMsg],
        msgIdCounter: aiMsgId,
        scrollToId: `msg-${aiMsgId}`
      });
      if (aiMsg.imageContext && aiMsg.imageContext.referenceImageUrl) {
        this.downloadAiImage(aiMsgId, aiMsg.imageContext.referenceImageUrl);
      }
    }).catch(err => {
      const errMsg = err.message || 'AI 服务暂时不可用';
      const aiMsgId = this.data.msgIdCounter + 1;
      const aiMsg = { id: aiMsgId, role: 'assistant', content: `⚠️ ${errMsg}` };

      this.setData({
        messages: [...this.data.messages, aiMsg],
        msgIdCounter: aiMsgId,
        scrollToId: `msg-${aiMsgId}`
      });
    }).finally(() => {
      this.setData({ loading: false });
    });
  }
});
