<template>
  <div class="process-design">
    <el-card class="form-card">
      <div class="header">
        <div class="title">{{ titleText }}</div>
        <div class="actions">
          <el-button @click="handleBack">返回</el-button>
          <el-button v-if="!isView" type="primary" :loading="saving" @click="handleSave">
            保存
          </el-button>
        </div>
      </div>

      <el-form :model="form" label-width="100px">
        <el-form-item label="流程标识" required>
          <el-input v-model="form.processKey" :disabled="isView || isEdit" placeholder="请输入流程标识" />
        </el-form-item>
        <el-form-item label="流程名称" required>
          <el-input v-model="form.processName" :disabled="isView" placeholder="请输入流程名称" />
        </el-form-item>
        <el-form-item label="流程分类">
          <el-input v-model="form.category" :disabled="isView" placeholder="请输入流程分类" />
        </el-form-item>
        <el-form-item label="流程描述">
          <el-input v-model="form.description" :disabled="isView" placeholder="请输入流程描述" />
        </el-form-item>
        <div class="section">
          <div class="section-header">
            <div class="section-title">节点配置</div>
            <el-button v-if="!isView" type="primary" plain @click="addNode">新增节点</el-button>
          </div>
          <div v-if="form.nodes.length === 0" class="empty-hint">暂无节点，请新增</div>
          <div v-for="(node, index) in form.nodes" :key="node.localId" class="config-card">
            <div class="card-header">
              <div class="card-title">节点 {{ index + 1 }}</div>
              <el-button v-if="!isView" type="danger" link @click="removeNode(index)">删除</el-button>
            </div>
            <el-row :gutter="12">
              <el-col :span="12">
                <el-form-item label="节点Key" required>
                  <el-input v-model="node.nodeKey" :disabled="isView" placeholder="唯一标识" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="节点名称" required>
                  <el-input v-model="node.nodeName" :disabled="isView" placeholder="显示名称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="节点类型" required>
                  <el-select v-model="node.nodeType" :disabled="isView" placeholder="请选择">
                    <el-option label="开始节点" value="START" />
                    <el-option label="结束节点" value="END" />
                    <el-option label="审批节点" value="APPROVAL" />
                    <el-option label="条件网关" value="CONDITION" />
                    <el-option label="并行网关" value="PARALLEL" />
                    <el-option label="子流程" value="SUB_PROCESS" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="事件处理">
                  <el-input v-model="node.eventHandler" :disabled="isView" placeholder="处理器标识" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="候选人类型">
                  <el-select v-model="node.candidateType" :disabled="isView" placeholder="请选择">
                    <el-option label="指定用户" value="USER" />
                    <el-option label="指定角色" value="ROLE" />
                    <el-option label="指定部门" value="DEPARTMENT" />
                    <el-option label="部门负责人" value="DEPARTMENT_LEADER" />
                    <el-option label="发起人所属部门" value="INITIATOR_DEPT" />
                    <el-option label="发起人部门及上级" value="DEPT_UPWARD" />
                    <el-option label="表达式" value="EXPRESSION" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="候选人配置">
                  <el-input v-model="node.candidateConfig" :disabled="isView" placeholder='如 {"userIds":[1,2]}' />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="审批方式">
                  <el-select v-model="node.approveType" :disabled="isView" placeholder="请选择">
                    <el-option label="顺序审批" value="SEQUENCE" />
                    <el-option label="会签" value="COUNTER_SIGN" />
                    <el-option label="或签" value="OR_SIGN" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="通过比例">
                  <el-input-number v-model="node.approveRatio" :disabled="isView" :min="0" :max="1" :step="0.1" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="可回退">
                  <el-switch v-model="node.canRollback" :disabled="isView" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="可回退节点">
                  <el-input v-model="node.rollbackNodes" :disabled="isView" placeholder="节点Key，逗号分隔" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="X 坐标">
                  <el-input-number v-model="node.positionX" :disabled="isView" :min="0" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="Y 坐标">
                  <el-input-number v-model="node.positionY" :disabled="isView" :min="0" />
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </div>

        <div class="section">
          <div class="section-header">
            <div class="section-title">连线配置</div>
            <el-button v-if="!isView" type="primary" plain @click="addRelation">新增连线</el-button>
          </div>
          <div v-if="form.relations.length === 0" class="empty-hint">暂无连线，请新增</div>
          <div v-for="(relation, index) in form.relations" :key="relation.localId" class="config-card">
            <div class="card-header">
              <div class="card-title">连线 {{ index + 1 }}</div>
              <el-button v-if="!isView" type="danger" link @click="removeRelation(index)">删除</el-button>
            </div>
            <el-row :gutter="12">
              <el-col :span="12">
                <el-form-item label="起点" required>
                  <el-select v-model="relation.sourceNodeKey" :disabled="isView" placeholder="选择节点">
                    <el-option
                      v-for="option in nodeKeyOptions"
                      :key="option.value"
                      :label="option.label"
                      :value="option.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="终点" required>
                  <el-select v-model="relation.targetNodeKey" :disabled="isView" placeholder="选择节点">
                    <el-option
                      v-for="option in nodeKeyOptions"
                      :key="option.value + '_t'"
                      :label="option.label"
                      :value="option.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="条件表达式">
                  <el-input v-model="relation.conditionExpr" :disabled="isView" placeholder="如 amount &gt; 1000" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="连线名称">
                  <el-input v-model="relation.relationName" :disabled="isView" placeholder="显示名称" />
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProcessDefinition, saveProcessDefinition, updateProcessDefinition } from '@/api/workflow'

const route = useRoute()
const router = useRouter()

const saving = ref(false)
const loading = ref(false)

const form = reactive({
  processKey: '',
  processName: '',
  category: '',
  description: '',
  nodes: [],
  relations: []
})

const definitionId = computed(() => route.query.id)
const isView = computed(() => route.query.mode === 'view')
const isEdit = computed(() => Boolean(definitionId.value))

const titleText = computed(() => {
  if (isView.value) return '查看流程'
  if (isEdit.value) return '编辑流程'
  return '新增流程'
})

const handleBack = () => {
  router.push('/workflow/definition')
}

const createNode = () => ({
  localId: `node_${Date.now()}_${Math.random().toString(16).slice(2)}`,
  nodeKey: '',
  nodeName: '',
  nodeType: 'APPROVAL',
  eventHandler: '',
  candidateType: '',
  candidateConfig: '',
  approveType: '',
  approveRatio: null,
  canRollback: false,
  rollbackNodes: '',
  positionX: null,
  positionY: null
})

const createRelation = () => ({
  localId: `relation_${Date.now()}_${Math.random().toString(16).slice(2)}`,
  sourceNodeKey: '',
  targetNodeKey: '',
  conditionExpr: '',
  relationName: ''
})

const addNode = () => {
  form.nodes.push(createNode())
}

const removeNode = (index) => {
  form.nodes.splice(index, 1)
}

const addRelation = () => {
  form.relations.push(createRelation())
}

const removeRelation = (index) => {
  form.relations.splice(index, 1)
}

const nodeKeyOptions = computed(() => {
  return form.nodes
    .filter(node => node.nodeKey)
    .map(node => ({
      label: node.nodeName ? `${node.nodeName} (${node.nodeKey})` : node.nodeKey,
      value: node.nodeKey
    }))
})

const loadDetail = async () => {
  if (!definitionId.value) {
    return
  }
  loading.value = true
  try {
    const res = await getProcessDefinition(definitionId.value)
    if (res.code === 200 && res.data) {
      form.processKey = res.data.processKey || ''
      form.processName = res.data.processName || ''
      form.category = res.data.category || ''
      form.description = res.data.description || ''
      form.nodes = (res.data.nodes || []).map(node => ({
        localId: `node_${Date.now()}_${Math.random().toString(16).slice(2)}`,
        nodeKey: node.nodeKey || '',
        nodeName: node.nodeName || '',
        nodeType: node.nodeType || 'APPROVAL',
        eventHandler: node.eventHandler || '',
        candidateType: node.candidateType || '',
        candidateConfig: node.candidateConfig || '',
        approveType: node.approveType || '',
        approveRatio: node.approveRatio === null || node.approveRatio === undefined ? null : Number(node.approveRatio),
        canRollback: Boolean(node.canRollback),
        rollbackNodes: node.rollbackNodes || '',
        positionX: node.positionX === null || node.positionX === undefined ? null : Number(node.positionX),
        positionY: node.positionY === null || node.positionY === undefined ? null : Number(node.positionY)
      }))
      form.relations = (res.data.nodeRelations || []).map(relation => ({
        localId: `relation_${Date.now()}_${Math.random().toString(16).slice(2)}`,
        sourceNodeKey: relation.sourceNodeKey || '',
        targetNodeKey: relation.targetNodeKey || '',
        conditionExpr: relation.conditionExpr || '',
        relationName: relation.relationName || ''
      }))
    }
  } finally {
    loading.value = false
  }
}

const validateNodes = () => {
  if (form.nodes.length === 0) {
    ElMessage.warning('请至少配置一个节点')
    return false
  }
  const keySet = new Set()
  for (const node of form.nodes) {
    if (!node.nodeKey) {
      ElMessage.warning('节点Key不能为空')
      return false
    }
    if (!node.nodeName) {
      ElMessage.warning('节点名称不能为空')
      return false
    }
    if (!node.nodeType) {
      ElMessage.warning('节点类型不能为空')
      return false
    }
    if (keySet.has(node.nodeKey)) {
      ElMessage.warning(`节点Key重复: ${node.nodeKey}`)
      return false
    }
    keySet.add(node.nodeKey)
    if (node.nodeType === 'APPROVAL' && !node.candidateType) {
      ElMessage.warning(`审批节点未配置候选人类型: ${node.nodeName}`)
      return false
    }
  }
  return true
}

const validateRelations = () => {
  const nodeKeySet = new Set(form.nodes.map(node => node.nodeKey))
  for (const relation of form.relations) {
    if (!relation.sourceNodeKey || !relation.targetNodeKey) {
      ElMessage.warning('连线起点和终点不能为空')
      return false
    }
    if (!nodeKeySet.has(relation.sourceNodeKey) || !nodeKeySet.has(relation.targetNodeKey)) {
      ElMessage.warning('连线的起点或终点不存在')
      return false
    }
  }
  return true
}

const buildPayload = () => {
  const nodes = form.nodes.map(node => ({
    nodeKey: node.nodeKey,
    nodeName: node.nodeName,
    nodeType: node.nodeType,
    eventHandler: node.eventHandler,
    candidateType: node.candidateType,
    candidateConfig: node.candidateConfig,
    approveType: node.approveType,
    approveRatio: node.approveRatio === null || node.approveRatio === undefined || node.approveRatio === ''
      ? null
      : Number(node.approveRatio),
    canRollback: node.canRollback ? 1 : 0,
    rollbackNodes: node.rollbackNodes,
    positionX: node.positionX === null || node.positionX === undefined || node.positionX === ''
      ? null
      : Number(node.positionX),
    positionY: node.positionY === null || node.positionY === undefined || node.positionY === ''
      ? null
      : Number(node.positionY)
  }))
  const nodeRelations = form.relations.map(relation => ({
    sourceNodeKey: relation.sourceNodeKey,
    targetNodeKey: relation.targetNodeKey,
    conditionExpr: relation.conditionExpr,
    relationName: relation.relationName
  }))
  return {
    processKey: form.processKey,
    processName: form.processName,
    category: form.category,
    description: form.description,
    nodes,
    nodeRelations
  }
}

const handleSave = async () => {
  if (isView.value) return
  if (!form.processKey) {
    ElMessage.warning('流程标识不能为空')
    return
  }
  if (!form.processName) {
    ElMessage.warning('流程名称不能为空')
    return
  }
  if (!validateNodes() || !validateRelations()) {
    return
  }
  saving.value = true
  try {
    const payload = buildPayload()
    if (definitionId.value) {
      await updateProcessDefinition(definitionId.value, payload)
      ElMessage.success('更新成功')
    } else {
      await saveProcessDefinition(payload)
      ElMessage.success('保存成功')
    }
    handleBack()
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  await loadDetail()
  if (!definitionId.value && form.nodes.length === 0) {
    addNode()
  }
})
</script>

<style scoped>
.process-design {
  padding: 16px;
}

.form-card {
  min-height: calc(100vh - 120px);
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.actions {
  display: flex;
  gap: 8px;
}

.section {
  margin-top: 12px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 8px 0 12px;
}

.section-title {
  font-weight: 600;
  color: #303133;
}

.empty-hint {
  color: #909399;
  margin-bottom: 8px;
}

.config-card {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 12px;
  margin-bottom: 12px;
  background: #fff;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.card-title {
  font-weight: 600;
  color: #303133;
}
</style>
