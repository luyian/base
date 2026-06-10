// components/md-render/md-render.js
const markdownIt = require('./markdown-it.min.js');

const md = markdownIt({
  html: false,
  breaks: true,
  linkify: true
});

Component({
  properties: {
    content: {
      type: String,
      value: '',
      observer(val) {
        this.renderMarkdown(val);
      }
    }
  },

  data: {
    nodes: ''
  },

  lifetimes: {
    attached() {
      if (this.data.content) {
        this.renderMarkdown(this.data.content);
      }
    }
  },

  methods: {
    renderMarkdown(content) {
      if (!content) {
        this.setData({ nodes: '' });
        return;
      }
      const html = md.render(content);
      this.setData({ nodes: html });
    }
  }
});
