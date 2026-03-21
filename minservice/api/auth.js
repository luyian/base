// api/auth.js - Authentication API
const request = require('../utils/request');

module.exports = {
  // WeChat mini program login
  wxLogin: (code) => request.post('/auth/wx-login', { code }),
  
  // Get user info
  getUserInfo: () => request.get('/auth/info'),
  
  // Logout
  logout: () => request.post('/auth/logout')
};