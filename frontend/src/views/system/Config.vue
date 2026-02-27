<template>
  <div class="config-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :model="queryForm" inline>
        <el-form-item label="配置键">
          <el-input v-model="queryForm.configKey" placeholder="请输入配置键" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="配置名称">
          <el-input v-model="queryForm.configName" placeholder="请输入配置名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="配置类型">
          <el-select v-model="queryForm.configType" placeholder="请选择配置类型" clearable style="width: 200px">
            <el-option label="系统内置" :value="1" />
            <el-option label="用户自定义" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 200px">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <el-card class="table-card">
      <div class="toolbar">
        <el-button v-permission="'system:config:add'" type="primary" @click="handleAdd">新增配置</el-button>
        <el-button v-permission="'system:config:delete'" type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">批量删除</el-button>
        <el-button type="success" @click="handleRefreshCache">刷新缓存</el-button>
      </div>

      <!-- 表格 -->
      <el-table
        :data="tableData"
        border
        stripe
        @selection-change="handleSelectionChange"
        style="margin-top: 20px"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="配置ID" width="80" />
        <el-table-column prop="configKey" label="配置键" min-width="150" show-overflow-tooltip />
        <el-table-column prop="configValue" label="配置值" min-width="150" show-overflow-tooltip />
        <el-table-column prop="configName" label="配置名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="configType" label="配置类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.configType === 1" type="warning">系统内置</el-tag>
            <el-tag v-else type="success">用户自定义</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">正常</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button v-permission="'system:config:edit'" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button v-permission="'system:config:delete'" type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryForm.current"
        v-model:page-size="queryForm.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="form.id ? '编辑配置' : '新增配置'"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="配置键" prop="configKey">
          <el-input v-model="form.configKey" placeholder="请输入配置键" />
        </el-form-item>
        <el-form-item label="配置值" prop="configValue">
          <el-input v-model="form.configValue" type="textarea" :rows="3" placeholder="请输入配置值" />
        </el-form-item>
        <el-form-item label="配置名称" prop="configName">
          <el-input v-model="form.configName" placeholder="请输入配置名称" />
        </el-form-item>
        <el-form-item label="配置类型" prop="configType">
          <el-select v-model="form.configType" placeholder="请选择配置类型" style="width: 100%">
            <el-option label="系统内置" :value="1" />
            <el-option label="用户自定义" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  pageConfigs,
  getConfigById,
  addConfig,
  updateConfig,
  deleteConfig,
  batchDeleteConfigs,
  refreshCache
} from '@/api/config'

// 查询表单
const queryForm = reactive({
  configKey: '',
  configName: '',
  configType: null,
  status: null,
  current: 1,
  size: 10
})

// 表格数据
const tableData = ref([])
const total = ref(0)
const selectedIds = ref([])

// 对话框
const dialogVisible = ref(false)
const formRef = ref(null)
const form = reactive({
  id: null,
  configKey: '',
  configValue: '',
  configName: '',
  configType: 2,
  status: 1,
  remark: ''
})

// 表单验证规则
const rules = {
  configKey: [
    { required: true, message: '请输入配置键', trigger: 'blur' },
    { min: 2, max: 50, message: '配置键长度必须在2-50个字符之间', trigger: 'blur' }
  ],
  configValue: [
    { required: true, message: '请输入配置值', trigger: 'blur' },
    { max: 500, message: '配置值长度不能超过500个字符', trigger: 'blur' }
  ],
  configName: [
    { required: true, message: '请输入配置名称', trigger: 'blur' },
    { min: 2, max: 50, message: '配置名称长度必须在2-50个字符之间', trigger: 'blur' }
  ],
  configType: [
    { required: true, message: '请选择配置类型', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  remark: [
    { max: 200, message: '备注长度不能超过200个字符', trigger: 'blur' }
  ]
}

// 查询配置列表
const handleQuery = async () => {
  try {
    const res = await pageConfigs(queryForm)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

// 重置查询表单
const handleReset = () => {
  queryForm.configKey = ''
  queryForm.configName = ''
  queryForm.configType = null
  queryForm.status = null
  queryForm.current = 1
  queryForm.size = 10
  handleQuery()
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 新增配置
const handleAdd = () => {
  dialogVisible.value = true
}

// 编辑配置
const handleEdit = async (row) => {
  try {
    const res = await getConfigById(row.id)
    Object.assign(form, res.data)
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取配置详情失败')
  }
}

// 删除配置
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该配置吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteConfig(id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除配置
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除选中的配置吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await batchDeleteConfigs(selectedIds.value)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 刷新缓存
const handleRefreshCache = async () => {
  try {
    await refreshCache()
    ElMessage.success('缓存刷新成功')
  } catch (error) {
    ElMessage.error('缓存刷新失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (form.id) {
      await updateConfig(form)
      ElMessage.success('编辑成功')
    } else {
      await addConfig(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    handleQuery()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(form.id ? '编辑失败' : '新增失败')
    }
  }
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
  form.id = null
  form.configKey = ''
  form.configValue = ''
  form.configName = ''
  form.configType = 2
  form.status = 1
  form.remark = ''
}

// 初始化
handleQuery()
</script>

<style scoped>
.config-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.toolbar {
  display: flex;
  gap: 10px;
}
</style>
