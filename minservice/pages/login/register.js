// pages/login/register.js
const authApi = require('../../api/auth');
const app = getApp();

Page({
  data: {
    username: '',
    password: '',
    confirmPassword: '',
    nickname: '',
    phone: '',
    loading: false,
    themeClass: ''
  },

  onLoad() {
    const theme = app.getTheme();
    this.applyTheme();
  },

  // 应用主题
  applyTheme() {
    const theme = app.getTheme();
    this.setData({
      themeClass: theme === 'dark' ? 'dark-theme' : 'light-theme'
    });
  },

  onUsernameInput(e) { this.setData({ username: e.detail.value }); },
  onPasswordInput(e) { this.setData({ password: e.detail.value }); },
  onConfirmPasswordInput(e) { this.setData({ confirmPassword: e.detail.value }); },
  onNicknameInput(e) { this.setData({ nickname: e.detail.value }); },
  onPhoneInput(e) { this.setData({ phone: e.detail.value }); },

  handleRegister() {
    const { username, password, confirmPassword, nickname, phone } = this.data;
    
    if (!username || username.length < 3) {
      wx.showToast({ title: '用户名至少3位', icon: 'none' });
      return;
    }
    if (!password || password.length < 6) {
      wx.showToast({ title: '密码至少6位', icon: 'none' });
      return;
    }
    if (password !== confirmPassword) {
      wx.showToast({ title: '两次密码不一致', icon: 'none' });
      return;
    }

    this.setData({ loading: true });
    
    authApi.register({ username, password, nickname, phone })
      .then(res => {
        const token = res.data.token;
        wx.setStorageSync('token', token);
        wx.setStorageSync('userInfo', res.data.user);
        app.globalData.token = token;
        app.globalData.userInfo = res.data.user;
        wx.showToast({ title: '注册成功', icon: 'success' });
        setTimeout(() => wx.switchTab({ url: '/pages/index/index' }), 1500);
      })
      .catch(err => {
        wx.showToast({ title: err.message || '注册失败', icon: 'none' });
      })
      .finally(() => {
        this.setData({ loading: false });
      });
  },

  goToLogin() {
    wx.navigateBack();
  }
});