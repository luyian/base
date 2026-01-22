<template>
  <div class="notice-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="通知标题">
          <el-input
            v-model="queryForm.title"
            placeholder="请输入通知标题"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="通知类型">
          <el-select v-model="queryForm.type" placeholder="请选择通知类型" clearable style="width: 150px">
            <el-option label="通知" :value="1" />
            <el-option label="公告" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="通知级别">
          <el-select v-model="queryForm.level" placeholder="请选择通知级别" clearable style="width: 150px">
            <el-option label="普通" :value="1" />
            <el-option label="重要" :value="2" />
            <el-option label="紧急" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏和表格 -->
    <el-card class="table-card">
      <div class="toolbar">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增通知</el-button>
        <el-button
          type="danger"
          :icon="Delete"
          :disabled="selectedIds.length === 0"
          @click="handleBatchDelete"
        >
          批量删除
        </el-button>
      </div>

      <el-table
        :data="tableData"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        v-loading="loading"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="通知ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)">
              {{ getTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="级别" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelTagType(row.level)">
              {{ getLevelText(row.level) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createBy" label="发布人" width="120" />
        <el-table-column prop="publishTime" label="发布时间" width="160" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="View" @click="handleView(row)">查看详情</el-button>
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button
          v-if="row.status === 0"
              link
              type="success"
              :icon="Promotion"
              @click="handlePublish(row)"
            >
              发布
            </el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryForm.current"
        v-model:page-size="queryForm.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
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
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="通知标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="请输入通知标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="通知类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :value="1">通知</el-radio>
            <el-radio :value="2">公告</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="通知级别" prop="level">
          <el-radio-group v-model="form.level">
            <el-radio :value="1">普通</el-radio>
            <el-radio :value="2">重要</el-radio>
            <el-radio :value="3">紧急</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">草稿</el-radio>
            <el-radio :value="1">已发布</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="通知内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="10"
            placeholder="请输入通知内容"
            maxlength="5000"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="通知详情"
      width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="通知ID">
          {{ viewData.id }}
        </el-descriptions-item>
        <el-descriptions-item label="通知标题">
          {{ viewData.title }}
        </el-descriptions-item>
        <el-descriptions-item label="通知类型">
          <el-tag :type="getTypeTagType(viewData.type)">
            {{ getTypeText(viewData.type) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="通知级别">
          <el-tag :type="getLevelTagType(viewData.level)">
            {{ getLevelText(viewData.level) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(viewData.status)">
            {{ getStatusText(viewData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布人">
          {{ viewData.createBy || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="发布时间">
          {{ viewData.publishTime || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ viewData.createTime }}
        </el-descriptions-item>
        <el-descriptions-item label="通知内容" :span="2">
          <div class="notice-content">{{ viewData.content }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          {{ viewData.remark || '-' }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Edit, View, Promotion } from '@element-plus/icons-vue'
import {
  pageNotices,
  getNoticeById,
  addNotice,
  updateNotice,
  deleteNotice,
  batchDeleteNotices,
  publishNotice
} from '@/api/notice'

// 查询表单
const queryForm = reactive({
  title: '',
  type: null,
  level: null,
  status: null,
  current: 1,
  size: 10
})

// 表格数据
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const selectedIds = ref([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const submitLoading = ref(false)

// 表单数据
const form = reactive({
  id: null,
  title: '',
  content: '',
  type: 1,
  level: 1,
  status: 0,
  remark: ''
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入通知标题', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择通知类型', trigger: 'change' }
  ],
  level: [
    { required: true, message: '请选择通知级别', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入通知内容', trigger: 'blur' },
    { min: 10, max: 5000, message: '长度在 10 到 5000 个字符', trigger: 'blur' }
  ]
}

// 查看详情
const viewDialogVisible = ref(false)
const viewData = ref({})

// 获取类型标签类型
const getTypeTagType = (type) => {
  const typeMap = {
    1: 'primary', // 通知 - 蓝色
    2: 'success'  // 公告 - 绿色
  }
  return typeMap[type] || ''
}

// 获取类型文本
const getTypeText = (type) => {
  const typeMap = {
    1: '通知',
    2: '公告'
  }
  return typeMap[type] || '-'
}

// 获取级别标签类型
const getLevelTagType = (level) => {
  const levelMap = {
    1: '',        // 普通 - 默认
    2: 'warning', // 重要 - 橙色
    3: 'danger'   // 紧急 - 红色
  }
  return levelMap[level] || ''
}

// 获取级别文本
const getLevelText = (level) => {
  const levelMap = {
    1: '普通',
    2: '重要',
    3: '紧急'
  }
  return levelMap[level] || '-'
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const statusMap = {
    0: 'info',    // 草稿 - 灰色
    1: 'success'  // 已发布 - 绿色
  }
  return statusMap[status] || ''
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '草稿',
    1: '已发布'
  }
  return statusMap[status] || '-'
}

// 查询
const handleQuery = async () => {
  loading.value = true
  try {
    const res = await pageNotices(queryForm)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

// 重置
const handleReset = () => {
  queryForm.title = ''
  queryForm.type = null
  queryForm.level = null
  queryForm.status = null
  queryForm.current = 1
  queryForm.size = 10
  handleQuery()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增通知'
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row) => {
  dialogTitle.value = '编辑通知'
  try {
    const res = await getNoticeById(row.id)
    Object.assign(form, res.data)
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取通知信息失败')
  }
}

// 查看详情
const handleView = async (row) => {
  try {
    const res = await getNoticeById(row.id)
    viewData.value = res.data
    viewDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取通知详情失败')
  }
}

// 发布
const handlePublish = (row) => {
  ElMessageBox.confirm('确定要发布该通知吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async () => {
    try {
      await publishNotice(row.id)
      ElMessage.success('发布成功')
      handleQuery()
    } catch (error) {
      ElMessage.error('发布失败')
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该通知吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteNotice(row.id)
      ElMessage.success('删除成功')
      handleQuery()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 批量删除
const handleBatchDelete = () => {
  ElMessageBox.confirm('确定要删除选中的通知吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await batchDeleteNotices(selectedIds.value)
      ElMessage.success('删除成功')
      selectedIds.value = []
      handleQuery()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (form.id) {
          await updateNotice(form)
          ElMessage.success('编辑成功')
        } else {
          await addNotice(form)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        handleQuery()
      } catch (error) {
        ElMessage.error(form.id ? '编辑失败' : '新增失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
  form.id = null
  form.title = ''
  form.content = ''
  form.type = 1
  form.level = 1
  form.status = 0
  form.remark = ''
}

// 表格选择
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 初始化
onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.notice-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 0;
}

.table-card {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

.notice-content {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.6;
  max-height: 400px;
  overflow-y: auto;
}
</style>
