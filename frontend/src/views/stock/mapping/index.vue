<template>
  <div class="mapping-container">
    <!-- 操作栏 -->
    <el-card class="action-card" shadow="never">
      <el-button type="primary" :icon="Plus" @click="handleAdd">添加映射配置</el-button>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card" shadow="never">
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="mappingCode" label="映射编码" width="150" align="center" />
        <el-table-column prop="mappingName" label="映射名称" width="200" align="center" />
        <el-table-column prop="sourceType" label="源数据类型" width="180" align="center" />
        <el-table-column prop="targetTable" label="目标表" width="150" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success" size="small">启用</el-tag>
            <el-tag v-else type="danger" size="small">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column prop="updateTime" label="更新时间" width="180" align="center" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listMappings, deleteMapping } from '@/api/stock'
import { useRouter } from 'vue-router'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])

// 获取映射配置列表
const fetchMappings = async () => {
  loading.value = true
  try {
    const res = await listMappings()
    tableData.value = res.data || []
  } catch (error) {
    console.error('获取映射配置列表失败', error)
  } finally {
    loading.value = false
  }
}

// 添加
const handleAdd = () => {
  router.push('/stock/mapping/edit')
}

// 编辑
const handleEdit = (row) => {
  router.push(`/stock/mapping/edit/${row.id}`)
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除映射配置 "${row.mappingName}" 吗？`, '提示', {
      type: 'warning'
    })
    await deleteMapping(row.id)
    ElMessage.success('删除成功')
    fetchMappings()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

onMounted(() => {
  fetchMappings()
})
</script>

<style scoped>
.mapping-container {
  padding: 20px;
}
.action-card {
  margin-bottom: 15px;
}
.table-card {
  margin-bottom: 15px;
}
</style>
