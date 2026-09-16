// pages/profile/profile.js - 我的（用户信息/微信绑定/主题/退出登录）
const app = getApp();
const api = require('../../api/auth');

Page({
  data: {
    themeClass: '',
    userInfo: null,
    displayName: '未登录',
    displaySub: '',
    userInitial: '灵',
    wechatBound: false
  },

  onLoad() {
    this.applyTheme();
    this.loadUserInfo();
  },

  onShow() {
    this.applyTheme();
    this.loadUserInfo();
  },

  applyTheme() {
    const theme = app.getTheme();
    this.setData({ themeClass: theme === 'dark' ? 'dark-theme' : 'light-theme' });
  },

  setTheme() {
    this.applyTheme();
  },

  loadUserInfo() {
    const ui = wx.getStorageSync('userInfo') || app.globalData.userInfo;
    if (!ui) {
      this.setData({ userInfo: null, wechatBound: false });
      return;
    }
    const name = ui.nickname || ui.username || '用户';
    const sub = this._buildSub(ui);
    const initial = (name || '灵').charAt(0).toUpperCase();
    this.setData({ userInfo: ui, displayName: name, displaySub: sub, userInitial: initial, wechatBound: !!ui.wxOpenid });
  },

  // ==================== 微信绑定 ====================
  // 已绑定 → 解绑；未绑定 → 绑定
  onToggleWechat() {
    if (this.data.wechatBound) {
      this._unbindWechat();
    } else {
      this._bindWechat();
    }
  },
  _bindWechat() {
    const that = this;
    wx.login({
      success(res) {
        if (!res.code) {
          wx.showToast({ title: '微信授权失败', icon: 'none' });
          return;
        }
        api.bindCurrentUserWechat(res.code)
          .then(() => {
            wx.showToast({ title: '绑定成功', icon: 'success' });
            that._markWechatBound(true);
          })
          .catch(() => {});
      },
      fail() {
        wx.showToast({ title: '微信授权失败', icon: 'none' });
      }
    });
  },
  _unbindWechat() {
    const that = this;
    wx.showModal({
      title: '解绑微信',
      content: '解绑后将无法使用微信登录该账号，确定解绑？',
      confirmColor: '#C6402E',
      success(r) {
        if (!r.confirm) return;
        api.unbindCurrentUserWechat()
          .then(() => {
            wx.showToast({ title: '已解绑', icon: 'success' });
            that._markWechatBound(false);
          })
          .catch(() => {});
      }
    });
  },
  // 更新本地绑定状态（storage + globalData + 页面）
  _markWechatBound(bound) {
    const ui = this.data.userInfo || wx.getStorageSync('userInfo') || {};
    if (bound) {
      ui.wxOpenid = ui.wxOpenid || 'bound';
    } else {
      ui.wxOpenid = null;
    }
    wx.setStorageSync('userInfo', ui);
    app.globalData.userInfo = ui;
    this.setData({ userInfo: ui, wechatBound: bound });
  },

  // 构建副标题：优先手机号脱敏，其次邮箱，其次角色
  _buildSub(ui) {
    if (ui.phone) return this._mask(ui.phone);
    if (ui.email) return this._maskEmail(ui.email);
    if (ui.deptName) return ui.deptName;
    return ui.username || '';
  },

  _mask(phone) {
    const s = String(phone);
    if (s.length >= 11) {
      return s.slice(0, 3) + '****' + s.slice(-4);
    }
    return s;
  },

  _maskEmail(email) {
    const idx = email.indexOf('@');
    if (idx <= 0) return email;
    return email.charAt(0) + '***' + email.slice(idx);
  },

  onToggleTheme() {
    app.toggleTheme();
    this.applyTheme();
  },

  onLogout() {
    const that = this;
    wx.showModal({
      title: '退出登录',
      content: '确定要退出当前账号吗？',
      confirmColor: '#C6402E',
      success(r) {
        if (!r.confirm) return;
        app.logout({ manual: true });
        wx.reLaunch({ url: '/pages/login/login' });
      }
    });
  }
});