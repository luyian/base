// pages/watchlist/watchlist.js
const watchlistApi = require('../../api/watchlist');
const app = getApp();

Page({
  data: {
    watchlist: [],
    loading: true,
    themeClass: ''
  },

  onLoad() {
    // 同步导航栏颜色
    const theme = app.getTheme();
    const navBgColor = theme === 'dark' ? '#0F172A' : '#FFFFFF';
    const navTextStyle = theme === 'dark' ? 'white' : 'black';
    wx.setNavigationBarColor({ frontColor: navTextStyle, backgroundColor: navBgColor });
    this.applyTheme();
    this.loadWatchlist();
  },

  onShow() {
    this.applyTheme();
    this.loadWatchlist();
  },

  // 应用主题
  applyTheme() {
    const theme = app.getTheme();
    this.setData({
      themeClass: theme === 'dark' ? 'dark-theme' : 'light-theme'
    });
  },

  // 页面样式设置（供 app.js 调用）
  setTheme(theme) {
    this.applyTheme();
  },

  loadWatchlist() {
    const token = wx.getStorageSync('token');
    if (!token) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      wx.navigateTo({ url: '/pages/login/login' });
      return;
    }
    
    this.setData({ loading: true });
    watchlistApi.getWatchlist()
      .then(res => {
        this.setData({ watchlist: res.data || [], loading: false });
      })
      .catch(() => this.setData({ loading: false }));
  },

  removeStock(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '确认',
      content: '确定删除该自选股？',
      success: (res) => {
        if (res.confirm) {
          watchlistApi.removeFromWatchlist(id)
            .then(() => this.loadWatchlist());
        }
      }
    });
  }
});