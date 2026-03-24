<template>
  <div class="knowledge-base-detail">
    <!-- 顶部导航 -->
    <div class="detail-header">
      <div class="header-left">
        <el-button :icon="ArrowLeft" circle @click="goBack" />
        <h2>{{ knowledgeBase?.name || '知识库详情' }}</h2>
      </div>
      <div class="header-actions">
        <el-button type="primary" :icon="FolderAdd" @click="handleCreateDirectory">
          新建目录
        </el-button>
        <el-button type="primary" :icon="DocumentAdd" @click="handleCreateDocument">
          新建文档
        </el-button>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="detail-content">
      <!-- 左侧：目录树 -->
      <div class="sidebar-left">
        <div class="sidebar-header">
          <span>目录结构</span>
        </div>
        <el-tree
          ref="treeRef"
          :data="directoryTree"
          :props="treeProps"
          node-key="id"
          :expand-on-click-node="false"
          :default-expand-all="true"
          @node-click="handleDirectoryClick"
        >
          <template #default="{ node, data }">
            <span class="custom-tree-node">
              <span class="node-label">{{ node.label }}</span>
              <span class="node-actions" @click.stop>
                <el-dropdown trigger="click">
                  <el-icon :size="14"><MoreFilled /></el-icon>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item :icon="FolderAdd" @click="handleCreateSubDirectory(data)">
                        新建子目录
                      </el-dropdown-item>
                      <el-dropdown-item :icon="Edit" @click="handleEditDirectory(data)">
                        编辑目录
                      </el-dropdown-item>
                      <el-dropdown-item :icon="Delete" divided @click="handleDeleteDirectory(data)">
                        删除目录
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </span>
            </span>
          </template>
        </el-tree>
      </div>

      <!-- 中间：文档列表 -->
      <div class="sidebar-middle">
        <div class="sidebar-header">
          <span>文档列表</span>
        </div>
        
        <!-- 标签筛选 -->
        <div class="tag-filter" v-if="tagList.length > 0">
          <el-select
            v-model="selectedTagId"
            placeholder="按标签筛选"
            clearable
            size="small"
            style="width: 100%"
            @change="handleTagFilterChange"
          >
            <el-option
              v-for="tag in tagList"
              :key="tag.id"
              :label="tag.name"
              :value="tag.id"
            >
              <el-tag :color="tag.color" size="small">{{ tag.name }}</el-tag>
            </el-option>
          </el-select>
        </div>

        <div class="document-list" v-loading="docListLoading">
          <el-empty v-if="!docListLoading && documentList.length === 0" description="暂无文档" :image-size="80" />
          
          <div
            v-for="doc in documentList"
            :key="doc.id"
            class="document-item"
            :class="{ active: currentDocument?.id === doc.id }"
            @click="handleDocumentClick(doc)"
          >
            <div class="doc-info">
              <el-icon class="doc-icon"><Document /></el-icon>
              <span class="doc-title">{{ doc.title }}</span>
            </div>
            <div class="doc-tags" v-if="doc.tags && doc.tags.length > 0">
              <el-tag
                v-for="tag in doc.tags.slice(0, 2)"
                :key="tag"
                :color="getTagColor(tag)"
                size="small"
                :disable-transitions="true"
              >
                {{ tag }}
              </el-tag>
              <span v-if="doc.tags.length > 2" class="more-tags">+{{ doc.tags.length - 2 }}</span>
            </div>
            <div class="doc-actions" @click.stop>
              <el-dropdown trigger="click">
                <el-icon :size="14"><MoreFilled /></el-icon>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item :icon="Edit" @click="handleEditDocument(doc)">
                      编辑
                    </el-dropdown-item>
                    <el-dropdown-item :icon="Delete" divided @click="handleDeleteDocument(doc)">
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：文档编辑器 -->
      <div class="editor-panel">
        <div v-if="!currentDocument" class="editor-empty">
          <el-empty description="请选择一个文档" />
        </div>
        
        <div v-else class="editor-content">
          <div class="editor-header">
            <el-input
              v-model="editingTitle"
              class="title-input"
              placeholder="文档标题"
              @blur="handleTitleBlur"
            />
            <el-button
              type="primary"
              :icon="Check"
              @click="handleSaveDocument"
              :loading="saving"
            >
              保存
            </el-button>
          </div>
          
          <!-- 文档标签 -->
          <div class="doc-tags-edit">
            <el-tag
              v-for="tag in currentDocument.tags"
              :key="tag"
              closable
              :color="getTagColor(tag)"
              @close="handleRemoveTag(tag)"
            >
              {{ tag }}
            </el-tag>
            <el-dropdown trigger="click" @command="handleAddTag">
              <el-button size="small" :icon="Plus">添加标签</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item
                    v-for="tag in availableTags"
                    :key="tag.id"
                    :command="tag"
                  >
                    <el-tag :color="tag.color" size="small">{{ tag.name }}</el-tag>
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleCreateTag">
                    <el-icon><Plus /></el-icon> 新建标签
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>

          <!-- 编辑器/预览切换 -->
          <div class="editor-tabs">
            <el-radio-group v-model="editorMode" size="small">
              <el-radio-button value="edit">编辑</el-radio-button>
              <el-radio-button value="preview">预览</el-radio-button>
            </el-radio-group>
          </div>

          <!-- 编辑区 -->
          <div class="editor-main" v-show="editorMode === 'edit'">
            <textarea
              v-model="editingContent"
              class="md-editor"
              placeholder="使用 Markdown 编写文档内容..."
            />
          </div>

          <!-- 预览区 -->
          <div class="editor-preview" v-show="editorMode === 'preview'" v-html="renderedContent">
          </div>
        </div>
      </div>
    </div>

    <!-- 创建/编辑目录对话框 -->
    <el-dialog
      v-model="directoryDialogVisible"
      :title="isEditDirectory ? '编辑目录' : '创建目录'"
      width="400px"
    >
      <el-form ref="dirFormRef" :model="directoryForm" :rules="directoryRules" label-width="60px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="directoryForm.name" placeholder="请输入目录名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="directoryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitDirectory" :loading="directoryLoading">
          确认
        </el-button>
      </template>
    </el-dialog>

    <!-- 创建/编辑文档对话框 -->
    <el-dialog
      v-model="documentDialogVisible"
      :title="isEditDocument ? '编辑文档' : '创建文档'"
      width="500px"
    >
      <el-form ref="docFormRef" :model="documentForm" :rules="documentRules" label-width="60px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="documentForm.title" placeholder="请输入文档标题" />
        </el-form-item>
        <el-form-item label="目录" prop="directoryId">
          <el-tree-select
            v-model="documentForm.directoryId"
            :data="directoryTreeSelect"
            :props="treeSelectProps"
            check-strictly
            placeholder="请选择所属目录"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="documentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitDocument" :loading="documentLoading">
          确认
        </el-button>
      </template>
    </el-dialog>

    <!-- 创建标签对话框 -->
    <el-dialog v-model="tagDialogVisible" title="创建标签" width="400px">
      <el-form ref="tagFormRef" :model="tagForm" :rules="tagRules" label-width="60px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="tagForm.name" placeholder="请输入标签名称" />
        </el-form-item>
        <el-form-item label="颜色" prop="color">
          <el-color-picker v-model="tagForm.color" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="tagDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitTag" :loading="tagLoading">
          确认
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  Document,
  FolderAdd,
  DocumentAdd,
  Edit,
  Delete,
  Plus,
  Check,
  MoreFilled
} from '@element-plus/icons-vue'
import {
  getKnowledgeBaseDetail,
  getDirectoryTree,
  createDirectory,
  updateDirectory,
  deleteDirectory,
  getDocumentList,
  getDocumentDetail,
  createDocument,
  updateDocument,
  deleteDocument,
  getTagList,
  createTag
} from '@/api/knowledge'

const route = useRoute()
const router = useRouter()

// 知识库ID
const knowledgeBaseId = computed(() => Number(route.params.id))

// 数据
const knowledgeBase = ref(null)
const directoryTree = ref([])
const documentList = ref([])
const tagList = ref([])
const currentDocument = ref(null)
const docListLoading = ref(false)
const saving = ref(false)

// 编辑状态
const editingTitle = ref('')
const editingContent = ref('')
const editorMode = ref('edit')

// 目录树配置
const treeProps = {
  children: 'children',
  label: 'name'
}

// 目录表单
const directoryDialogVisible = ref(false)
const isEditDirectory = ref(false)
const directoryLoading = ref(false)
const dirFormRef = ref(null)
const directoryForm = reactive({
  id: null,
  parentId: null,
  name: ''
})
const directoryRules = {
  name: [{ required: true, message: '请输入目录名称', trigger: 'blur' }]
}

// 文档表单
const documentDialogVisible = ref(false)
const isEditDocument = ref(false)
const documentLoading = ref(false)
const docFormRef = ref(null)
const documentForm = reactive({
  id: null,
  title: '',
  directoryId: null
})
const documentRules = {
  title: [{ required: true, message: '请输入文档标题', trigger: 'blur' }]
}

// 目录树选择器数据
const directoryTreeSelect = computed(() => {
  const convert = (list) => {
    return list.map(item => ({
      value: item.id,
      label: item.name,
      children: item.children ? convert(item.children) : []
    }))
  }
  return [{ value: null, label: '根目录', children: convert(directoryTree.value) }]
})
const treeSelectProps = {
  value: 'value',
  label: 'label',
  children: 'children'
}

// 标签筛选
const selectedTagId = ref(null)

// 标签表单
const tagDialogVisible = ref(false)
const tagLoading = ref(false)
const tagFormRef = ref(null)
const tagForm = reactive({
  name: '',
  color: '#409eff'
})
const tagRules = {
  name: [{ required: true, message: '请输入标签名称', trigger: 'blur' }]
}

// 可用标签（未添加到此文档的标签）
const availableTags = computed(() => {
  if (!currentDocument.value) return tagList.value
  const docTags = currentDocument.value.tags || []
  return tagList.value.filter(t => !docTags.includes(t.name))
})

// 渲染 Markdown（简单实现）
const renderedContent = computed(() => {
  if (!editingContent.value) return ''
  let html = editingContent.value
  // 标题
  html = html.replace(/^### (.*$)/gim, '<h3>$1</h3>')
  html = html.replace(/^## (.*$)/gim, '<h2>$1</h2>')
  html = html.replace(/^# (.*$)/gim, '<h1>$1</h1>')
  // 粗体
  html = html.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
  // 斜体
  html = html.replace(/\*(.*?)\*/g, '<em>$1</em>')
  // 代码块
  html = html.replace(/```([\s\S]*?)```/g, '<pre><code>$1</code></pre>')
  // 行内代码
  html = html.replace(/`(.*?)`/g, '<code>$1</code>')
  // 链接
  html = html.replace(/\[(.*?)\]\((.*?)\)/g, '<a href="$2" target="_blank">$1</a>')
  // 图片
  html = html.replace(/!\[(.*?)\]\((.*?)\)/g, '<img src="$2" alt="$1" style="max-width:100%">')
  // 列表
  html = html.replace(/^\s*[-*]\s+(.*$)/gim, '<li>$1</li>')
  html = html.replace(/(<li>.*<\/li>)/s, '<ul>$1</ul>')
  // 换行
  html = html.replace(/\n/g, '<br>')
  return html
})

// 获取标签颜色
const getTagColor = (tagName) => {
  const tag = tagList.value.find(t => t.name === tagName)
  return tag?.color || '#909399'
}

// 返回
const goBack = () => {
  router.push('/knowledge-base')
}

// 加载知识库详情
const loadKnowledgeBase = async () => {
  try {
    const res = await getKnowledgeBaseDetail(knowledgeBaseId.value)
    knowledgeBase.value = res.data
  } catch (error) {
    console.error('获取知识库详情失败:', error)
    ElMessage.error('获取知识库详情失败')
  }
}

// 加载目录树
const loadDirectoryTree = async () => {
  try {
    const res = await getDirectoryTree(knowledgeBaseId.value)
    directoryTree.value = res.data || []
  } catch (error) {
    console.error('获取目录树失败:', error)
  }
}

// 加载文档列表
const loadDocumentList = async () => {
  docListLoading.value = true
  try {
    const params = { knowledgeBaseId: knowledgeBaseId.value }
    if (selectedTagId.value) {
      params.tagId = selectedTagId.value
    }
    const res = await getDocumentList(params)
    documentList.value = res.data || []
  } catch (error) {
    console.error('获取文档列表失败:', error)
  } finally {
    docListLoading.value = false
  }
}

// 加载标签列表
const loadTagList = async () => {
  try {
    const res = await getTagList(knowledgeBaseId.value)
    tagList.value = res.data || []
  } catch (error) {
    console.error('获取标签列表失败:', error)
  }
}

// 加载文档详情
const loadDocumentDetail = async (doc) => {
  try {
    const res = await getDocumentDetail(doc.id)
    currentDocument.value = res.data
    editingTitle.value = res.data.title
    editingContent.value = res.data.content || ''
  } catch (error) {
    console.error('获取文档详情失败:', error)
    ElMessage.error('获取文档详情失败')
  }
}

// 点击目录
const handleDirectoryClick = (data) => {
  loadDocumentListByDirectory(data.id)
}

// 按目录加载文档
const loadDocumentListByDirectory = async (directoryId) => {
  docListLoading.value = true
  try {
    const res = await getDocumentList({
      knowledgeBaseId: knowledgeBaseId.value,
      directoryId
    })
    documentList.value = res.data || []
  } catch (error) {
    console.error('获取文档列表失败:', error)
  } finally {
    docListLoading.value = false
  }
}

// 点击文档
const handleDocumentClick = async (doc) => {
  await loadDocumentDetail(doc)
}

// 标签筛选变化
const handleTagFilterChange = () => {
  loadDocumentList()
}

// 保存文档
const handleSaveDocument = async () => {
  if (!currentDocument.value) return
  saving.value = true
  try {
    await updateDocument({
      id: currentDocument.value.id,
      title: editingTitle.value,
      content: editingContent.value,
      tags: currentDocument.value.tags
    })
    ElMessage.success('保存成功')
    currentDocument.value.title = editingTitle.value
    currentDocument.value.content = editingContent.value
  } catch (error) {
    console.error('保存文档失败:', error)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 标题失焦保存
const handleTitleBlur = () => {
  if (currentDocument.value && editingTitle.value !== currentDocument.value.title) {
    handleSaveDocument()
  }
}

// 添加标签
const handleAddTag = async (tag) => {
  if (!currentDocument.value) return
  const tags = [...(currentDocument.value.tags || []), tag.name]
  currentDocument.value.tags = tags
  await updateDocument({
    id: currentDocument.value.id,
    title: editingTitle.value,
    content: editingContent.value,
    tags
  })
  ElMessage.success('添加标签成功')
}

// 移除标签
const handleRemoveTag = async (tagName) => {
  if (!currentDocument.value) return
  const tags = currentDocument.value.tags.filter(t => t !== tagName)
  currentDocument.value.tags = tags
  await updateDocument({
    id: currentDocument.value.id,
    title: editingTitle.value,
    content: editingContent.value,
    tags
  })
  ElMessage.success('移除标签成功')
}

// 创建目录
const handleCreateDirectory = () => {
  isEditDirectory.value = false
  directoryForm.id = null
  directoryForm.parentId = null
  directoryForm.name = ''
  directoryDialogVisible.value = true
}

// 创建子目录
const handleCreateSubDirectory = (parent) => {
  isEditDirectory.value = false
  directoryForm.id = null
  directoryForm.parentId = parent.id
  directoryForm.name = ''
  directoryDialogVisible.value = true
}

// 编辑目录
const handleEditDirectory = (row) => {
  isEditDirectory.value = true
  directoryForm.id = row.id
  directoryForm.parentId = row.parentId
  directoryForm.name = row.name
  directoryDialogVisible.value = true
}

// 删除目录
const handleDeleteDirectory = (row) => {
  ElMessageBox.confirm(`确定要删除目录 "${row.name}" 吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteDirectory(row.id)
      ElMessage.success('删除成功')
      loadDirectoryTree()
    } catch (error) {
      console.error('删除目录失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 提交目录表单
const handleSubmitDirectory = async () => {
  const valid = await dirFormRef.value?.validate().catch(() => false)
  if (!valid) return

  directoryLoading.value = true
  try {
    const data = {
      knowledgeBaseId: knowledgeBaseId.value,
      parentId: directoryForm.parentId,
      name: directoryForm.name
    }
    if (isEditDirectory.value) {
      data.id = directoryForm.id
      await updateDirectory(data)
      ElMessage.success('更新成功')
    } else {
      await createDirectory(data)
      ElMessage.success('创建成功')
    }
    directoryDialogVisible.value = false
    loadDirectoryTree()
  } catch (error) {
    console.error('提交目录失败:', error)
    ElMessage.error(isEditDirectory.value ? '更新失败' : '创建失败')
  } finally {
    directoryLoading.value = false
  }
}

// 创建文档
const handleCreateDocument = () => {
  isEditDocument.value = false
  documentForm.id = null
  documentForm.title = ''
  documentForm.directoryId = null
  documentDialogVisible.value = true
}

// 编辑文档
const handleEditDocument = (row) => {
  isEditDocument.value = true
  documentForm.id = row.id
  documentForm.title = row.title
  documentForm.directoryId = row.directoryId
  documentDialogVisible.value = true
}

// 删除文档
const handleDeleteDocument = (row) => {
  ElMessageBox.confirm(`确定要删除文档 "${row.title}" 吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteDocument(row.id)
      ElMessage.success('删除成功')
      if (currentDocument.value?.id === row.id) {
        currentDocument.value = null
        editingTitle.value = ''
        editingContent.value = ''
      }
      loadDocumentList()
    } catch (error) {
      console.error('删除文档失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 提交文档表单
const handleSubmitDocument = async () => {
  const valid = await docFormRef.value?.validate().catch(() => false)
  if (!valid) return

  documentLoading.value = true
  try {
    const data = {
      knowledgeBaseId: knowledgeBaseId.value,
      title: documentForm.title,
      directoryId: documentForm.directoryId
    }
    if (isEditDocument.value) {
      data.id = documentForm.id
      await updateDocument(data)
      ElMessage.success('更新成功')
    } else {
      const res = await createDocument(data)
      ElMessage.success('创建成功')
      // 打开新创建的文档
      await loadDocumentDetail({ id: res.data?.id })
    }
    documentDialogVisible.value = false
    loadDocumentList()
  } catch (error) {
    console.error('提交文档失败:', error)
    ElMessage.error(isEditDocument.value ? '更新失败' : '创建失败')
  } finally {
    documentLoading.value = false
  }
}

// 创建标签
const handleCreateTag = () => {
  tagForm.name = ''
  tagForm.color = '#409eff'
  tagDialogVisible.value = true
}

// 提交标签表单
const handleSubmitTag = async () => {
  const valid = await tagFormRef.value?.validate().catch(() => false)
  if (!valid) return

  tagLoading.value = true
  try {
    await createTag({
      knowledgeBaseId: knowledgeBaseId.value,
      name: tagForm.name,
      color: tagForm.color
    })
    ElMessage.success('创建成功')
    tagDialogVisible.value = false
    loadTagList()
  } catch (error) {
    console.error('创建标签失败:', error)
    ElMessage.error('创建失败')
  } finally {
    tagLoading.value = false
  }
}

// 初始化
onMounted(async () => {
  await loadKnowledgeBase()
  await Promise.all([
    loadDirectoryTree(),
    loadDocumentList(),
    loadTagList()
  ])
})
</script>

<style scoped>
.knowledge-base-detail {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-left h2 {
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.detail-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 左侧目录 */
.sidebar-left {
  width: 220px;
  background: #fff;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 12px 16px;
  font-weight: 600;
  border-bottom: 1px solid #e4e7ed;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-right: 8px;
}

.node-label {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.node-actions {
  opacity: 0;
  transition: opacity 0.2s;
}

.el-tree-node:hover .node-actions {
  opacity: 1;
}

/* 中间文档列表 */
.sidebar-middle {
  width: 280px;
  background: #fff;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
}

.tag-filter {
  padding: 12px;
  border-bottom: 1px solid #e4e7ed;
}

.document-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.document-item {
  padding: 10px 12px;
  border-radius: 4px;
  cursor: pointer;
  margin-bottom: 4px;
  transition: background 0.2s;
}

.document-item:hover {
  background: #f5f7fa;
}

.document-item.active {
  background: #ecf5ff;
  border-left: 3px solid #409eff;
}

.doc-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.doc-icon {
  color: #909399;
}

.doc-title {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 14px;
}

.doc-tags {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.more-tags {
  font-size: 12px;
  color: #909399;
}

.doc-actions {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  opacity: 0;
  transition: opacity 0.2s;
}

.document-item:hover .doc-actions {
  opacity: 1;
}

.document-item {
  position: relative;
}

/* 右侧编辑器 */
.editor-panel {
  flex: 1;
  background: #fff;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.editor-empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.editor-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.editor-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-bottom: 1px solid #e4e7ed;
}

.title-input {
  flex: 1;
}

.title-input :deep(.el-input__wrapper) {
  box-shadow: none;
  border: none;
}

.title-input :deep(.el-input__inner) {
  font-size: 18px;
  font-weight: 600;
}

.doc-tags-edit {
  display: flex;
  gap: 8px;
  align-items: center;
  padding: 8px 16px;
  border-bottom: 1px solid #e4e7ed;
  flex-wrap: wrap;
}

.editor-tabs {
  padding: 8px 16px;
  border-bottom: 1px solid #e4e7ed;
}

.editor-main {
  flex: 1;
  overflow: hidden;
  padding: 16px;
}

.md-editor {
  width: 100%;
  height: 100%;
  border: none;
  resize: none;
  font-family: 'Monaco', 'Menlo', 'Consolas', monospace;
  font-size: 14px;
  line-height: 1.6;
  outline: none;
}

.editor-preview {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  line-height: 1.8;
}

.editor-preview :deep(h1) {
  font-size: 24px;
  margin: 16px 0;
}

.editor-preview :deep(h2) {
  font-size: 20px;
  margin: 14px 0;
}

.editor-preview :deep(h3) {
  font-size: 16px;
  margin: 12px 0;
}

.editor-preview :deep(code) {
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 3px;
}

.editor-preview :deep(pre) {
  background: #f5f5f5;
  padding: 12px;
  border-radius: 4px;
  overflow-x: auto;
}

.editor-preview :deep(ul) {
  padding-left: 20px;
}

.editor-preview :deep(img) {
  max-width: 100%;
}
</style>