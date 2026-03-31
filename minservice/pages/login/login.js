// pages/login/login.js
const authApi = require('../../api/auth');

Page({
  data: {
    activeTab: 'password',
    loading: false,
    username: '',
    password: '',
    showBindModal: false,
    binding: false,
    wxCode: '',
    bindUsername: '',
    bindPassword: ''
  },

  onLoad() {
    const token = wx.getStorageSync('token');
    if (token) {
      wx.switchTab({ url: '/pages/index/index' });
    }
  },

  switchTab(e) {
    this.setData({ activeTab: e.currentTarget.dataset.tab });
  },

  onUsernameInput(e) {
    this.setData({ username: e.detail.value });
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value });
  },

  onBindUsernameInput(e) {
    this.setData({ bindUsername: e.detail.value });
  },

  onBindPasswordInput(e) {
    this.setData({ bindPassword: e.detail.value });
  },

  // 账号密码登录
  handlePasswordLogin() {
    const { username, password } = this.data;
    if (!username) {
      wx.showToast({ title: '请输入用户名', icon: 'none' });
      return;
    }
    if (!password) {
      wx.showToast({ title: '请输入密码', icon: 'none' });
      return;
    }

    this.setData({ loading: true });
    authApi.login({ username, password })
      .then(res => {
        const token = res.data.token;
        wx.setStorageSync('token', token);
        wx.setStorageSync('userInfo', res.data.user);
        wx.showToast({ title: '登录成功', icon: 'success' });
        wx.switchTab({ url: '/pages/index/index' });
      })
      .catch(err => {
        wx.showToast({ title: err.message || '登录失败', icon: 'none' });
      })
      .finally(() => {
        this.setData({ loading: false });
      });
  },

  // 微信登录
  handleWxLogin() {
    if (this.data.loading) return;
    
    this.setData({ loading: true });
    
    wx.login({
      success: (res) => {
        if (res.code) {
          this.setData({ wxCode: res.code });
          authApi.wxLogin(res.code)
            .then(data => {
              const token = data.data.token;
              wx.setStorageSync('token', token);
              wx.setStorageSync('userInfo', data.data.user);
              wx.showToast({ title: '登录成功', icon: 'success' });
              wx.switchTab({ url: '/pages/index/index' });
            })
            .catch(err => {
              if (err.code === 401 && err.message === 'NEED_BIND') {
                this.setData({ showBindModal: true });
              } else {
                wx.showToast({ title: err.message || '登录失败', icon: 'none' });
              }
            })
            .finally(() => {
              this.setData({ loading: false });
            });
        } else {
          wx.showToast({ title: '获取code失败', icon: 'none' });
          this.setData({ loading: false });
        }
      },
      fail: () => {
        wx.showToast({ title: '登录失败', icon: 'none' });
        this.setData({ loading: false });
      }
    });
  },

  closeBindModal() {
    this.setData({ showBindModal: false });
  },

  confirmBind() {
    const { wxCode, bindUsername, bindPassword } = this.data;
    
    this.setData({ binding: true });
    
    authApi.bindWechat({
      code: wxCode,
      username: bindUsername,
      password: bindPassword
    })
      .then(res => {
        const token = res.data.token;
        wx.setStorageSync('token', token);
        wx.setStorageSync('userInfo', res.data.user);
        wx.showToast({ title: '绑定成功', icon: 'success' });
        this.setData({ showBindModal: false });
        wx.switchTab({ url: '/pages/index/index' });
      })
      .catch(err => {
        wx.showToast({ title: err.message || '绑定失败', icon: 'none' });
      })
      .finally(() => {
        this.setData({ binding: false });
      });
  },

  goToRegister() {
    wx.navigateTo({ url: '/pages/login/register' });
  }
});