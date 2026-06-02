<template>
  <div class="template-container">
    <el-card shadow="never">
      <div style="display: flex; justify-content: space-between; margin-bottom: 16px">
        <el-form :inline="true" :model="queryParams">
          <el-form-item label="平台">
            <el-select v-model="queryParams.platform" placeholder="全部" clearable style="width: 120px">
              <el-option label="飞书" value="feishu" />
              <el-option label="钉钉" value="dingtalk" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
          </el-form-item>
        </el-form>
        <el-button type="success" @click="openDialog(null)">新增模板</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column prop="templateCode" label="模板编码" width="160" />
        <el-table-column prop="templateName" label="模板名称" min-width="160" />
        <el-table-column prop="platform" label="平台" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ row.platform === 'feishu' ? '飞书' : '钉钉' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="platformApprovalCode" label="平台审批编码" width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        layout="total, prev, pager, next"
        style="margin-top: 16px; justify-content: flex-end"
        @current-change="fetchList"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑模板' : '新增模板'" width="560px">
      <el-form :model="form" label-width="110px">
        <el-form-item label="模板编码" required>
          <el-input v-model="form.templateCode" :disabled="!!editingId" />
        </el-form-item>
        <el-form-item label="模板名称" required>
          <el-input v-model="form.templateName" />
        </el-form-item>
        <el-form-item label="平台" required>
          <el-select v-model="form.platform" style="width: 100%">
            <el-option label="飞书" value="feishu" />
            <el-option label="钉钉" value="dingtalk" />
          </el-select>
        </el-form-item>
        <el-form-item label="平台审批编码">
          <el-input v-model="form.platformApprovalCode" placeholder="飞书 approval_code" />
        </el-form-item>
        <el-form-item label="表单映射">
          <el-input v-model="form.formMapping" type="textarea" :rows="5" placeholder="JSON 格式" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="form.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listTemplates, saveTemplate, updateTemplate, deleteTemplate } from '@/api/approvalTemplate'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const saving = ref(false)
const editingId = ref(null)

const queryParams = reactive({ pageNum: 1, pageSize: 10, platform: '' })

const form = reactive({
  templateCode: '',
  templateName: '',
  platform: 'feishu',
  platformApprovalCode: '',
  formMapping: '',
  description: '',
  status: 1
})

async function fetchList() {
  loading.value = true
  try {
    const res = await listTemplates(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleQuery() {
  queryParams.pageNum = 1
  fetchList()
}

function openDialog(row) {
  if (row) {
    editingId.value = row.id
    Object.assign(form, {
      templateCode: row.templateCode,
      templateName: row.templateName,
      platform: row.platform,
      platformApprovalCode: row.platformApprovalCode || '',
      formMapping: row.formMapping || '',
      description: row.description || '',
      status: row.status
    })
  } else {
    editingId.value = null
    Object.assign(form, {
      templateCode: '', templateName: '', platform: 'feishu',
      platformApprovalCode: '', formMapping: '', description: '', status: 1
    })
  }
  dialogVisible.value = true
}

async function handleSave() {
  if (!form.templateCode || !form.templateName) {
    ElMessage.warning('请填写必填项')
    return
  }
  saving.value = true
  try {
    if (editingId.value) {
      await updateTemplate(editingId.value, form)
    } else {
      await saveTemplate(form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchList()
  } finally {
    saving.value = false
  }
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除该模板？', '提示', { type: 'warning' })
  await deleteTemplate(id)
  ElMessage.success('删除成功')
  fetchList()
}

onMounted(() => fetchList())
</script>

<style scoped>
.template-container {
  padding: 16px;
}
</style>
