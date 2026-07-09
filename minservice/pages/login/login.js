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
    loginReady: false,
    autoLogin: false,
    autoLoginTried: false,
    wxCode: '',
    bindUsername: '',
    bindPassword: '',
    themeClass: ''
  },

  onLoad() {
    this.applyTheme();
    
    const token = wx.getStorageSync('token');
    if (token) {
      wx.switchTab({ url: '/pages/index/index' });
      return;
    }
    this.tryAutoWxLogin();
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

  tryAutoWxLogin() {
    if (wx.getStorageSync('manualLogout') || this.data.autoLoginTried) {
      this.showLoginPage();
      return;
    }
    this.setData({
      activeTab: 'password',
      loginReady: false,
      autoLogin: true,
      autoLoginTried: true
    });
    this.loginByWechat({
      auto: true,
      failMessage: '微信自动登录失败，请使用账号密码登录或微信一键登录'
    });
  },

  showLoginPage() {
    this.setData({
      activeTab: 'password',
      loginReady: true,
      autoLogin: false
    });
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
        app.globalData.token = token;
        app.clearManualLogout();
        return authApi.getUserInfo();
      })
      .then(userRes => {
        wx.setStorageSync('userInfo', userRes.data);
        app.globalData.userInfo = userRes.data;
        
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
                app.clearManualLogout();
                wx.switchTab({ url: '/pages/index/index' });
              }
            }
          });
        } else {
          wx.showToast({ title: '登录成功', icon: 'success' });
          app.clearManualLogout();
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
              app.globalData.userInfo = res.data;
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
    this.loginByWechat({
      auto: false,
      failMessage: '微信登录失败'
    });
  },

  loginByWechat(options = {}) {
    const that = this;
    const isAuto = !!options.auto;
    const failMessage = options.failMessage || '微信登录失败';
    wx.login({
      success(res) {
        that.setData({ loading: !isAuto, autoLogin: isAuto });
        authApi.wxLogin(res.code, { skipAuthRedirect: true, silentError: true })
          .then(res => {
            if (res.data && res.data.needBind) {
              that.handleWxLoginNeedBind(isAuto);
              return null;
            } else {
              const token = res.data.token;
              wx.setStorageSync('token', token);
              app.globalData.token = token;
              app.clearManualLogout();
              return authApi.getUserInfo();
            }
          })
          .then(userRes => {
            if (userRes && userRes.data) {
              wx.setStorageSync('userInfo', userRes.data);
              app.globalData.userInfo = userRes.data;
              wx.showToast({ title: '登录成功', icon: 'success' });
              setTimeout(() => { wx.switchTab({ url: '/pages/index/index' }); }, 1000);
            }
          })
          .catch(err => {
            console.error('WeChat login error:', err);
            if (that.isNeedBindError(err)) {
              that.handleWxLoginNeedBind(isAuto);
              return;
            }
            if (isAuto) {
              that.showLoginPage();
            }
            wx.showToast({ title: failMessage, icon: 'none' });
          })
          .finally(() => {
            const nextData = { loading: false };
            if (!isAuto) {
              nextData.autoLogin = false;
            }
            that.setData(nextData);
          });
      },
      fail() {
        if (isAuto) {
          that.showLoginPage();
        } else {
          that.setData({ autoLogin: false, activeTab: 'password' });
        }
        that.setData({ loading: false });
        wx.showToast({ title: failMessage, icon: 'none' });
      }
    });
  },

  isNeedBindError(err) {
    return err && err.message === 'NEED_BIND';
  },

  handleWxLoginNeedBind(isAuto) {
    if (isAuto) {
      this.showLoginPage();
    } else {
      this.setData({ activeTab: 'password' });
    }
    wx.showToast({
      title: isAuto ? '微信未绑定，请使用账号密码登录' : '微信未绑定，请先账号登录并绑定微信',
      icon: 'none'
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
        app.globalData.token = token;
        app.clearManualLogout();
        wx.showToast({ title: '绑定成功', icon: 'success' });
        this.setData({ showBindModal: false });
        return authApi.getUserInfo();
      })
      .then(res => {
        if (res.data) {
          wx.setStorageSync('userInfo', res.data);
          app.globalData.userInfo = res.data;
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
