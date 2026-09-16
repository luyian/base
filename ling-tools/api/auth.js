// api/auth.js - Authentication API
const request = require('../utils/request');

module.exports = {
  // Get captcha
  getCaptcha: () => request.get('/auth/captcha'),
  
  // Account password login
  login: (data) => request.post('/auth/login', data),
  
  // Register
  register: (data) => request.post('/auth/register', data),
  
  // WeChat mini program login
  // 携带 ling-tools 的 appId，后端据此路由到本小程序的 appSecret
  wxLogin: (code, options = {}) => request.post('/auth/wx-login', { code, appId: 'wx9d17e9aff6f97cbb' }, options),
  
  // WeChat bind (bind existing account or create new)
  bindWechat: (data) => request.post('/auth/wx-bind', { ...data, appId: 'wx9d17e9aff6f97cbb' }),

  // Bind WeChat for currently logged in user
  bindCurrentUserWechat: (code) => request.post('/auth/bind-wx', { code, appId: 'wx9d17e9aff6f97cbb' }),
  
  // Unbind WeChat for currently logged in user
  unbindCurrentUserWechat: () => request.delete('/auth/unbind-wx'),
  
  // Get user info
  // 携带 appId：绑定状态按「当前小程序」维度返回（历史绑定记录 app_id 为空不会误匹配）
  getUserInfo: () => request.get('/auth/info', { appId: 'wx9d17e9aff6f97cbb' }),
  
  // Logout
  logout: () => request.post('/auth/logout')
};
