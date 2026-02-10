<template>
  <div class="token-container">
    <!-- 操作栏 -->
    <el-card class="action-card" shadow="never">
      <el-button type="primary" :icon="Plus" @click="handleAdd">添加Token</el-button>
      <el-button type="danger" :icon="Delete" @click="handleBatchDelete" :disabled="selectedIds.length === 0">
        批量删除
      </el-button>
      <el-button type="warning" :icon="Refresh" @click="handleResetDaily">重置每日计数</el-button>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card" shadow="never">
      <el-table v-loading="loading" :data="tableData" border stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="tokenName" label="Token名称" width="150" align="center" />
        <el-table-column prop="tokenValue" label="Token值" min-width="200" align="center">
          <template #default="{ row }">
            <span>{{ maskToken(row.tokenValue) }}</span>
            <el-button type="primary" link size="small" @click="copyToken(row.tokenValue)">复制</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="provider" label="服务商" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success" size="small">正常</el-tag>
            <el-tag v-else type="danger" size="small">作废</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dailyLimit" label="每日限额" width="100" align="center">
          <template #default="{ row }">
            {{ row.dailyLimit === 0 ? '无限制' : row.dailyLimit }}
          </template>
        </el-table-column>
        <el-table-column prop="dailyUsed" label="今日已用" width="100" align="center" />
        <el-table-column prop="useCount" label="总使用次数" width="100" align="center" />
        <el-table-column prop="lastUsedTime" label="最后使用时间" width="180" align="center" />
        <el-table-column prop="expireTime" label="过期时间" width="180" align="center">
          <template #default="{ row }">
            {{ row.expireTime || '永不过期' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button
              v-if="row.status === 1"
              type="warning"
              link
              @click="handleDisable(row)"
            >作废</el-button>
            <el-button
              v-else
              type="success"
              link
              @click="handleEnable(row)"
            >启用</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="Token名称" prop="tokenName">
          <el-input v-model="form.tokenName" placeholder="请输入Token名称" />
        </el-form-item>
        <el-form-item label="Token值" prop="tokenValue">
          <el-input v-model="form.tokenValue" placeholder="请输入Token值" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="服务商" prop="provider">
          <el-select v-model="form.provider" placeholder="请选择服务商" style="width: 100%">
            <el-option label="iTick" value="itick" />
          </el-select>
        </el-form-item>
        <el-form-item label="每日限额" prop="dailyLimit">
          <el-input-number v-model="form.dailyLimit" :min="0" placeholder="0表示无限制" style="width: 100%" />
        </el-form-item>
        <el-form-item label="过期时间" prop="expireTime">
          <el-date-picker
            v-model="form.expireTime"
            type="datetime"
            placeholder="选择过期时间（不选表示永不过期）"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { Plus, Edit, Delete, Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listTokens, addToken, updateToken, deleteToken, batchDeleteTokens, disableToken, enableToken, resetTokenDaily } from '@/api/stock'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const selectedIds = ref([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = ref({
  id: null,
  tokenName: '',
  tokenValue: '',
  provider: 'itick',
  dailyLimit: 0,
  expireTime: null
})

const rules = {
  tokenName: [{ required: true, message: '请输入Token名称', trigger: 'blur' }],
  tokenValue: [{ required: true, message: '请输入Token值', trigger: 'blur' }],
  provider: [{ required: true, message: '请选择服务商', trigger: 'change' }]
}

const dialogTitle = computed(() => isEdit.value ? '编辑Token' : '添加Token')

// 获取Token列表
const fetchTokens = async () => {
  loading.value = true
  try {
    const res = await listTokens()
    tableData.value = res.data || []
  } catch (error) {
    console.error('获取Token列表失败', error)
  } finally {
    loading.value = false
  }
}

// 遮蔽Token
const maskToken = (token) => {
  if (!token || token.length <= 8) {
    return token
  }
  return token.substring(0, 4) + '****' + token.substring(token.length - 4)
}

// 复制Token
const copyToken = async (token) => {
  try {
    await navigator.clipboard.writeText(token)
    ElMessage.success('复制成功')
  } catch (error) {
    ElMessage.error('复制失败')
  }
}

/**
 * 获取当前日期字符串（YYYYMMDD格式）
 */
const getDefaultTokenName = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  return `${year}${month}${day}`
}

// 添加
const handleAdd = () => {
  isEdit.value = false
  form.value = {
    id: null,
    tokenName: getDefaultTokenName(),
    tokenValue: '',
    provider: 'itick',
    dailyLimit: 0,
    expireTime: null
  }
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true

    if (isEdit.value) {
      await updateToken(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await addToken(form.value)
      ElMessage.success('添加成功')
    }

    dialogVisible.value = false
    fetchTokens()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(error.message || '操作失败')
    }
  } finally {
    submitting.value = false
  }
}

// 作废
const handleDisable = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要作废Token "${row.tokenName}" 吗？`, '提示', {
      type: 'warning'
    })
    await disableToken(row.id)
    ElMessage.success('作废成功')
    fetchTokens()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

// 启用
const handleEnable = async (row) => {
  try {
    await enableToken(row.id)
    ElMessage.success('启用成功')
    fetchTokens()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除Token "${row.tokenName}" 吗？`, '提示', {
      type: 'warning'
    })
    await deleteToken(row.id)
    ElMessage.success('删除成功')
    fetchTokens()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的Token')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个Token吗？`, '提示', {
      type: 'warning'
    })
    await batchDeleteTokens(selectedIds.value)
    ElMessage.success('批量删除成功')
    selectedIds.value = []
    fetchTokens()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '批量删除失败')
    }
  }
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 重置每日计数
const handleResetDaily = async () => {
  try {
    await ElMessageBox.confirm('确定要重置所有Token的每日计数吗？', '提示', {
      type: 'warning'
    })
    await resetTokenDaily('itick')
    ElMessage.success('重置成功')
    fetchTokens()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '重置失败')
    }
  }
}

onMounted(() => {
  fetchTokens()
})
</script>

<style scoped>
.token-container {
  padding: 20px;
}
.action-card {
  margin-bottom: 15px;
}
.table-card {
  margin-bottom: 15px;
}
</style>
