<template>
  <div class="gen-container">
    <!-- 单卡片：tabs 切换码制 + 操作栏（生成按钮/筛选/刷新 放一起）+ 卡片网格 + 分页 -->
    <el-card class="gen-card result-card">
      <el-tabs v-model="activeType" class="gen-tabs" @tab-change="handleTabChange">
        <el-tab-pane label="二维码" name="QR" />
        <el-tab-pane label="条形码" name="CODE128" />
      </el-tabs>

      <!-- 操作栏：左=筛选，右=批量生成 + 刷新 同一行 -->
      <div class="toolbar">
        <div class="filter-left">
          <span class="result-count">{{ currentTypeLabel }} 共 {{ total }} 个</span>
          <el-radio-group v-model="boundFilter" size="small" @change="handleSearch">
            <el-radio-button :value="null">全部</el-radio-button>
            <el-radio-button :value="false">未绑定</el-radio-button>
            <el-radio-button :value="true">已绑定</el-radio-button>
          </el-radio-group>
        </div>
        <div class="toolbar-right">
          <el-button type="warning" :icon="Printer" @click="openPrintDialog">打印</el-button>
          <el-button type="success" :icon="Refresh" :loading="listLoading" @click="handleSearch">刷新</el-button>
          <el-button type="primary" :icon="Plus" :loading="genLoading" @click="openDialog(activeType)">
            批量生成
          </el-button>
        </div>
      </div>

      <!-- 数量输入对话框 -->
      <el-dialog
        :model-value="dialogVisible"
        title="批量生成"
        width="420px"
        :close-on-click-modal="false"
        @update:model-value="(v) => (dialogVisible = v)"
      >
        <div class="dialog-body">
          <span class="dialog-label">生成{{ dialogType === 'QR' ? '二维码' : '条形码' }}数量</span>
          <el-input-number v-model="dialogCount" :min="1" :max="200" :step="1" step-strictly controls-position="right" />
        </div>
        <template #footer>
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" :loading="genLoading" @click="handleGenerateBatch">确 定</el-button>
        </template>
      </el-dialog>

      <!-- 打印选择对话框：列出未绑定二维码，可勾选后打印 -->
      <el-dialog v-model="printDialogVisible" title="打印未绑定二维码" width="720px" append-to-body :close-on-click-modal="false">
        <div class="print-select-head">
          <span>未绑定二维码共 {{ unboundList.length }} 个，选择要打印的：</span>
          <el-checkbox :model-value="allSelected" @change="handleToggleAll">全选</el-checkbox>
        </div>
        <el-empty v-if="!printLoading && !unboundList.length" description="暂无未绑定二维码" :image-size="60" />
        <div v-else-if="printLoading" class="print-loading">加载中…</div>
        <div v-else class="print-select-grid">
          <label v-for="b in unboundList" :key="b.id" class="print-select-item">
            <span class="print-select-check">
              <input type="checkbox" :checked="!!selected[b.id]" @change="(e) => toggleItem(b.id, e.target.checked)" />
            </span>
            <img :src="b.fileUrl" alt="预览" />
            <span class="print-select-code">{{ b.code }}</span>
          </label>
        </div>
        <template #footer>
          <el-button @click="printDialogVisible = false">取 消</el-button>
          <el-button type="primary" :disabled="!selectedCount" @click="confirmPrint">
            打印（{{ selectedCount }} 个）
          </el-button>
        </template>
      </el-dialog>

      <el-empty v-if="!listLoading && !records.length" description="暂无数据，请先点击批量生成" />

      <div v-else class="result-grid">
        <div v-for="item in records" :key="item.id" class="code-item">
          <div class="code-img-wrap">
            <img v-if="item.fileUrl" :src="item.fileUrl" class="code-img" alt="条码预览" />
            <span v-else class="no-img">无图片</span>
          </div>
          <div class="code-content" :title="item.code">{{ item.code }}</div>
          <el-tag :type="item.bound ? 'danger' : 'success'" size="small" effect="light">
            {{ item.bound ? '已绑定' : '未绑定' }}
          </el-tag>
          <div class="code-time">{{ item.createTime }}</div>
          <div class="code-actions">
            <el-button size="small" type="primary" :icon="Download" @click="handleDownload(item)">下载</el-button>
            <el-button size="small" type="danger" text :icon="Delete" @click="handleDelete(item)">删除</el-button>
          </div>
        </div>
      </div>

      <div v-if="total > pageSize" class="pager-row">
        <el-pagination
          background
          layout="prev, pager, next, total"
          :current-page="page"
          :page-size="pageSize"
          :total="total"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>

  <!-- 打印通过独立隐藏 iframe 实现（与主页面 DOM 完全隔离），不再渲染页面内打印区 -->
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Download, Refresh, Delete, Plus, Printer } from '@element-plus/icons-vue'
import { generateBarcodeBatch, listBarcodes, deleteBarcodeRecord } from '@/api/barcode'

const activeType = ref('QR')
const genLoading = ref(false)
const listLoading = ref(false)
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)
const records = ref([])
const boundFilter = ref(null)

// 批量生成对话框状态
const dialogVisible = ref(false)
const dialogType = ref('QR')
const dialogCount = ref(10)

// 打印状态：选择对话框（打印内容经独立隐藏 iframe 渲染，与页面 DOM 完全隔离）
const printDialogVisible = ref(false)
const printLoading = ref(false)
const unboundList = ref([])
const selected = ref({})

// 每 A4 页二维码数量（3 列 × 5 行 = 15）
const PRINT_PAGE_SIZE = 15

const currentTypeLabel = computed(() => (activeType.value === 'QR' ? '二维码' : '条形码'))

const allSelected = computed(() =>
  unboundList.value.length > 0 && unboundList.value.every((b) => !!selected.value[b.id])
)
const selectedItems = computed(() => unboundList.value.filter((b) => !!selected.value[b.id]))
const selectedCount = computed(() => selectedItems.value.length)

/** 翻页拉取当前类型的全部未绑定二维码 */
async function fetchAllUnbound() {
  const all = []
  const size = 200
  let p = 1
  let total
  do {
    const res = await listBarcodes({ type: typeParam(), bound: false, page: p, size })
    const data = res && res.data
    const rows = (data && data.records) || []
    all.push(...rows)
    total = (data && data.total) || 0
    p++
  } while (all.length < total)
  return all
}

async function openPrintDialog() {
  printDialogVisible.value = true
  printLoading.value = true
  try {
    const all = await fetchAllUnbound()
    unboundList.value = all
    selected.value = {}
    all.forEach((b) => {
      selected.value[b.id] = true
    })
  } catch (e) {
    ElMessage.error('加载未绑定二维码失败')
  } finally {
    printLoading.value = false
  }
}

function handleToggleAll() {
  const next = !allSelected.value
  unboundList.value.forEach((b) => {
    selected.value[b.id] = next
  })
}

function toggleItem(id, checked) {
  selected.value[id] = checked
}

function confirmPrint() {
  const items = selectedItems.value
  if (!items.length) {
    ElMessage.warning('请至少勾选一个二维码')
    return
  }
  // 关闭选择弹框
  printDialogVisible.value = false
  // 按每页 15 个分页，直接以 COS 预签名图 URL 写进 iframe（<img> 跨域加载无需 CORS）
  const pages = []
  for (let i = 0; i < items.length; i += PRINT_PAGE_SIZE) {
    pages.push(items.slice(i, i + PRINT_PAGE_SIZE))
  }
  const pageHtml = pages
    .map((pg) => {
      const cells = pg
        .map((b) => {
          return `<div class="a4-cell">
            <img src="${b.fileUrl}" alt="条码" />
            <div class="a4-code">${b.code}</div>
          </div>`
        })
        .join('')
      return `<div class="a4-page">${cells}</div>`
    })
    .join('')
  printInIframe(pageHtml)
}

/** 把 A4 网格写入独立隐藏 iframe 并触发打印，与主页面 DOM/弹框完全隔离 */
function printInIframe(pageHtml) {
  const style = `
    @page { size: A4; margin: 0; }
    * { margin: 0; padding: 0; box-sizing: border-box; }
    html, body { background: #fff; }
    .a4-page {
      width: 210mm; min-height: 297mm;
      display: grid; grid-template-columns: repeat(3, 1fr);
      grid-auto-rows: 52mm; gap: 4mm; padding: 8mm 6mm;
      page-break-after: always;
    }
    .a4-page:last-child { page-break-after: auto; }
    .a4-cell {
      border: 1px solid #999; border-radius: 2mm;
      display: flex; flex-direction: column; align-items: center; justify-content: center;
      gap: 2mm; padding: 2mm; overflow: hidden;
    }
    .a4-cell img { max-width: 90%; max-height: 78%; object-fit: contain; }
    .a4-code { font-size: 10pt; color: #000; max-width: 100%; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
  `
  const iframe = document.createElement('iframe')
  iframe.setAttribute('aria-hidden', 'true')
  iframe.style.cssText = 'position:fixed;right:0;bottom:0;width:0;height:0;border:0;visibility:hidden;'
  document.body.appendChild(iframe)
  const doc = iframe.contentWindow.document
  doc.open()
  doc.write(
    `<!doctype html><html><head><meta charset="utf-8"><style>${style}</style></head><body>${pageHtml}</body></html>`
  )
  doc.close()
  const win = iframe.contentWindow
  // 等 iframe 内容与图片渲染完成后再打印，超时兜底
  const images = Array.from(doc.querySelectorAll('img'))
  const pending = images.filter((i) => !i.complete)
  if (pending.length) {
    Promise.all(
      pending.map(
        (i) =>
          new Promise((r) => {
            i.onload = r
            i.onerror = r
            setTimeout(r, 3000)
          })
      )
    ).then(() => finishPrint(win, iframe))
  } else {
    finishPrint(win, iframe)
  }
}

function finishPrint(win, iframe) {
  win.focus()
  win.print()
  // 打印对话框在多数浏览器是阻塞的，这里延迟再移除 iframe
  setTimeout(() => iframe.remove(), 1500)
}

function typeParam() {
  return activeType.value === 'QR' ? 2 : 1
}

async function loadList() {
  const params = {
    type: typeParam(),
    page: page.value,
    size: pageSize.value
  }
  if (boundFilter.value !== null) {
    params.bound = boundFilter.value
  }
  listLoading.value = true
  try {
    const res = await listBarcodes(params)
    const data = res && res.data
    records.value = (data && data.records) || []
    total.value = (data && data.total) || 0
  } catch (e) {
    ElMessage.error('加载列表失败')
  } finally {
    listLoading.value = false
  }
}

function handleTabChange() {
  page.value = 1
  loadList()
}

function handleSearch() {
  page.value = 1
  loadList()
}

function handlePageChange(p) {
  page.value = p
  loadList()
}

function openDialog(type) {
  dialogType.value = type
  dialogCount.value = 10
  dialogVisible.value = true
}

async function handleGenerateBatch() {
  const type = dialogType.value
  const count = dialogCount.value
  if (!count || count < 1) {
    ElMessage.warning('请输入生成数量')
    return
  }
  genLoading.value = true
  try {
    await generateBarcodeBatch(type, count)
    ElMessage.success(`成功生成 ${count} 个${type === 'QR' ? '二维码' : '条形码'}`)
    dialogVisible.value = false
    page.value = 1
    loadList()
  } catch (e) {
    ElMessage.error((e && e.message) || '批量生成失败')
  } finally {
    genLoading.value = false
  }
}

function triggerDownload(url, filename) {
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  document.body.appendChild(a)
  a.click()
  a.remove()
}

async function handleDownload(item) {
  if (!item.fileUrl) return
  try {
    // COS 预签名地址下载：先落地为 blob 再触发下载，保证存为本地文件
    const resp = await fetch(item.fileUrl)
    const blob = await resp.blob()
    const url = URL.createObjectURL(blob)
    triggerDownload(url, `barcode_${item.code || Date.now()}.png`)
    setTimeout(() => URL.revokeObjectURL(url), 1000)
  } catch (e) {
    ElMessage.error('下载失败，请重试')
  }
}

async function handleDelete(item) {
  try {
    await ElMessageBox.confirm(`确认删除条码 ${item.code} 吗？`, '删除确认', { type: 'warning' })
  } catch (e) {
    return
  }
  try {
    await deleteBarcodeRecord(item.id)
    ElMessage.success('删除成功')
    loadList()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

// 首次加载
loadList()
</script>

<style scoped>
.gen-container {
  padding: 16px;
}
.gen-tabs :deep(.el-tabs__header) {
  margin-bottom: 8px;
}
.result-card {
  animation: fade-up 0.3s ease;
}
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
  flex-wrap: wrap;
  gap: 8px;
}
.filter-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.result-count {
  color: #606266;
  font-weight: 500;
}
.dialog-body {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}
.dialog-label {
  color: #303133;
  font-size: 14px;
}
.result-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}
.code-item {
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  background: #fff;
}
.code-img-wrap {
  min-height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
}
.code-img {
  max-width: 180px;
  max-height: 96px;
}
.no-img {
  color: #c0c4cc;
  font-size: 12px;
}
.code-content {
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 600;
  color: #303133;
  font-size: 13px;
}
.code-time {
  color: #c0c4cc;
  font-size: 12px;
}
.code-actions {
  display: flex;
  gap: 4px;
  align-items: center;
}
.pager-row {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
.print-select-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  color: #606266;
}
.print-loading {
  padding: 24px 0;
  text-align: center;
  color: #909399;
}
.print-select-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 10px;
  max-height: 420px;
  overflow-y: auto;
}
.print-select-item {
  position: relative;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  background: #fff;
}
.print-select-item img {
  width: 64px;
  height: 64px;
  object-fit: cover;
}
.print-select-check {
  position: absolute;
  top: 4px;
  left: 6px;
}
.print-select-code {
  font-size: 11px;
  color: #303133;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 打印样式已内联进独立隐藏 iframe（printInIframe），不再依赖页面内 CSS/：本页无需打印样式 */
@keyframes fade-up {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>