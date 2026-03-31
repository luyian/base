// pages/profile/profile.js
const authApi = require('../../api/auth');
const app = getApp();

Page({
  data: {
    userInfo: null,
    showBindModal: false,
    binding: false,
    wxCode: ''
  },

  onLoad() {
    this.loadUserInfo();
  },

  onShow() {
    this.loadUserInfo();
  },

  loadUserInfo() {
    const userInfo = wx.getStorageSync('userInfo');
    this.setData({ userInfo });
  },

  // Bind WeChat - now just gets code and binds directly
  bindWechat() {
    const that = this;
    wx.login({
      success(res) {
        that.setData({ binding: true });
        // Use the simple bind API - no need for username/password
        authApi.bindCurrentUserWechat(res.code)
          .then(() => {
            wx.showToast({ title: '绑定成功', icon: 'success' });
            // Refresh user info
            return authApi.getUserInfo();
          })
          .then(res => {
            if (res.data) {
              wx.setStorageSync('userInfo', res.data);
              that.setData({ userInfo: res.data });
            }
          })
          .catch(err => {
            console.error('Bind error:', err);
            wx.showToast({ title: err.data?.message || '绑定失败', icon: 'none' });
          })
          .finally(() => {
            that.setData({ binding: false });
          });
      },
      fail() {
        wx.showToast({ title: '获取微信授权失败', icon: 'none' });
      }
    });
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
