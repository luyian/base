// api/auth.js - Authentication API
const request = require('../utils/request');

module.exports = {
  // Account password login
  login: (data) => request.post('/auth/login', data),
  
  // Register
  register: (data) => request.post('/auth/register', data),
  
  // WeChat mini program login
  wxLogin: (code) => request.post('/auth/wx-login', { code }),
  
  // WeChat bind (bind existing account or create new)
  bindWechat: (data) => request.post('/auth/wx-bind', data),
  
  // Get user info
  getUserInfo: () => request.get('/auth/info'),
  
  // Logout
  logout: () => request.post('/auth/logout')
};