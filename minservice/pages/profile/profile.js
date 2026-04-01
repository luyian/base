// pages/profile/profile.js
const authApi = require('../../api/auth');
const app = getApp();

Page({
  data: {
    userInfo: null,
    showBindModal: false,
    binding: false,
    wxCode: '',
    isDarkTheme: false
  },

  onLoad() {
    this.loadUserInfo();
    this.checkTheme();
  },

  onShow() {
    this.loadUserInfo();
    this.checkTheme();
  },

  loadUserInfo() {
    const userInfo = wx.getStorageSync('userInfo');
    this.setData({ userInfo });
  },

  // 检查当前主题
  checkTheme() {
    const theme = app.getTheme();
    this.setData({ isDarkTheme: theme === 'dark' });
  },

  // 页面样式设置（供 app.js 调用）
  setTheme(theme) {
    this.setData({ isDarkTheme: theme === 'dark' });
  },

  // 切换主题
  toggleTheme() {
    const newTheme = app.toggleTheme();
    this.setData({ isDarkTheme: newTheme === 'dark' });
    wx.showToast({
      title: newTheme === 'dark' ? '已切换到深色模式' : '已切换到亮色模式',
      icon: 'success'
    });
  },

  // Bind WeChat
  bindWechat() {
    const that = this;
    wx.login({
      success(res) {
        that.setData({ binding: true });
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

  // Unbind WeChat
  unbindWechat() {
    const that = this;
    wx.showModal({
      title: '确认解绑',
      content: '确定要解绑微信吗？解绑后将无法使用微信一键登录',
      success: (res) => {
        if (res.confirm) {
          authApi.unbindCurrentUserWechat()
            .then(() => {
              wx.showToast({ title: '解绑成功', icon: 'success' });
              return authApi.getUserInfo();
            })
            .then(res => {
              if (res.data) {
                wx.setStorageSync('userInfo', res.data);
                that.setData({ userInfo: res.data });
              }
            })
            .catch(err => {
              console.error('Unbind error:', err);
              wx.showToast({ title: err.data?.message || '解绑失败', icon: 'none' });
            });
        }
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
