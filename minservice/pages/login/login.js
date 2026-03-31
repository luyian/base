// pages/login/login.js
const authApi = require('../../api/auth');

Page({
  data: {
    activeTab: 'password',
    loading: false,
    username: '',
    password: '',
    captchaEnabled: false,
    captchaKey: '',
    captchaImage: '',
    captcha: '',
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
    // Check and load captcha
    this.loadCaptcha();
  },

  // Load captcha
  loadCaptcha() {
    authApi.getCaptcha().then(res => {
      if (res.data && res.data.enabled) {
        this.setData({
          captchaEnabled: true,
          captchaKey: res.data.captchaKey,
          captchaImage: res.data.captchaImage
        });
      } else {
        this.setData({ captchaEnabled: false });
      }
    }).catch(() => {
      this.setData({ captchaEnabled: false });
    });
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

  onCaptchaInput(e) {
    this.setData({ captcha: e.detail.value });
  },

  // Refresh captcha
  refreshCaptcha() {
    this.loadCaptcha();
  },

  onBindUsernameInput(e) {
    this.setData({ bindUsername: e.detail.value });
  },

  onBindPasswordInput(e) {
    this.setData({ bindPassword: e.detail.value });
  },

  // 账号密码登录
  handlePasswordLogin() {
    const { username, password, captchaEnabled, captchaKey, captcha } = this.data;
    if (!username) {
      wx.showToast({ title: '请输入用户名', icon: 'none' });
      return;
    }
    if (!password) {
      wx.showToast({ title: '请输入密码', icon: 'none' });
      return;
    }
    if (captchaEnabled && !captcha) {
      wx.showToast({ title: '请输入验证码', icon: 'none' });
      return;
    }

    this.setData({ loading: true });
    
    const loginData = { username, password };
    if (captchaEnabled && captchaKey && captcha) {
      loginData.captchaKey = captchaKey;
      loginData.captcha = captcha;
    }
    
    authApi.login(loginData)
      .then(res => {
        const token = res.data.token;
        wx.setStorageSync('token', token);
        wx.showToast({ title: '登录成功', icon: 'success' });
        setTimeout(() => {
          wx.switchTab({ url: '/pages/index/index' });
        }, 1000);
      })
      .catch(err => {
        console.error('Login error:', err);
        // If captcha error, refresh captcha
        if (this.data.captchaEnabled) {
          this.refreshCaptcha();
        }
      })
      .finally(() => {
        this.setData({ loading: false });
      });
  },

  // ... rest of the file
  handleWxLogin() {
    const that = this;
    wx.login({
      success(res) {
        that.setData({ loading: true });
        authApi.wxLogin(res.code)
          .then(res => {
            if (res.data && res.data.needBind) {
              that.setData({ 
                wxCode: res.code,
                showBindModal: true 
              });
            } else {
              const token = res.data.token;
              wx.setStorageSync('token', token);
              wx.showToast({ title: '登录成功', icon: 'success' });
              setTimeout(() => {
                wx.switchTab({ url: '/pages/index/index' });
              }, 1000);
            }
          })
          .catch(err => {
            console.error('WeChat login error:', err);
            wx.showToast({ title: '微信登录失败', icon: 'none' });
          })
          .finally(() => {
            that.setData({ loading: false });
          });
      },
      fail() {
        wx.showToast({ title: '微信登录失败', icon: 'none' });
      }
    });
  },

  closeBindModal() {
    this.setData({ showBindModal: false });
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
        const token = res.data.token;
        wx.setStorageSync('token', token);
        wx.showToast({ title: '绑定成功', icon: 'success' });
        this.setData({ showBindModal: false });
        setTimeout(() => {
          wx.switchTab({ url: '/pages/index/index' });
        }, 1000);
      })
      .catch(err => {
        console.error('Bind error:', err);
        wx.showToast({ title: '绑定失败', icon: 'none' });
      })
      .finally(() => {
        this.setData({ binding: false });
      });
  },

  goToRegister() {
    wx.navigateTo({ url: '/pages/login/register' });
  }
});
