// pages/ai/ai.js - AI 助手页面
const aiApi = require('../../api/ai');

Page({
  data: {
    mode: 'stock',
    messages: [],
    inputValue: '',
    loading: false,
    scrollToId: '',
    quickQuestions: [],
    msgIdCounter: 0
  },

  onLoad() {
    this.updateQuickQuestions();
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

    aiApi.chat({
      message: msg,
      enableSkills: this.data.mode === 'stock'
    }).then(res => {
      const answer = (res.data && res.data.answer) ? res.data.answer : '暂无回复';
      const aiMsgId = this.data.msgIdCounter + 1;
      const aiMsg = { id: aiMsgId, role: 'assistant', content: answer };

      this.setData({
        messages: [...this.data.messages, aiMsg],
        msgIdCounter: aiMsgId,
        scrollToId: `msg-${aiMsgId}`
      });
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
