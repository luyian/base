// 全局劫持 Page，为每个页面自动注入主题同步
const _originalPage = Page;
const _windowInfo = wx.getWindowInfo();
const _navBarTotalHeight = (_windowInfo.statusBarHeight || 20) + 44;

Page = function(pageConfig) {
  const originalOnShow = pageConfig.onShow;
  const originalOnLoad = pageConfig.onLoad;

  pageConfig.onLoad = function(...args) {
    this.setData({ navBarTotalHeight: _navBarTotalHeight });
    if (originalOnLoad) {
      originalOnLoad.apply(this, args);
    }
  };

  pageConfig.onShow = function(...args) {
    const app = getApp();
    if (app) {
      app._syncThemeStyle();
    }
    if (!pageConfig.applyTheme) {
      const theme = app ? app.getTheme() : 'light';
      this.setData({ themeClass: theme === 'dark' ? 'dark-theme' : 'light-theme' });
    }
    if (originalOnShow) {
      originalOnShow.apply(this, args);
    }
  };

  if (!pageConfig.setTheme) {
    pageConfig.setTheme = function() {
      const app = getApp();
      if (app) {
        app._syncThemeStyle();
      }
      const theme = app ? app.getTheme() : 'light';
      this.setData({ themeClass: theme === 'dark' ? 'dark-theme' : 'light-theme' });
    };
  }

  _originalPage(pageConfig);
};

App({
  globalData: {
    userInfo: null,
    token: null,
    baseUrl: 'https://xuecong.xyz/api',
    theme: 'light'
  },

  onLaunch() {
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    if (token) {
      this.globalData.token = token;
      this.globalData.userInfo = userInfo;
    }
    
    const savedTheme = wx.getStorageSync('theme');
    if (savedTheme) {
      this.globalData.theme = savedTheme;
    } else {
      wx.setStorageSync('theme', 'light');
      this.globalData.theme = 'light';
    }
    
    setTimeout(() => {
      this._syncThemeStyle();
    }, 100);
  },

  _syncThemeStyle() {
    try {
      const theme = this.globalData.theme || 'light';

      // TabBar 样式
      const tabBgColor = theme === 'dark' ? '#1E293B' : '#FFFFFF';
      const tabTextColor = theme === 'dark' ? '#94A3B8' : '#94A3B8';
      const tabSelectedColor = theme === 'dark' ? '#60A5FA' : '#3B82F6';
      wx.setTabBarStyle({
        color: tabTextColor,
        selectedColor: tabSelectedColor,
        backgroundColor: tabBgColor,
        borderStyle: theme === 'dark' ? 'white' : 'black'
      });

      // 页面下拉/橡皮筋区域背景色
      const bgColor = theme === 'dark' ? '#0F172A' : '#F5F7FA';
      wx.setBackgroundColor({
        backgroundColor: bgColor,
        backgroundColorTop: bgColor,
        backgroundColorBottom: bgColor
      });
    } catch (e) {
      console.error('[Theme] 同步失败:', e);
    }
  },

  onShow() {
    this._syncThemeStyle();
  },

  setTheme(theme) {
    this.globalData.theme = theme;
    wx.setStorageSync('theme', theme);
    this._syncThemeStyle();
    
    const pages = getCurrentPages();
    pages.forEach(page => {
      if (page && page.setTheme) {
        page.setTheme(theme);
      }
    });
    (this._navBars || []).forEach(nb => nb._syncTheme());
  },

  toggleTheme() {
    const newTheme = this.globalData.theme === 'dark' ? 'light' : 'dark';
    this.setTheme(newTheme);
    return newTheme;
  },

  getTheme() {
    return this.globalData.theme || 'light';
  },

  isLoggedIn() {
    return !!this.globalData.token;
  },

  isAdmin() {
    const roles = this.globalData.userInfo && this.globalData.userInfo.roles;
    return Array.isArray(roles) && (roles.includes('SUPER_ADMIN') || roles.includes('ADMIN'));
  },

  logout() {
    this.globalData.token = null;
    this.globalData.userInfo = null;
    wx.removeStorageSync('token');
    wx.removeStorageSync('userInfo');
  },

  login(code) {
    return new Promise((resolve, reject) => {
      wx.request({
        url: `${this.globalData.baseUrl}/auth/login`,
        method: 'POST',
        data: { code },
        header: { 'Content-Type': 'application/json' },
        success: (res) => {
          if (res.data.code === 200) {
            const token = res.data.data.token;
            const userInfo = res.data.data.user;
            this.globalData.token = token;
            this.globalData.userInfo = userInfo;
            wx.setStorageSync('token', token);
            wx.setStorageSync('userInfo', userInfo);
            resolve(userInfo);
          } else {
            reject(new Error(res.data.message));
          }
        },
        fail: reject
      });
    });
  }
});
