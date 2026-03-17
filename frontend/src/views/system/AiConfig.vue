<template>
  <div class="ai-config-container">
    <el-card>
      <template #header>
        <span>大模型配置</span>
        <span class="card-tip">支持多条配置，选择一条生效后首页 AI 助手将使用该配置</span>
      </template>

      <div class="toolbar">
        <el-form inline>
          <el-form-item label="配置名称">
            <el-input v-model="queryForm.configName" placeholder="配置名称" clearable style="width: 160px" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 100px">
              <el-option label="启用" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadPage">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
        <el-button v-permission="'system:ai-config:add'" type="primary" @click="handleAdd">新增配置</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="configName" label="配置名称" min-width="120" />
        <el-table-column prop="baseUrl" label="API 地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="apiKey" label="API Key" width="120" show-overflow-tooltip />
        <el-table-column prop="model" label="模型" width="110" />
        <el-table-column prop="timeout" label="超时(ms)" width="90" />
        <el-table-column prop="retry" label="重试" width="70" />
        <el-table-column prop="isActive" label="生效" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isActive === 1" type="success">生效</el-tag>
            <el-tag v-else type="info">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button v-permission="'system:ai-config:edit'" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button
              v-permission="'system:ai-config:edit'"
              v-if="row.isActive !== 1 && row.status === 1"
              type="success"
              link
              @click="handleSetActive(row)"
            >
              设为生效
            </el-button>
            <el-button v-permission="'system:ai-config:delete'" type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryForm.current"
        v-model:page-size="queryForm.size"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        class="pagination"
        @size-change="loadPage"
        @current-change="loadPage"
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="form.id ? '编辑配置' : '新增配置'"
      width="560px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="140px">
        <el-form-item label="配置名称" prop="configName">
          <el-input v-model="form.configName" placeholder="如：通义千问、DeepSeek" />
        </el-form-item>
        <el-form-item label="API 基础地址" prop="baseUrl">
          <el-input v-model="form.baseUrl" placeholder="如 https://dashscope.aliyuncs.com/compatible-mode/v1" />
        </el-form-item>
        <el-form-item label="API Key" prop="apiKey">
          <el-input v-model="form.apiKey" type="password" placeholder="请输入 API Key" show-password />
        </el-form-item>
        <el-form-item label="模型名称" prop="model">
          <el-input v-model="form.model" placeholder="如 qwen-plus" />
        </el-form-item>
        <el-form-item label="超时时间(ms)" prop="timeout">
          <el-input-number v-model="form.timeout" :min="5000" :max="120000" :step="5000" />
        </el-form-item>
        <el-form-item label="重试次数" prop="retry">
          <el-input-number v-model="form.retry" :min="0" :max="5" />
        </el-form-item>
        <el-form-item label="消息最大长度" prop="maxMessageLength">
          <el-input-number v-model="form.maxMessageLength" :min="100" :max="10000" :step="100" />
        </el-form-item>
        <el-form-item label="上下文最大长度" prop="maxContextLength">
          <el-input-number v-model="form.maxContextLength" :min="500" :max="20000" :step="500" />
        </el-form-item>
        <el-form-item v-if="form.id" label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  pageAiConfig,
  getAiConfigById,
  addAiConfig,
  updateAiConfig,
  deleteAiConfig,
  setActiveAiConfig
} from '@/api/ai'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)
const tableData = ref([])
const total = ref(0)

const queryForm = reactive({
  configName: '',
  status: null,
  current: 1,
  size: 10
})

const form = reactive({
  id: null,
  configName: '',
  baseUrl: '',
  apiKey: '',
  model: 'qwen-plus',
  timeout: 30000,
  retry: 2,
  maxMessageLength: 2000,
  maxContextLength: 5000,
  status: 1,
  remark: ''
})

const rules = {
  configName: [{ required: true, message: '请输入配置名称', trigger: 'blur' }],
  baseUrl: [{ required: true, message: '请输入 API 基础地址', trigger: 'blur' }],
  apiKey: [{ required: true, message: '请输入 API Key', trigger: 'blur' }]
}

async function loadPage() {
  loading.value = true
  try {
    const res = await pageAiConfig({
      current: queryForm.current,
      size: queryForm.size,
      configName: queryForm.configName || undefined,
      status: queryForm.status
    })
    tableData.value = res.data?.records ?? []
    total.value = res.data?.total ?? 0
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  queryForm.configName = ''
  queryForm.status = null
  queryForm.current = 1
  queryForm.size = 10
  loadPage()
}

function resetForm() {
  form.id = null
  form.configName = ''
  form.baseUrl = ''
  form.apiKey = ''
  form.model = 'qwen-plus'
  form.timeout = 30000
  form.retry = 2
  form.maxMessageLength = 2000
  form.maxContextLength = 5000
  form.status = 1
  form.remark = ''
}

function handleAdd() {
  resetForm()
  dialogVisible.value = true
}

async function handleEdit(row) {
  try {
    const res = await getAiConfigById(row.id)
    Object.assign(form, res.data)
    dialogVisible.value = true
  } catch (e) {
    ElMessage.error('获取详情失败')
  }
}

async function handleSubmit() {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }
  saving.value = true
  try {
    if (form.id) {
      await updateAiConfig(form)
      ElMessage.success('保存成功')
    } else {
      await addAiConfig(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadPage()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '操作失败')
  } finally {
    saving.value = false
  }
}

async function handleSetActive(row) {
  try {
    await setActiveAiConfig(row.id)
    ElMessage.success('已设为生效配置')
    loadPage()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '操作失败')
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除配置「${row.configName}」吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAiConfig(row.id)
    ElMessage.success('删除成功')
    loadPage()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e?.response?.data?.message || '删除失败')
  }
}

onMounted(() => {
  loadPage()
})
</script>

<style scoped>
.ai-config-container {
  padding: 20px;
}

.card-tip {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-left: 12px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
