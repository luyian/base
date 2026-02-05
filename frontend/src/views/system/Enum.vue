<template>
  <div class="enum-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="枚举类型">
          <el-input v-model="queryForm.enumType" placeholder="请输入枚举类型" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <el-card class="table-card">
      <div class="toolbar">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增枚举类型</el-button>
        <el-button type="success" :icon="Refresh" @click="handleRefreshCache">刷新缓存</el-button>
      </div>

      <!-- 枚举类型列表表格 -->
      <el-table
        :data="filteredTableData"
        style="width: 100%; margin-top: 20px"
        v-loading="loading"
      >
        <el-table-column prop="enumType" label="枚举类型" min-width="100" />
        <el-table-column prop="typeDesc" label="描述" min-width="100" />
        <el-table-column prop="itemCount" label="枚举项数量" min-width="100" align="center" />
        <el-table-column prop="createTime" label="创建时间" min-width="180" />
        <el-table-column label="操作" min-width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="枚举类型" prop="enumType">
          <el-input
            v-model="form.enumType"
            placeholder="请输入枚举类型"
            :disabled="isEdit"
            style="width: 300px"
          />
        </el-form-item>
        <el-form-item label="中文描述" prop="typeDesc">
          <el-input
            v-model="form.typeDesc"
            placeholder="请输入中文描述"
            style="width: 300px"
          />
        </el-form-item>

        <!-- 枚举项列表 -->
        <el-form-item label="枚举项">
          <div class="enum-items-container">
            <el-button type="primary" size="small" :icon="Plus" @click="handleAddItem" style="margin-bottom: 10px">
              添加枚举项
            </el-button>
            <el-table :data="form.items" border size="small">
              <el-table-column prop="enumCode" label="编码" width="150">
                <template #default="{ row }">
                  <el-input v-model="row.enumCode" placeholder="编码" size="small" />
                </template>
              </el-table-column>
              <el-table-column prop="enumValue" label="显示值" min-width="150">
                <template #default="{ row }">
                  <el-input v-model="row.enumValue" placeholder="显示值" size="small" />
                </template>
              </el-table-column>
              <el-table-column prop="sort" label="排序" width="100">
                <template #default="{ row }">
                  <el-input-number v-model="row.sort" :min="0" :max="9999" size="small" controls-position="right" style="width: 80px" />
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-switch v-model="row.status" :active-value="1" :inactive-value="0" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80" align="center">
                <template #default="{ $index }">
                  <el-button link type="danger" :icon="Delete" @click="handleRemoveItem($index)" />
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import {
  listEnumTypes,
  listByType,
  batchSaveByType,
  deleteByType,
  refreshCache
} from '@/api/enum'

// 查询表单
const queryForm = reactive({
  enumType: ''
})

// 表格数据
const tableData = ref([])
const loading = ref(false)
const submitLoading = ref(false)

// 过滤后的表格数据
const filteredTableData = computed(() => {
  if (!queryForm.enumType) {
    return tableData.value
  }
  return tableData.value.filter(item =>
    item.enumType.toLowerCase().includes(queryForm.enumType.toLowerCase())
  )
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref(null)
const form = reactive({
  enumType: '',
  typeDesc: '',
  items: []
})

// 表单验证规则
const formRules = {
  enumType: [
    { required: true, message: '请输入枚举类型', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z_][a-zA-Z0-9_]*$/, message: '只能包含字母、数字和下划线，且以字母或下划线开头', trigger: 'blur' }
  ]
}

// 查询枚举类型列表
const handleQuery = async () => {
  loading.value = true
  try {
    const { data } = await listEnumTypes()
    tableData.value = data
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

// 重置查询
const handleReset = () => {
  queryForm.enumType = ''
}

// 新增枚举类型
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增枚举类型'
  form.enumType = ''
  form.typeDesc = ''
  form.items = [{ enumCode: '', enumValue: '', sort: 0, status: 1 }]
  dialogVisible.value = true
}

// 编辑枚举类型
const handleEdit = async (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑枚举类型'
  form.enumType = row.enumType
  form.typeDesc = row.typeDesc || ''

  try {
    const { data } = await listByType(row.enumType)
    form.items = data.map(item => ({
      id: item.id,
      enumCode: item.enumCode,
      enumValue: item.enumValue,
      sort: item.sort,
      status: item.status
    }))
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取枚举项列表失败')
  }
}

// 删除枚举类型
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除枚举类型 "${row.enumType}" 及其所有枚举项吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await deleteByType(row.enumType)
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
    ElMessage.success('刷新缓存成功')
  } catch (error) {
    ElMessage.error('刷新缓存失败')
  }
}

// 添加枚举项
const handleAddItem = () => {
  form.items.push({
    enumCode: '',
    enumValue: '',
    sort: form.items.length,
    status: 1
  })
}

// 移除枚举项
const handleRemoveItem = (index) => {
  form.items.splice(index, 1)
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    // 验证枚举项
    if (form.items.length === 0) {
      ElMessage.warning('请至少添加一个枚举项')
      return
    }

    for (let i = 0; i < form.items.length; i++) {
      const item = form.items[i]
      if (!item.enumCode || !item.enumCode.trim()) {
        ElMessage.warning(`第 ${i + 1} 行的编码不能为空`)
        return
      }
      if (!item.enumValue || !item.enumValue.trim()) {
        ElMessage.warning(`第 ${i + 1} 行的显示值不能为空`)
        return
      }
    }

    // 检查编码是否重复
    const codes = form.items.map(item => item.enumCode)
    const uniqueCodes = new Set(codes)
    if (codes.length !== uniqueCodes.size) {
      ElMessage.warning('枚举编码不能重复')
      return
    }

    submitLoading.value = true
    await batchSaveByType(form.enumType, form.typeDesc, form.items)
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    handleQuery()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(isEdit.value ? '编辑失败' : '新增失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
  form.enumType = ''
  form.typeDesc = ''
  form.items = []
}

// 页面加载时查询数据
onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.enum-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 0;
}

.toolbar {
  margin-bottom: 20px;
}

.enum-items-container {
  width: 100%;
}
</style>
