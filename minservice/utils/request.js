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
          }
          resolve(res.data);
        } else if (res.data.code === 401) {
          // Token expired, redirect to login
          app.logout();
          wx.redirectTo({
            url: '/pages/login/login'
          });
          reject(res.data);
        } else {
          wx.showToast({
            title: res.data.message || '请求失败',
            icon: 'none'
          });
          reject(res.data);
        }
      },
      fail: (err) => {
        console.log('请求失败:', err);
        wx.showToast({
          title: '网络请求失败',
          icon: 'none'
        });
        reject(err);
      }
    });
  });
};

module.exports = {
  get: (url, data) => request({ url, data, method: 'GET' }),
  post: (url, data) => request({ url, data, method: 'POST' }),
  put: (url, data) => request({ url, data, method: 'PUT' }),
  delete: (url, data) => request({ url, data, method: 'DELETE' })
};