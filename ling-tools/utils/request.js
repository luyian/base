// utils/request.js - HTTP request utility
const app = getApp();

const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token');
    console.log('请求URL:', options.url, 'Token:', token ? token.substring(0, 20) + '...' : '无');
    
    wx.request({
      url: app.globalData.baseUrl + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.header
      },
      success: (res) => {
        console.log('响应:', res.statusCode, res.data);
        if (res.data.code === 200) {
          // Store token if present
          if (res.data.data && res.data.data.token) {
            wx.setStorageSync('token', res.data.data.token);
            app.globalData.token = res.data.data.token;  // Update globalData
            if (app.clearManualLogout) {
              app.clearManualLogout();
            }
          }
          resolve(res.data);
        } else if (!options.skipAuthRedirect && (res.data.code === 401 ||
            // 携带 token 但后端返回「用户不存在」：旧 token 对应用户已不存在，
            // 视为会话失效，自动清理并回登录页重新登录（否则会卡死在该提示）
            (token && res.data.code === 404 && res.data.message === '用户不存在'))) {
          // Token expired / user gone, redirect to login（清栈，最稳）
          app.logout();
          wx.reLaunch({
            url: '/pages/login/login'
          });
          reject(res.data);
        } else {
          if (!options.silentError) {
            wx.showToast({
              title: res.data.message || '请求失败',
              icon: 'none'
            });
          }
          reject(res.data);
        }
      },
      fail: (err) => {
        console.log('请求失败:', err);
        if (!options.silentError) {
          wx.showToast({
            title: '网络请求失败',
            icon: 'none'
          });
        }
        reject(err);
      }
    });
  });
};

module.exports = {
  get: (url, data) => request({ url, data, method: 'GET' }),
  post: (url, data, options = {}) => request({ url, data, method: 'POST', ...options }),
  put: (url, data) => request({ url, data, method: 'PUT' }),
  delete: (url, data) => request({ url, data, method: 'DELETE' })
};
