// pages/watchlist/watchlist.js
const watchlistApi = require('../../api/watchlist');
const app = getApp();

Page({
    data: {
        watchlist: [],
        loading: true,
        refreshing: false,
        updateTime: '',
        themeClass: ''
    },

    onLoad() {
        this.applyTheme();
        this.loadWatchlist();
    },

    onShow() {
        this.applyTheme();
        this.loadWatchlist();
    },

    applyTheme() {
        const theme = app.getTheme();
        this.setData({
            themeClass: theme === 'dark' ? 'dark-theme' : 'light-theme'
        });
    },

    setTheme() {
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
                const list = res.data || [];
                this.setData({ watchlist: list, loading: false });
                if (list.length > 0) {
                    this.fetchQuotes(list);
                }
            })
            .catch(() => this.setData({ loading: false }));
    },

    fetchQuotes(list) {
        const items = list || this.data.watchlist;
        const codes = items.map(item => item.stockCode);
        if (codes.length === 0) return;

        watchlistApi.getQuotes(codes)
            .then(res => {
                const quotes = res.data || {};
                const watchlist = this.data.watchlist.map(item => {
                    const quote = quotes[item.stockCode];
                    if (quote && quote.success !== false) {
                        return {
                            ...item,
                            price: quote.price ? Number(quote.price).toFixed(2) : '--',
                            change: quote.change ? Number(quote.change) : 0,
                            changeStr: quote.change ? Number(quote.change).toFixed(2) : '--',
                            changePercent: quote.changePercent ? Number(quote.changePercent) : 0,
                            changePercentStr: quote.changePercent ? Number(quote.changePercent).toFixed(2) + '%' : '--'
                        };
                    }
                    return item;
                });

                const now = new Date();
                const h = now.getHours().toString().padStart(2, '0');
                const m = now.getMinutes().toString().padStart(2, '0');
                const s = now.getSeconds().toString().padStart(2, '0');

                this.setData({ watchlist, updateTime: h + ':' + m + ':' + s, refreshing: false });
            })
            .catch(() => {
                this.setData({ refreshing: false });
            });
    },

    onRefresh() {
        if (this.data.refreshing) return;
        this.setData({ refreshing: true });
        this.fetchQuotes();
    },

    onPullDownRefresh() {
        this.loadWatchlist();
        wx.stopPullDownRefresh();
    },

    removeStock(e) {
        const id = e.currentTarget.dataset.id;
        wx.showModal({
            title: '确认',
            content: '确定删除该自选股？',
            success: (res) => {
                if (res.confirm) {
                    watchlistApi.removeFromWatchlist(id)
                        .then(() => {
                            const watchlist = this.data.watchlist.filter(item => item.id !== id);
                            this.setData({ watchlist });
                            wx.showToast({ title: '已删除', icon: 'success' });
                        });
                }
            }
        });
    }
});
