// pages/profile/profile.js
const authApi = require('../../api/auth');
const app = getApp();

Page({
  data: {
    userInfo: null
  },

  onLoad() {
    const userInfo = wx.getStorageSync('userInfo');
    this.setData({ userInfo });
  },

  onShow() {
    const userInfo = wx.getStorageSync('userInfo');
    this.setData({ userInfo });
  },

  logout() {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          authApi.logout().finally(() => {
            app.logout();
            wx.reLaunch({ url: '/pages/login/login' });
          });
        }
      }
    });
  }
});