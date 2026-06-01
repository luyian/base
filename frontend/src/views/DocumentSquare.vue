<template>
  <div class="doc-square">
    <!-- 顶部 -->
    <div class="square-header">
      <h2>文档广场</h2>
      <el-input
        v-model="keyword"
        placeholder="搜索文档标题"
        clearable
        style="width: 260px"
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      >
        <template #append>
          <el-button :icon="Search" @click="handleSearch" />
        </template>
      </el-input>
    </div>

    <!-- 卡片列表 -->
    <div class="square-body" v-loading="loading">
      <el-empty v-if="!loading && list.length === 0" description="暂无文档" />
      <div class="card-grid">
        <el-card
          v-for="doc in list"
          :key="doc.id"
          class="doc-card"
          shadow="hover"
          @click="openDoc(doc)"
        >
          <div class="card-kb">
            <el-icon :size="14"><Collection /></el-icon>
            <span>{{ doc.knowledgeBaseName || '未分类' }}</span>
          </div>
          <div class="card-title">{{ doc.title }}</div>
          <div class="card-summary">{{ getSummary(doc.content) }}</div>
          <div class="card-tags" v-if="doc.tags && doc.tags.length > 0">
            <el-tag
              v-for="tag in doc.tags"
              :key="tag"
              size="small"
              :disable-transitions="true"
            >
              {{ tag }}
            </el-tag>
          </div>
          <div class="card-footer">
            <el-icon :size="13"><Clock /></el-icon>
            <span>{{ doc.updateTime }}</span>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 分页 -->
    <div class="square-pagination">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[12, 24, 48]"
        layout="total, sizes, prev, pager, next"
        @current-change="loadData"
        @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Collection, Clock } from '@element-plus/icons-vue'
import { getAllDocuments } from '@/api/knowledge'

const router = useRouter()

const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)
const keyword = ref('')
const loading = ref(false)

// 提取正文摘要（去除常见 Markdown 标记后截断）
const getSummary = (content) => {
  if (!content) {
    return '暂无内容'
  }
  const text = content
    .replace(/[#*`>\[\]!()_~]/g, '')
    .replace(/\n+/g, ' ')
    .trim()
  return text.length > 100 ? text.slice(0, 100) + '...' : text
}

const openDoc = (doc) => {
  router.push(`/document/${doc.id}`)
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAllDocuments({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined
    })
    list.value = res.data?.records || []
    total.value = Number(res.data?.total || 0)
  } catch (error) {
    console.error('获取文档列表失败:', error)
    ElMessage.error('获取文档列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadData()
}

const handleSizeChange = () => {
  pageNum.value = 1
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.doc-square {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.square-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
}

.square-header h2 {
  margin: 0;
}

.square-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.doc-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.doc-card:hover {
  transform: translateY(-4px);
}

.card-kb {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #409eff;
  margin-bottom: 6px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-summary {
  font-size: 13px;
  color: #909399;
  line-height: 1.6;
  height: 62px;
  overflow: hidden;
  margin-bottom: 10px;
}

.card-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  margin-bottom: 10px;
  min-height: 24px;
}

.card-footer {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #c0c4cc;
  border-top: 1px solid #f0f0f0;
  padding-top: 8px;
}

.square-pagination {
  display: flex;
  justify-content: flex-end;
  padding: 12px 20px;
  background: #fff;
  border-top: 1px solid #e4e7ed;
}
</style>
