<template>
  <div class="product-codes">
    <!-- 已有条码列表 -->
    <el-table :data="codeList" border stripe size="small" style="margin-bottom: 16px">
      <el-table-column label="条码图" width="90" align="center">
        <template #default="{ row }">
          <el-image
            v-if="row.fileUrl"
            :src="row.fileUrl"
            :preview-src-list="[row.fileUrl]"
            preview-teleported
            fit="contain"
            style="width: 56px; height: 56px"
          />
          <el-tag v-else type="info" size="small">无图</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="code" label="条码内容" min-width="170" show-overflow-tooltip />
      <el-table-column label="类型" width="80">
        <template #default="{ row }">
          <el-tag :type="row.type === 2 ? 'warning' : ''" size="small">
            {{ row.type === 2 ? '二维码' : '条形码' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="来源" width="110">
        <template #default="{ row }">
          <el-tag :type="row.source === 2 ? 'success' : 'info'" size="small">
            {{ row.source === 2 ? '图片识别' : '自生成' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="状态" width="90">
        <template #default>
          <el-tag type="success" size="small">已绑定</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="90" fixed="right">
        <template #default="{ row }">
          <el-button type="danger" link size="small" @click="handleUnbind(row.id)">解绑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 生成条码/二维码（无需填写内容，系统自动生成唯一编号） -->
    <el-divider content-position="left">生成条码 / 二维码</el-divider>
    <el-form :inline="true">
      <el-form-item label="码制">
        <el-radio-group v-model="genType">
          <el-radio-button value="CODE128">条形码</el-radio-button>
          <el-radio-button value="QR">二维码</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="genLoading" @click="handleGenCode">生成</el-button>
        <el-button type="success" v-if="previewUrl" @click="handleBindCode">绑定到商品</el-button>
        <el-button type="warning" @click="openBindDialog">绑定已生成</el-button>
      </el-form-item>
    </el-form>

    <!-- 预览 -->
    <div v-if="previewUrl" class="preview-area">
      <img :src="previewUrl" :alt="genContent" class="barcode-img" />
      <div class="code-text">{{ genContent }}</div>
      <el-button type="primary" :icon="Download" @click="handleDownload">下载图片</el-button>
    </div>

    <!-- 绑定已生成的未绑定二维码/条形码 -->
    <el-dialog v-model="bindDialogVisible" title="绑定已生成的二维码 / 条形码" width="720px" append-to-body :close-on-click-modal="false">
      <div class="bind-select-head">
        <span>未绑定的已生成码共 {{ unboundList.length }} 个，选择要绑定到本商品的：</span>
        <el-checkbox :model-value="allSelected" @change="handleToggleAll">全选</el-checkbox>
      </div>
      <el-empty v-if="!bindLoading && !unboundList.length" description="暂无未绑定码，请先在生成条码页生成" :image-size="60" />
      <div v-else-if="bindLoading" class="bind-loading">加载中…</div>
      <div v-else class="bind-select-grid">
        <label v-for="b in unboundList" :key="b.id" class="bind-select-item">
          <span class="bind-select-check">
            <input type="checkbox" :checked="!!bindSelected[b.id]" @change="(e) => toggleBind(b.id, e.target.checked)" />
          </span>
          <img :src="b.fileUrl" alt="预览" />
          <span class="bind-select-code">{{ b.code }}（{{ b.type === 2 ? '二维码' : '条形码' }}）</span>
        </label>
      </div>
      <template #footer>
        <el-button @click="bindDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="bindLoading" :disabled="!bindCount" @click="confirmBind">
          绑定（{{ bindCount }} 个）
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Download } from '@element-plus/icons-vue'
import { listProductCodes, bindCode, unbindCode } from '@/api/product'
import { generateBarcodeBatch, listBarcodes } from '@/api/barcode'

const props = defineProps({
  productId: { type: Number, required: true }
})
const emit = defineEmits(['refresh'])

const codeList = ref([])
const genContent = ref('')
const genType = ref('CODE128')
const genLoading = ref(false)
const previewUrl = ref('')

// 绑定已生成的未绑定码状态
const bindDialogVisible = ref(false)
const bindLoading = ref(false)
const unboundList = ref([])
const bindSelected = ref({})

const allSelected = computed(() =>
  unboundList.value.length > 0 && unboundList.value.every((b) => !!bindSelected.value[b.id])
)
const bindCount = computed(() => unboundList.value.filter((b) => !!bindSelected.value[b.id]).length)

async function loadCodes() {
  const res = await listProductCodes(props.productId)
  codeList.value = (res.data || res) || []
}

async function handleGenCode() {
  genLoading.value = true
  try {
    // 与条码管理页一致：系统自动生成唯一编号（二维码 BR 前缀 / 条形码 BAR 前缀）并落库，可点「绑定到商品」
    const res = await generateBarcodeBatch(genType.value, 1)
    const list = (res && res.data) || res || []
    const item = Array.isArray(list) ? list[0] : list
    if (item && item.code) {
      genContent.value = item.code
      previewUrl.value = item.fileUrl
      ElMessage.success(`已生成 ${item.code}，可点击「绑定到商品」绑定`)
    } else {
      ElMessage.error('生成失败，请重试')
    }
  } finally {
    genLoading.value = false
  }
}

/** 翻页拉取全部未绑定的已生成码（不分码制），供勾选绑定到本商品 */
async function fetchUnboundAll() {
  const all = []
  const size = 200
  let p = 1
  let total
  do {
    const res = await listBarcodes({ bound: false, page: p, size })
    const data = res && res.data
    const rows = (data && data.records) || []
    all.push(...rows)
    total = (data && data.total) || 0
    p++
  } while (all.length < total)
  return all
}

async function openBindDialog() {
  bindDialogVisible.value = true
  bindLoading.value = true
  try {
    const all = await fetchUnboundAll()
    unboundList.value = all
    bindSelected.value = {}
    all.forEach((b) => {
      bindSelected.value[b.id] = true
    })
  } catch (e) {
    ElMessage.error('加载未绑定码失败')
  } finally {
    bindLoading.value = false
  }
}

function handleToggleAll() {
  const next = !allSelected.value
  unboundList.value.forEach((b) => {
    bindSelected.value[b.id] = next
  })
}

function toggleBind(id, checked) {
  bindSelected.value[id] = checked
}

async function confirmBind() {
  const items = unboundList.value.filter((b) => !!bindSelected.value[b.id])
  if (!items.length) {
    ElMessage.warning('请至少勾选一个')
    return
  }
  bindLoading.value = true
  try {
    for (const b of items) {
      await bindCode(props.productId, { code: b.code, type: b.type, source: 1 })
    }
    ElMessage.success(`绑定成功 ${items.length} 个`)
    bindDialogVisible.value = false
    loadCodes()
    emit('refresh')
  } catch (e) {
    ElMessage.error((e && e.message) || '绑定失败')
  } finally {
    bindLoading.value = false
  }
}


async function handleDownload() {
  if (!previewUrl.value) return
  try {
    // COS 预签名地址下载：先落地为 blob 再触发下载
    const resp = await fetch(previewUrl.value)
    const blob = await resp.blob()
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `barcode_${genContent.value || Date.now()}.png`
    document.body.appendChild(a)
    a.click()
    a.remove()
    setTimeout(() => URL.revokeObjectURL(url), 1000)
  } catch (e) {
    ElMessage.error('下载失败，请重试')
  }
}

async function handleBindCode() {
  if (!genContent.value.trim()) return
  const res = await bindCode(props.productId, {
    code: genContent.value.trim(),
    type: genType.value === 'QR' ? 2 : 1,
    source: 1
  })
  ElMessage.success('绑定成功')
  previewUrl.value = ''
  loadCodes()
  emit('refresh')
}

async function handleUnbind(codeId) {
  await ElMessageBox.confirm('确定解绑该条码吗？', '提示', { type: 'warning' })
  await unbindCode(props.productId, codeId)
  ElMessage.success('解绑成功')
  loadCodes()
}

watch(() => props.productId, loadCodes)
onMounted(loadCodes)
</script>

<style scoped>
.preview-area {
  margin-top: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px;
  background: #fff;
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
}
.barcode-img {
  max-width: 260px;
  max-height: 180px;
}
.code-text {
  font-weight: 600;
  color: #303133;
}
.bind-select-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  color: #606266;
}
.bind-loading {
  padding: 24px 0;
  text-align: center;
  color: #909399;
}
.bind-select-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 10px;
  max-height: 420px;
  overflow-y: auto;
}
.bind-select-item {
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
.bind-select-item img {
  width: 64px;
  height: 64px;
  object-fit: cover;
}
.bind-select-check {
  position: absolute;
  top: 4px;
  left: 6px;
}
.bind-select-code {
  font-size: 11px;
  color: #303133;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>