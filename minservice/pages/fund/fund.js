// pages/fund/fund.js
const fundApi = require('../../api/fund');
const app = getApp();

Page({
  data: {
    funds: [],
    loading: true,
    activeTab: 'all',
    isAdmin: false,
    themeClass: ''
  },

  onLoad() {
    // 同步导航栏颜色
    const theme = app.getTheme();
    const navBgColor = theme === 'dark' ? '#0F172A' : '#FFFFFF';
    const navTextStyle = theme === 'dark' ? 'white' : 'black';
    wx.setNavigationBarColor({ frontColor: navTextStyle, backgroundColor: navBgColor });
    this.applyTheme();
    this.setData({ isAdmin: app.isAdmin() });
    console.log('isAdmin:', app.isAdmin());
  },

  onShow() {
    this.applyTheme();
    if (app.isLoggedIn()) {
      this.setData({ isAdmin: app.isAdmin() });
      this.loadFunds();
    }
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

  loadFunds() {
    this.setData({ loading: true });
    fundApi.getFundList()
      .then(res => {
        const funds = (res.data || []).map(fund => ({
          ...fund,
          estimatedChangePercent: parseFloat(fund.estimatedChangePercent || 0).toFixed(2)
        }));
        this.setData({ funds: funds, loading: false });
      })
      .catch(() => this.setData({ loading: false }));
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/fund/detail?id=${id}` });
  },

  goToStock() {
    wx.navigateTo({ url: '/pages/stock/stock' });
  },

  goToCreate() {
    wx.navigateTo({ url: '/pages/fund/edit' });
  },

  goToEdit(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/fund/edit?id=${id}` });
  },

  // Delete fund
  deleteFund(e) {
    const fund = e.currentTarget.dataset.fund;
    const that = this;
    const fundId = fund.fundId;
    
    wx.showModal({
      title: '确认删除',
      content: `确定要删除「${fund.fundName}」吗？此操作不可恢复`,
      success(res) {
        if (res.confirm) {
          fundApi.deleteFund(fundId)
            .then(() => {
              wx.showToast({ title: '删除成功', icon: 'success' });
              that.loadFunds();
            })
            .catch(err => {
              wx.showToast({ title: '删除失败', icon: 'none' });
            });
        }
      }
    });
  },

  // Toggle watchlist
  toggleWatchlist(e) {
    const fund = e.currentTarget.dataset.fund;
    const that = this;
    const fundId = fund.fundId;
    
    if (!fundId) {
      wx.showToast({ title: '基金ID获取失败', icon: 'none' });
      return;
    }
    
    if (fund.inWatchlist) {
      wx.showModal({
        title: '提示',
        content: `确定取消关注「${fund.fundName}」？`,
        success(res) {
          if (res.confirm) {
            fundApi.removeFromWatchlist(fundId)
              .then(() => {
                wx.showToast({ title: '已取消关注', icon: 'success' });
                that.loadFunds();
              })
              .catch(err => {
                wx.showToast({ title: '操作失败', icon: 'none' });
              });
          }
        }
      });
    } else {
      fundApi.addToWatchlist(fundId)
        .then(() => {
          wx.showToast({ title: '已添加到自选', icon: 'success' });
          that.loadFunds();
        })
        .catch(err => {
          wx.showToast({ title: '操作失败', icon: 'none' });
        });
    }
  }
});
