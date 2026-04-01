// pages/login/login.js
const authApi = require('../../api/auth');
const app = getApp();

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
    bindPassword: '',
    themeClass: ''
  },

  onLoad() {
    // 同步导航栏颜色
    const theme = app.getTheme();
    const navBgColor = theme === 'dark' ? '#0F172A' : '#FFFFFF';
    const navTextStyle = theme === 'dark' ? 'white' : 'black';
    wx.setNavigationBarColor({ frontColor: navTextStyle, backgroundColor: navBgColor });
    this.applyTheme();
    
    const token = wx.getStorageSync('token');
    if (token) {
      wx.switchTab({ url: '/pages/index/index' });
    }
    // Check and load captcha
    this.loadCaptcha();
  },

  // 应用主题
  applyTheme() {
    const theme = app.getTheme();
    this.setData({
      themeClass: theme === 'dark' ? 'dark-theme' : 'light-theme'
    });
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
    
    if (!username) { wx.showToast({ title: '请输入用户名', icon: 'none' }); return; }
    if (!password) { wx.showToast({ title: '请输入密码', icon: 'none' }); return; }
    if (captchaEnabled && !captcha) { wx.showToast({ title: '请输入验证码', icon: 'none' }); return; }

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
        return authApi.getUserInfo();
      })
      .then(userRes => {
        wx.setStorageSync('userInfo', userRes.data);
        
        const userInfo = userRes.data;
        if (!userInfo.wxOpenid) {
          // Show bind WeChat popup - using current user binding
          wx.showModal({
            title: '绑定微信',
            content: '您还未绑定微信，是否立即绑定？绑定后可使用微信一键登录',
            confirmText: '绑定',
            cancelText: '跳过',
            success: (modalRes) => {
              if (modalRes.confirm) {
                this.bindWechat();
              } else {
                wx.switchTab({ url: '/pages/index/index' });
              }
            }
          });
        } else {
          wx.showToast({ title: '登录成功', icon: 'success' });
          setTimeout(() => { wx.switchTab({ url: '/pages/index/index' }); }, 1000);
        }
      })
      .catch(err => {
        console.error('Login error:', err);
        if (this.data.captchaEnabled) this.refreshCaptcha();
      })
      .finally(() => {
        this.setData({ loading: false });
      });
  },

  // Bind WeChat to current logged-in user (doesn't change existing account)
  bindWechat() {
    const that = this;
    wx.login({
      success(res) {
        that.setData({ binding: true });
        // 只绑定微信到当前登录用户，不修改任何账号信息
        authApi.bindCurrentUserWechat(res.code)
          .then(() => {
            wx.showToast({ title: '绑定成功', icon: 'success' });
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
            wx.showToast({ title: err.message || '绑定失败', icon: 'none' });
          })
          .finally(() => {
            that.setData({ binding: false });
            wx.switchTab({ url: '/pages/index/index' });
          });
      },
      fail() {
        wx.showToast({ title: '获取微信授权失败', icon: 'none' });
        wx.switchTab({ url: '/pages/index/index' });
      }
    });
  },

  // 微信登录
  handleWxLogin() {
    const that = this;
    wx.login({
      success(res) {
        that.setData({ loading: true });
        authApi.wxLogin(res.code)
          .then(res => {
            if (res.data && res.data.needBind) {
              that.setData({ wxCode: res.code, showBindModal: true });
            } else {
              const token = res.data.token;
              wx.setStorageSync('token', token);
              return authApi.getUserInfo();
            }
          })
          .then(userRes => {
            if (userRes && userRes.data) {
              wx.setStorageSync('userInfo', userRes.data);
              wx.showToast({ title: '登录成功', icon: 'success' });
              setTimeout(() => { wx.switchTab({ url: '/pages/index/index' }); }, 1000);
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

  // 确认绑定 - 使用当前用户绑定，不修改账号信息
  confirmBind() {
    const { wxCode } = this.data;
    this.setData({ binding: true });
    
    // 使用当前用户绑定微信，无需输入用户名密码
    authApi.bindCurrentUserWechat(wxCode)
      .then(res => {
        const token = res.data.token;
        wx.setStorageSync('token', token);
        wx.showToast({ title: '绑定成功', icon: 'success' });
        this.setData({ showBindModal: false });
        return authApi.getUserInfo();
      })
      .then(res => {
        if (res.data) {
          wx.setStorageSync('userInfo', res.data);
        }
      })
      .catch(err => {
        console.error('Bind error:', err);
        wx.showToast({ title: '绑定失败', icon: 'none' });
      })
      .finally(() => {
        this.setData({ binding: false });
        wx.switchTab({ url: '/pages/index/index' });
      });
  },

  goToRegister() {
    wx.navigateTo({ url: '/pages/login/register' });
  }
});