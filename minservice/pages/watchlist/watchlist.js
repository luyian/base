// pages/watchlist/watchlist.js
const watchlistApi = require('../../api/watchlist');

Page({
  data: {
    watchlist: [],
    loading: true
  },

  onLoad() {
    this.loadWatchlist();
  },

  onShow() {
    this.loadWatchlist();
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