// api/ai.js - AI 助手 API
const request = require('../utils/request');

module.exports = {
  /**
   * AI 对话（支持技能调用）
   * @param {Object} data - { message: string, context?: string, enableSkills?: boolean }
   */
  chat: (data) => request.post('/ai/chat', data)
};
