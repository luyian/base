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
              wx.showToast({
                title: '登录成功',
                icon: 'success'
              });
              setTimeout(() => {
                wx.switchTab({
                  url: '/pages/index/index'
                });
              }, 1500);
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