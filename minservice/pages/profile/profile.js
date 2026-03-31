// pages/profile/profile.js
const authApi = require('../../api/auth');
const app = getApp();

Page({
  data: {
    userInfo: null,
    showBindModal: false,
    binding: false,
    bindUsername: '',
    bindPassword: ''
  },

  onLoad() {
    const userInfo = wx.getStorageSync('userInfo');
    this.setData({ userInfo });
  },

  onShow() {
    const userInfo = wx.getStorageSync('userInfo');
    this.setData({ userInfo });
  },

  // Bind WeChat
  bindWechat() {
    const that = this;
    wx.login({
      success(res) {
        that.setData({ showBindModal: true, wxCode: res.code });
      },
      fail() {
        wx.showToast({ title: '获取微信授权失败', icon: 'none' });
      }
    });
  },

  onBindUsernameInput(e) {
    this.setData({ bindUsername: e.detail.value });
  },

  onBindPasswordInput(e) {
    this.setData({ bindPassword: e.detail.value });
  },

  confirmBind() {
    const { wxCode, bindUsername, bindPassword } = this.data;
    if (!bindUsername && !bindPassword) {
      wx.showToast({ title: '请输入用户名或密码', icon: 'none' });
      return;
    }

    this.setData({ binding: true });
    authApi.bindWechat({
      code: wxCode,
      username: bindUsername,
      password: bindPassword
    })
      .then(res => {
        wx.showToast({ title: '绑定成功', icon: 'success' });
        this.setData({ showBindModal: false });
        // Refresh user info
        return authApi.getUserInfo();
      })
      .then(res => {
        if (res.data) {
          wx.setStorageSync('userInfo', res.data);
          this.setData({ userInfo: res.data });
        }
      })
      .catch(err => {
        console.error('Bind error:', err);
        wx.showToast({ title: '绑定失败', icon: 'none' });
      })
      .finally(() => {
        this.setData({ binding: false });
      });
  },

  closeBindModal() {
    this.setData({ showBindModal: false });
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