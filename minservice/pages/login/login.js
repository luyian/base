// pages/login/login.js
const authApi = require('../../api/auth');

Page({
  data: {
    loading: false
  },

  onLoad() {
    // Check if already logged in
    const token = wx.getStorageSync('token');
    if (token) {
      wx.switchTab({
        url: '/pages/index/index'
      });
    }
  },

  // WeChat login
  handleWxLogin() {
    if (this.data.loading) return;
    
    this.setData({ loading: true });
    
    wx.login({
      success: (res) => {
        if (res.code) {
          authApi.wxLogin(res.code)
            .then(data => {
              console.log('登录响应:', data);
              // Save token
              const app = getApp();
              if (data.data && data.data.token) {
                const token = data.data.token;
                wx.setStorageSync('token', token);
                app.globalData.token = token;  // Update globalData
                console.log('Token已保存:', token);
              }
              wx.showToast({
                title: '登录成功',
                icon: 'success'
              });
              wx.switchTab({
                url: '/pages/index/index'
              });
            })
            .catch(err => {
              wx.showToast({
                title: err.message || '登录失败',
                icon: 'none'
              });
            })
            .finally(() => {
              this.setData({ loading: false });
            });
        } else {
          wx.showToast({
            title: '获取code失败',
            icon: 'none'
          });
          this.setData({ loading: false });
        }
      },
      fail: () => {
        wx.showToast({
          title: '登录失败',
          icon: 'none'
        });
        this.setData({ loading: false });
      }
    });
  }
});