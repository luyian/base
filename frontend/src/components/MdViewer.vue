<template>
  <div class="md-viewer" ref="viewerRef">
    <div class="md-viewer__body" v-html="renderedHtml"></div>
    <!-- 图片预览 -->
    <el-image-viewer
      v-if="previewVisible"
      :url-list="[previewSrc]"
      @close="previewVisible = false"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted, onBeforeUnmount } from 'vue'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js/lib/core'
import taskLists from 'markdown-it-task-lists'
import { ElMessage } from 'element-plus'

// 按需注册常用语言（减少打包体积）
import javascript from 'highlight.js/lib/languages/javascript'
import typescript from 'highlight.js/lib/languages/typescript'
import java from 'highlight.js/lib/languages/java'
import python from 'highlight.js/lib/languages/python'
import sql from 'highlight.js/lib/languages/sql'
import xml from 'highlight.js/lib/languages/xml'
import css from 'highlight.js/lib/languages/css'
import json from 'highlight.js/lib/languages/json'
import bash from 'highlight.js/lib/languages/bash'
import yaml from 'highlight.js/lib/languages/yaml'
import markdown from 'highlight.js/lib/languages/markdown'
import go from 'highlight.js/lib/languages/go'
import csharp from 'highlight.js/lib/languages/csharp'
import dockerfile from 'highlight.js/lib/languages/dockerfile'
import nginx from 'highlight.js/lib/languages/nginx'
import plaintext from 'highlight.js/lib/languages/plaintext'

hljs.registerLanguage('javascript', javascript)
hljs.registerLanguage('js', javascript)
hljs.registerLanguage('typescript', typescript)
hljs.registerLanguage('ts', typescript)
hljs.registerLanguage('java', java)
hljs.registerLanguage('python', python)
hljs.registerLanguage('py', python)
hljs.registerLanguage('sql', sql)
hljs.registerLanguage('xml', xml)
hljs.registerLanguage('html', xml)
hljs.registerLanguage('css', css)
hljs.registerLanguage('json', json)
hljs.registerLanguage('bash', bash)
hljs.registerLanguage('shell', bash)
hljs.registerLanguage('sh', bash)
hljs.registerLanguage('yaml', yaml)
hljs.registerLanguage('yml', yaml)
hljs.registerLanguage('markdown', markdown)
hljs.registerLanguage('md', markdown)
hljs.registerLanguage('go', go)
hljs.registerLanguage('csharp', csharp)
hljs.registerLanguage('cs', csharp)
hljs.registerLanguage('dockerfile', dockerfile)
hljs.registerLanguage('nginx', nginx)
hljs.registerLanguage('plaintext', plaintext)
hljs.registerLanguage('text', plaintext)

const props = defineProps({
  /** Markdown 原始文本 */
  content: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['toc'])

const viewerRef = ref(null)
const previewVisible = ref(false)
const previewSrc = ref('')

// 初始化 markdown-it 实例
const md = new MarkdownIt({
  html: false,
  linkify: true,
  breaks: true,
  typographer: true,
  highlight(str, lang) {
    const language = lang && hljs.getLanguage(lang) ? lang : 'plaintext'
    const highlighted = hljs.highlight(str, { language }).value
    // 包裹代码块：语言角标 + 复制按钮
    const langLabel = lang || 'text'
    return `<div class="md-code-block">
      <div class="md-code-header">
        <span class="md-code-lang">${langLabel}</span>
        <button class="md-code-copy" data-code="${encodeURIComponent(str)}">复制</button>
      </div>
      <pre class="hljs"><code>${highlighted}</code></pre>
    </div>`
  }
})

// 启用任务列表
md.use(taskLists, { enabled: true })

// 为标题注入 id 锚点
let headingIndex = 0
md.renderer.rules.heading_open = function (tokens, idx, options, env, self) {
  const token = tokens[idx]
  token.attrSet('id', 'heading-' + headingIndex)
  headingIndex++
  return self.renderToken(tokens, idx, options)
}

// 外部链接自动加 target="_blank" 和图标
const defaultLinkOpen = md.renderer.rules.link_open ||
  function (tokens, idx, options, env, self) { return self.renderToken(tokens, idx, options) }
md.renderer.rules.link_open = function (tokens, idx, options, env, self) {
  const href = tokens[idx].attrGet('href') || ''
  if (href.startsWith('http://') || href.startsWith('https://')) {
    tokens[idx].attrSet('target', '_blank')
    tokens[idx].attrSet('rel', 'noopener noreferrer')
    tokens[idx].attrJoin('class', 'md-external-link')
  }
  return defaultLinkOpen(tokens, idx, options, env, self)
}

// 渲染 HTML
const renderedHtml = computed(() => {
  if (!props.content) {
    return '<p class="md-empty">暂无内容</p>'
  }
  headingIndex = 0
  return md.render(props.content)
})

// 解析目录列表并通知父组件
const parseToc = () => {
  if (!props.content) {
    emit('toc', [])
    return
  }
  const tokens = md.parse(props.content, {})
  const headings = []
  let idx = 0
  for (let i = 0; i < tokens.length; i++) {
    if (tokens[i].type === 'heading_open') {
      const level = parseInt(tokens[i].tag.slice(1))
      const text = tokens[i + 1]?.content || ''
      headings.push({ id: 'heading-' + idx, text, level })
      idx++
    }
  }
  emit('toc', headings)
}

watch(() => props.content, () => {
  parseToc()
  nextTick(() => bindEvents())
}, { immediate: true })

// 绑定代码复制和图片预览事件
const bindEvents = () => {
  if (!viewerRef.value) {
    return
  }
  // 代码复制按钮
  const copyBtns = viewerRef.value.querySelectorAll('.md-code-copy')
  copyBtns.forEach(btn => {
    btn.onclick = async () => {
      const code = decodeURIComponent(btn.getAttribute('data-code') || '')
      try {
        await navigator.clipboard.writeText(code)
        btn.textContent = '已复制'
        btn.classList.add('copied')
        setTimeout(() => {
          btn.textContent = '复制'
          btn.classList.remove('copied')
        }, 2000)
      } catch (error) {
        ElMessage.warning('复制失败，请手动复制')
      }
    }
  })
  // 图片点击预览
  const images = viewerRef.value.querySelectorAll('.md-viewer__body img')
  images.forEach(img => {
    img.style.cursor = 'zoom-in'
    img.onclick = () => {
      previewSrc.value = img.src
      previewVisible.value = true
    }
  })
}

onMounted(() => {
  nextTick(() => bindEvents())
})
</script>

<style>
@import 'highlight.js/styles/atom-one-dark.css';
</style>

<style scoped>
.md-viewer {
  width: 100%;
}

/* ==================== 全局 Markdown 排版 ==================== */
.md-viewer__body {
  font-size: 15px;
  line-height: 1.8;
  color: #303133;
  word-wrap: break-word;
}

/* 空状态 */
.md-viewer__body :deep(.md-empty) {
  color: #c0c4cc;
  text-align: center;
  padding: 40px 0;
}

/* 标题 */
.md-viewer__body :deep(h1) {
  font-size: 26px;
  font-weight: 700;
  margin: 32px 0 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e4e7ed;
  letter-spacing: -0.5px;
}

.md-viewer__body :deep(h2) {
  font-size: 22px;
  font-weight: 600;
  margin: 28px 0 14px;
  padding-left: 12px;
  border-left: 4px solid #409eff;
}

.md-viewer__body :deep(h3) {
  font-size: 18px;
  font-weight: 600;
  margin: 24px 0 12px;
  color: #303133;
}

.md-viewer__body :deep(h4) {
  font-size: 16px;
  font-weight: 600;
  margin: 20px 0 10px;
  color: #606266;
}

.md-viewer__body :deep(h5),
.md-viewer__body :deep(h6) {
  font-size: 14px;
  font-weight: 600;
  margin: 16px 0 8px;
  color: #909399;
}

/* 段落 */
.md-viewer__body :deep(p) {
  margin: 12px 0;
}

/* 行内代码 */
.md-viewer__body :deep(code) {
  background: #f0f4ff;
  color: #5b6abf;
  padding: 2px 8px;
  border-radius: 4px;
  font-family: 'JetBrains Mono', 'Fira Code', 'Monaco', 'Menlo', 'Consolas', monospace;
  font-size: 0.88em;
  word-break: break-word;
}

/* 代码块容器 */
.md-viewer__body :deep(.md-code-block) {
  margin: 16px 0;
  border-radius: 8px;
  overflow: hidden;
  background: #282c34;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.md-viewer__body :deep(.md-code-header) {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 16px;
  background: #21252b;
  border-bottom: 1px solid #373d47;
}

.md-viewer__body :deep(.md-code-lang) {
  font-size: 12px;
  color: #abb2bf;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.md-viewer__body :deep(.md-code-copy) {
  font-size: 12px;
  color: #abb2bf;
  background: transparent;
  border: 1px solid #4b5263;
  border-radius: 4px;
  padding: 2px 10px;
  cursor: pointer;
  transition: all 0.2s;
}

.md-viewer__body :deep(.md-code-copy:hover) {
  color: #61afef;
  border-color: #61afef;
}

.md-viewer__body :deep(.md-code-copy.copied) {
  color: #98c379;
  border-color: #98c379;
}

.md-viewer__body :deep(.md-code-block pre) {
  margin: 0;
  padding: 16px;
  overflow-x: auto;
  background: transparent;
}

.md-viewer__body :deep(.md-code-block pre code) {
  background: none;
  color: #abb2bf;
  padding: 0;
  font-size: 13.5px;
  line-height: 1.6;
  font-family: 'JetBrains Mono', 'Fira Code', 'Monaco', 'Menlo', 'Consolas', monospace;
}

/* 引用块 */
.md-viewer__body :deep(blockquote) {
  margin: 16px 0;
  padding: 12px 20px;
  border-left: 4px solid #409eff;
  background: linear-gradient(to right, #f0f7ff, transparent);
  border-radius: 0 6px 6px 0;
  color: #606266;
}

.md-viewer__body :deep(blockquote p) {
  margin: 4px 0;
}

/* 表格 */
.md-viewer__body :deep(table) {
  border-collapse: separate;
  border-spacing: 0;
  width: 100%;
  margin: 16px 0;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  border: 1px solid #ebeef5;
}

.md-viewer__body :deep(table thead) {
  background: #f5f7fa;
}

.md-viewer__body :deep(table th) {
  font-weight: 600;
  color: #303133;
  padding: 10px 14px;
  text-align: left;
  border-bottom: 2px solid #e4e7ed;
}

.md-viewer__body :deep(table td) {
  padding: 10px 14px;
  border-bottom: 1px solid #ebeef5;
  color: #606266;
}

.md-viewer__body :deep(table tbody tr:nth-child(even)) {
  background: #fafafa;
}

.md-viewer__body :deep(table tbody tr:hover) {
  background: #ecf5ff;
}

.md-viewer__body :deep(table tbody tr:last-child td) {
  border-bottom: none;
}

/* 列表 */
.md-viewer__body :deep(ul),
.md-viewer__body :deep(ol) {
  padding-left: 24px;
  margin: 12px 0;
}

.md-viewer__body :deep(li) {
  margin: 6px 0;
}

.md-viewer__body :deep(li p) {
  margin: 4px 0;
}

/* 任务列表 */
.md-viewer__body :deep(.task-list-item) {
  list-style: none;
  margin-left: -24px;
  padding-left: 0;
}

.md-viewer__body :deep(.task-list-item input[type="checkbox"]) {
  appearance: none;
  width: 16px;
  height: 16px;
  border: 2px solid #c0c4cc;
  border-radius: 3px;
  margin-right: 8px;
  vertical-align: middle;
  position: relative;
  top: -1px;
  cursor: default;
}

.md-viewer__body :deep(.task-list-item input[type="checkbox"]:checked) {
  background: #409eff;
  border-color: #409eff;
}

.md-viewer__body :deep(.task-list-item input[type="checkbox"]:checked::after) {
  content: '';
  position: absolute;
  left: 4px;
  top: 1px;
  width: 5px;
  height: 9px;
  border: 2px solid #fff;
  border-top: none;
  border-left: none;
  transform: rotate(45deg);
}

/* 链接 */
.md-viewer__body :deep(a) {
  color: #409eff;
  text-decoration: none;
  border-bottom: 1px dashed #a0cfff;
  transition: all 0.2s;
}

.md-viewer__body :deep(a:hover) {
  color: #66b1ff;
  border-bottom-style: solid;
}

.md-viewer__body :deep(a.md-external-link::after) {
  content: '\2197';
  font-size: 12px;
  margin-left: 3px;
  display: inline-block;
}

/* 图片 */
.md-viewer__body :deep(img) {
  max-width: 100%;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  margin: 12px 0;
  transition: transform 0.2s, box-shadow 0.2s;
}

.md-viewer__body :deep(img:hover) {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: scale(1.01);
}

/* 分割线 */
.md-viewer__body :deep(hr) {
  border: none;
  height: 1px;
  margin: 32px 0;
  background: linear-gradient(to right, transparent, #dcdfe6, transparent);
}

/* 键盘按键样式（可选） */
.md-viewer__body :deep(kbd) {
  background: #fafafa;
  border: 1px solid #dcdfe6;
  border-bottom-width: 2px;
  border-radius: 3px;
  padding: 1px 6px;
  font-size: 0.85em;
  font-family: inherit;
}

/* ==================== 暗色主题 ==================== */
[data-theme="dark"] .md-viewer__body {
  color: #e0e4ea;
}

[data-theme="dark"] .md-viewer__body :deep(.md-empty) {
  color: #5c6370;
}

[data-theme="dark"] .md-viewer__body :deep(h1) {
  color: #e0e4ea;
  border-bottom-color: #3e4452;
}

[data-theme="dark"] .md-viewer__body :deep(h2) {
  color: #e0e4ea;
  border-left-color: #61afef;
}

[data-theme="dark"] .md-viewer__body :deep(h3) {
  color: #d4d8e0;
}

[data-theme="dark"] .md-viewer__body :deep(h4) {
  color: #abb2bf;
}

[data-theme="dark"] .md-viewer__body :deep(h5),
[data-theme="dark"] .md-viewer__body :deep(h6) {
  color: #7f848e;
}

[data-theme="dark"] .md-viewer__body :deep(code) {
  background: #2c313a;
  color: #e5c07b;
}

[data-theme="dark"] .md-viewer__body :deep(.md-code-block) {
  background: #1e2227;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
}

[data-theme="dark"] .md-viewer__body :deep(.md-code-header) {
  background: #181b20;
  border-bottom-color: #2c313a;
}

[data-theme="dark"] .md-viewer__body :deep(blockquote) {
  background: linear-gradient(to right, rgba(64, 158, 255, 0.06), transparent);
  border-left-color: #61afef;
  color: #abb2bf;
}

[data-theme="dark"] .md-viewer__body :deep(table) {
  border-color: #3e4452;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.2);
}

[data-theme="dark"] .md-viewer__body :deep(table thead) {
  background: #2c313a;
}

[data-theme="dark"] .md-viewer__body :deep(table th) {
  color: #e0e4ea;
  border-bottom-color: #3e4452;
}

[data-theme="dark"] .md-viewer__body :deep(table td) {
  color: #abb2bf;
  border-bottom-color: #2c313a;
}

[data-theme="dark"] .md-viewer__body :deep(table tbody tr:nth-child(even)) {
  background: #21252b;
}

[data-theme="dark"] .md-viewer__body :deep(table tbody tr:hover) {
  background: rgba(64, 158, 255, 0.08);
}

[data-theme="dark"] .md-viewer__body :deep(.task-list-item input[type="checkbox"]) {
  border-color: #4b5263;
  background: #2c313a;
}

[data-theme="dark"] .md-viewer__body :deep(.task-list-item input[type="checkbox"]:checked) {
  background: #61afef;
  border-color: #61afef;
}

[data-theme="dark"] .md-viewer__body :deep(a) {
  color: #61afef;
  border-bottom-color: rgba(97, 175, 239, 0.4);
}

[data-theme="dark"] .md-viewer__body :deep(a:hover) {
  color: #8cc8f8;
}

[data-theme="dark"] .md-viewer__body :deep(img) {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

[data-theme="dark"] .md-viewer__body :deep(hr) {
  background: linear-gradient(to right, transparent, #3e4452, transparent);
}

[data-theme="dark"] .md-viewer__body :deep(kbd) {
  background: #2c313a;
  border-color: #3e4452;
  color: #abb2bf;
}
</style>
