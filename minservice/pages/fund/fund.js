// pages/fund/fund.js
const fundApi = require('../../api/fund');

Page({
  data: {
    funds: [],
    loading: true,
    activeTab: 'all'
  },

  onLoad() {
    this.loadFunds();
  },

  onShow() {
    if (getApp().isLoggedIn()) {
      this.loadFunds();
    }
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

  // Toggle watchlist
  toggleWatchlist(e) {
    const fund = e.currentTarget.dataset.fund;
    const that = this;
    const fundId = fund.id || fund.fundId; // 支持 id 或 fundId
    
    if (!fundId) {
      wx.showToast({ title: '基金ID获取失败', icon: 'none' });
      return;
    }
    
    if (fund.inWatchlist) {
      // Remove from watchlist
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
      // Add to watchlist
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