<template>
  <div class="json-page" :class="{ dark: isDark }">
    <div class="tool-frame">
      <!-- 顶部标签栏 -->
      <header class="toolbar">
        <div class="toolbar-left">
          <span class="brand-mark">{ }</span>
          <span class="page-title">JSON 格式化</span>
          <div class="view-switch" role="tablist">
            <button
              class="switch-btn"
              :class="{ active: viewMode === 'text' }"
              role="tab"
              @click="viewMode = 'text'"
            >文本</button>
            <button
              class="switch-btn"
              :class="{ active: viewMode === 'tree' }"
              :disabled="!parsedValue.valid"
              role="tab"
              @click="viewMode = 'tree'"
            >树形</button>
          </div>
        </div>
        <div class="toolbar-right">
          <el-select v-model="indent" size="small" class="indent-select">
            <el-option :value="2" label="缩进 ×2" />
            <el-option :value="4" label="缩进 ×4" />
          </el-select>
          <template v-if="viewMode === 'tree'">
            <button class="btn" @click="expandAll">展开全部</button>
            <button class="btn" @click="collapseAll">收起全部</button>
          </template>
          <button class="btn" :disabled="!parsedValue.valid" @click="handleFormat">格式化</button>
          <button class="btn" :disabled="!parsedValue.valid" @click="handleCompress">压缩</button>
          <button class="btn" :disabled="!rawInput.trim()" @click="handleEscape">转义</button>
          <button class="btn" :disabled="!rawInput.trim()" @click="handleUnescape">反转义</button>
          <button class="btn" :disabled="!parsedValue.valid" @click="handleCopy">复制</button>
          <button class="btn" @click="handleSample">示例</button>
          <button class="btn" :disabled="!rawInput.trim()" @click="handleClear">清空</button>
          <button class="btn-theme" :title="isDark ? '切换浅色' : '切换深色'" @click="toggleTheme">
            <el-icon v-if="isDark"><Sunny /></el-icon>
            <el-icon v-else><Moon /></el-icon>
          </button>
        </div>
      </header>

      <!-- 错误提示条 -->
      <div v-if="rawInput.trim() && !parsedValue.valid" class="error-bar">
        <span class="error-tag">ERR</span>
        <span>{{ parsedValue.error }}</span>
      </div>

      <!-- 工作区卡片 -->
      <main class="workspace">
        <!-- 文本视图：可直接编辑 -->
        <textarea
          v-show="viewMode === 'text'"
          v-model="rawInput"
          class="json-editor"
          :class="{ 'has-error': rawInput.trim() && !parsedValue.valid }"
          placeholder="// 粘贴 JSON，自动格式化，可直接编辑"
          spellcheck="false"
          @paste="handlePaste"
        ></textarea>

        <!-- 树形视图：展开收起预览 -->
        <div v-show="viewMode === 'tree'" class="tree-body">
          <div v-if="parsedValue.valid" class="preview-tree">
            <json-node
              :value="parsedValue.data"
              :depth="0"
              :expand-signal="expandSignal"
              :collapse-signal="collapseSignal"
            />
          </div>
        </div>
      </main>

      <!-- 底部状态栏 -->
      <footer class="status-bar">
        <span class="status-item">
          <span class="status-dot" :class="statusClass"></span>
          {{ statusText }}
        </span>
        <span class="status-item">{{ stats.chars.toLocaleString() }} 字符</span>
        <span class="status-item">{{ stats.lines.toLocaleString() }} 行</span>
        <template v-if="parsedValue.valid">
          <span class="status-item">深度 {{ stats.depth }}</span>
          <span class="status-item">{{ stats.nodes.toLocaleString() }} 节点</span>
        </template>
        <span class="status-spacer"></span>
        <span class="status-item status-brand">元宝工具箱</span>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Moon, Sunny } from '@element-plus/icons-vue'
import JsonNode from './JsonNode.vue'

/** 编辑区内容（格式化后的文本也在此直接编辑） */
const rawInput = ref('')
/** 视图模式：text 文本 / tree 树形 */
const viewMode = ref('text')
/** 缩进空格数 */
const indent = ref(2)
/** 是否深色主题 */
const isDark = ref(false)
/** 展开全部信号（递增通知所有节点） */
const expandSignal = ref(0)
/** 收起全部信号（递增通知所有节点） */
const collapseSignal = ref(0)

/** 解析结果：valid 表示是否为合法 JSON */
const parsedValue = computed(() => {
  const text = rawInput.value.trim()
  if (!text) {
    return { valid: false, data: null, error: '' }
  }
  try {
    return { valid: true, data: JSON.parse(text), error: '' }
  } catch (e) {
    return { valid: false, data: null, error: formatError(e.message, text) }
  }
})

/** 按当前缩进格式化后的文本 */
const formattedText = computed(() => {
  if (!parsedValue.value.valid) {
    return ''
  }
  return JSON.stringify(parsedValue.value.data, null, indent.value)
})

/** 状态栏统计：字符数、行数、嵌套深度、节点数 */
const stats = computed(() => {
  const text = rawInput.value
  const result = {
    chars: text.length,
    lines: text ? text.split('\n').length : 0,
    depth: 0,
    nodes: 0
  }
  if (parsedValue.value.valid) {
    const walk = (value, depth) => {
      result.nodes++
      if (depth > result.depth) {
        result.depth = depth
      }
      if (value !== null && typeof value === 'object') {
        Object.values(value).forEach(child => walk(child, depth + 1))
      }
    }
    walk(parsedValue.value.data, 1)
  }
  return result
})

/** 状态栏指示灯样式 */
const statusClass = computed(() => {
  if (!rawInput.value.trim()) {
    return 'idle'
  }
  return parsedValue.value.valid ? 'ok' : 'err'
})

/** 状态栏文字 */
const statusText = computed(() => {
  if (!rawInput.value.trim()) {
    return '就绪'
  }
  return parsedValue.value.valid ? 'JSON 有效' : '格式错误'
})

// 切换缩进时，内容合法则原地重新格式化
watch(indent, () => {
  if (parsedValue.value.valid) {
    rawInput.value = formattedText.value
  }
})

/**
 * 将 JSON.parse 的英文错误信息转换为更友好的中文提示
 * @param {string} message 原始错误信息
 * @param {string} text 原始输入
 * @returns {string} 中文错误描述
 */
function formatError(message, text) {
  // Chrome: "... at position 123"；尽量提取位置换算行号
  const posMatch = message.match(/position\s+(\d+)/i)
  if (posMatch) {
    const pos = Number(posMatch[1])
    const line = text.slice(0, pos).split('\n').length
    return `${message}（第 ${line} 行附近）`
  }
  return message
}

/**
 * 粘贴完成后自动原地格式化（内容非法则保持原样）
 */
async function handlePaste() {
  await nextTick()
  if (parsedValue.value.valid) {
    rawInput.value = formattedText.value
  }
}

/** 手动格式化当前内容 */
function handleFormat() {
  if (!parsedValue.value.valid) {
    ElMessage.error('JSON 格式错误，无法格式化')
    return
  }
  rawInput.value = formattedText.value
  ElMessage.success('格式化完成')
}

/** 展开全部节点 */
function expandAll() {
  expandSignal.value++
}

/** 收起全部节点（根节点保持展开） */
function collapseAll() {
  collapseSignal.value++
}

/** 压缩为单行 JSON */
function handleCompress() {
  if (!parsedValue.value.valid) {
    ElMessage.error('JSON 格式错误，无法压缩')
    return
  }
  rawInput.value = JSON.stringify(parsedValue.value.data)
  viewMode.value = 'text'
  ElMessage.success('已压缩为单行')
}

/**
 * 转义：将整段文本转为 JSON 字符串字面量
 * 例如 {"a":1} → "{\"a\":1}"
 */
function handleEscape() {
  const text = rawInput.value.trim()
  if (!text) {
    return
  }
  rawInput.value = JSON.stringify(text)
  viewMode.value = 'text'
  ElMessage.success('已转义')
}

/**
 * 反转义：将 JSON 字符串字面量还原为原始文本
 * 例如 "{\"a\":1}" → {"a":1}
 */
function handleUnescape() {
  const text = rawInput.value.trim()
  if (!text) {
    return
  }
  try {
    const result = JSON.parse(text)
    if (typeof result !== 'string') {
      ElMessage.error('内容不是 JSON 字符串，无法反转义')
      return
    }
    rawInput.value = result
    viewMode.value = 'text'
    ElMessage.success('已反转义')
  } catch (e) {
    ElMessage.error(`反转义失败：${formatError(e.message, text)}`)
  }
}

/** 复制格式化结果到剪贴板 */
async function handleCopy() {
  if (!parsedValue.value.valid) {
    ElMessage.error('JSON 格式错误，无法复制')
    return
  }
  try {
    await navigator.clipboard.writeText(formattedText.value)
    ElMessage.success('已复制到剪贴板')
  } catch (e) {
    // 剪贴板 API 不可用（非 HTTPS 等场景）时降级到 textarea 方案
    const textarea = document.createElement('textarea')
    textarea.value = formattedText.value
    document.body.appendChild(textarea)
    textarea.select()
    document.execCommand('copy')
    document.body.removeChild(textarea)
    ElMessage.success('已复制到剪贴板')
  }
}

/** 填充示例数据 */
function handleSample() {
  rawInput.value = JSON.stringify({
    name: 'JSON 格式化工具',
    version: '1.0.0',
    features: ['一键格式化', '树形展开收起', '语法高亮'],
    config: {
      indent: 2,
      theme: 'auto',
      maxDepth: null
    },
    downloads: 10240,
    free: true
  }, null, indent.value)
}

/** 清空输入 */
function handleClear() {
  rawInput.value = ''
  viewMode.value = 'text'
}

/** 切换深浅主题 */
function toggleTheme() {
  isDark.value = !isDark.value
}
</script>

<style scoped>
/* ========== 设计令牌 ========== */
.json-page {
  --json-bg: #eef1f6;
  --json-panel-bg: #ffffff;
  --json-text: #1e2433;
  --json-text-secondary: #6b7385;
  --json-border: #dde2ec;
  --json-key: #7a3db8;
  --json-string: #b3352b;
  --json-number: #175cd3;
  --json-boolean: #0e7490;
  --json-null: #8a8f9c;
  --json-accent: #3b5bfd;
  --json-accent-soft: rgba(59, 91, 253, 0.08);
  --json-hover-bg: rgba(59, 91, 253, 0.1);
  --json-error-bg: #fdecec;
  --json-error-text: #d92d20;
  --json-statusbar-bg: #1e2433;
  --json-statusbar-text: #c8cfdd;
  --json-ok: #12b76a;
  --font-mono: 'JetBrains Mono', Consolas, 'Courier New', monospace;
  --font-ui: 'PingFang SC', 'Microsoft YaHei', 'Helvetica Neue', sans-serif;

  min-height: 100vh;
  background-color: var(--json-bg);
  color: var(--json-text);
  font-family: var(--font-ui);
}

.json-page.dark {
  --json-bg: #14161c;
  --json-panel-bg: #1c1f28;
  --json-text: #dfe3ec;
  --json-text-secondary: #8b93a5;
  --json-border: #2c313e;
  --json-key: #c39af0;
  --json-string: #f0998f;
  --json-number: #84adff;
  --json-boolean: #5dd4c6;
  --json-null: #7d8494;
  --json-accent: #7c9aff;
  --json-accent-soft: rgba(124, 154, 255, 0.12);
  --json-hover-bg: rgba(124, 154, 255, 0.14);
  --json-error-bg: #3a2226;
  --json-error-text: #ff8a80;
  --json-statusbar-bg: #0e1015;
  --json-statusbar-text: #9aa3b5;
  --json-ok: #34d399;
}

/* ========== 整体框架：通栏全屏 ========== */
.tool-frame {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: var(--json-panel-bg);
}

/* ========== 顶部标签栏 ========== */
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 8px;
  padding: 10px 16px;
  border-bottom: 1px solid var(--json-border);
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.brand-mark {
  font-family: var(--font-mono);
  font-size: 14px;
  font-weight: 700;
  color: var(--json-accent);
  background: var(--json-accent-soft);
  padding: 3px 8px;
  border-radius: 5px;
  letter-spacing: 1px;
}

.page-title {
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.view-switch {
  display: inline-flex;
  border: 1px solid var(--json-border);
  border-radius: 6px;
  overflow: hidden;
}

.switch-btn {
  padding: 5px 16px;
  font-family: var(--font-mono);
  font-size: 12px;
  letter-spacing: 1px;
  color: var(--json-text-secondary);
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all 0.15s;
}

.switch-btn + .switch-btn {
  border-left: 1px solid var(--json-border);
}

.switch-btn.active {
  color: #ffffff;
  background: var(--json-accent);
}

.switch-btn:not(.active):hover:not(:disabled) {
  color: var(--json-accent);
  background: var(--json-accent-soft);
}

.switch-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}

.indent-select {
  width: 96px;
}

.btn {
  padding: 5px 12px;
  font-family: var(--font-mono);
  font-size: 12px;
  letter-spacing: 1px;
  color: var(--json-text);
  background: transparent;
  border: 1px solid var(--json-border);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.15s;
}

.btn:hover:not(:disabled) {
  color: var(--json-accent);
  border-color: var(--json-accent);
  background: var(--json-accent-soft);
}

.btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.btn-theme {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  color: var(--json-text-secondary);
  background: transparent;
  border: 1px solid var(--json-border);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.15s;
}

.btn-theme:hover {
  color: var(--json-accent);
  border-color: var(--json-accent);
}

/* ========== 错误提示条 ========== */
.error-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 7px 16px;
  font-family: var(--font-mono);
  font-size: 12px;
  color: var(--json-error-text);
  background: var(--json-error-bg);
  border-bottom: 1px solid var(--json-border);
  word-break: break-all;
}

.error-tag {
  flex-shrink: 0;
  padding: 1px 6px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 1px;
  color: #ffffff;
  background: var(--json-error-text);
  border-radius: 3px;
}

/* ========== 工作区 ========== */
.workspace {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.json-editor {
  flex: 1;
  padding: 16px 20px;
  font-family: var(--font-mono);
  font-size: 13px;
  line-height: 1.7;
  color: var(--json-text);
  background: transparent;
  border: none;
  outline: none;
  resize: none;
}

.json-editor::placeholder {
  color: var(--json-text-secondary);
  opacity: 0.6;
}

.json-editor.has-error {
  background: var(--json-error-bg);
}

.tree-body {
  flex: 1;
  overflow: auto;
  padding: 16px 20px;
}

.preview-tree {
  min-width: max-content;
}

/* ========== 底部状态栏（签名元素） ========== */
.status-bar {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 6px 16px;
  font-family: var(--font-mono);
  font-size: 11px;
  letter-spacing: 0.5px;
  color: var(--json-statusbar-text);
  background: var(--json-statusbar-bg);
  user-select: none;
}

.status-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
}

.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
}

.status-dot.idle {
  background: var(--json-statusbar-text);
  opacity: 0.5;
}

.status-dot.ok {
  background: var(--json-ok);
  box-shadow: 0 0 6px var(--json-ok);
}

.status-dot.err {
  background: var(--json-error-text);
  box-shadow: 0 0 6px var(--json-error-text);
}

.status-spacer {
  flex: 1;
}

.status-brand {
  opacity: 0.55;
  letter-spacing: 2px;
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .status-bar {
    gap: 12px;
    overflow-x: auto;
  }

  .status-brand {
    display: none;
  }
}

@media (prefers-reduced-motion: reduce) {
  .btn,
  .switch-btn,
  .btn-theme {
    transition: none;
  }
}
</style>
