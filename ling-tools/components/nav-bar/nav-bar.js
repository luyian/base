const app = getApp();

Component({
  properties: {
    title: { type: String, value: '' },
    back: { type: Boolean, value: false }
  },

  data: {
    statusBarHeight: 20,
    navBarHeight: 44,
    isDark: false
  },

  lifetimes: {
    attached() {
      const windowInfo = wx.getWindowInfo();
      this.setData({ statusBarHeight: windowInfo.statusBarHeight || 20 });
      this._syncTheme();
      if (!app._navBars) app._navBars = [];
      app._navBars.push(this);
    },
    detached() {
      if (app._navBars) {
        const idx = app._navBars.indexOf(this);
        if (idx > -1) app._navBars.splice(idx, 1);
      }
    }
  },

  pageLifetimes: {
    show() {
      this._syncTheme();
    }
  },

  methods: {
    _syncTheme() {
      const theme = app.getTheme();
      this.setData({ isDark: theme === 'dark' });
    },

    onBack() {
      const pages = getCurrentPages();
      if (pages.length > 1) {
        wx.navigateBack();
      }
    }
  }
});
