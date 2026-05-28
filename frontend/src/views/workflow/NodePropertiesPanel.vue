<template>
  <div v-if="visible" class="node-properties-panel">
    <div class="panel-header">
      <span class="panel-title">{{ selectedElement?.businessObject?.name || '节点属性' }}</span>
      <el-button :icon="Close" link @click="$emit('close')" />
    </div>

    <div class="panel-body">
      <!-- 基本信息 -->
      <div class="section">
        <div class="section-title">基本信息</div>
        <el-form label-width="80px" size="small">
          <el-form-item label="节点ID">
            <el-input :model-value="selectedElement?.id" disabled />
          </el-form-item>
          <el-form-item label="节点名称">
            <el-input :model-value="selectedElement?.businessObject?.name" disabled />
          </el-form-item>
        </el-form>
      </div>

      <!-- 候选人配置（仅 UserTask） -->
      <div v-if="isUserTask" class="section">
        <div class="section-title">候选人配置</div>
        <el-form label-width="80px" size="small">
          <el-form-item label="候选人类型">
            <el-select v-model="nodeConfig.candidateType" placeholder="请选择" :disabled="isView" @change="handleCandidateTypeChange">
              <el-option v-for="item in candidateTypes" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>

          <!-- 指定角色 -->
          <el-form-item v-if="nodeConfig.candidateType === 'ROLE'" label="选择角色">
            <el-select v-model="nodeConfig.roleIds" multiple placeholder="请选择角色" :disabled="isView" style="width: 100%">
              <el-option v-for="role in roleList" :key="role.id" :label="role.roleName" :value="role.id" />
            </el-select>
          </el-form-item>

          <!-- 指定用户 -->
          <el-form-item v-if="nodeConfig.candidateType === 'USER'" label="选择用户">
            <el-select v-model="nodeConfig.userIds" multiple filterable remote :remote-method="searchUsers"
                       placeholder="输入搜索用户" :disabled="isView" :loading="userLoading" style="width: 100%">
              <el-option v-for="user in userList" :key="user.id" :label="`${user.nickname}(${user.username})`" :value="user.id" />
            </el-select>
          </el-form-item>

          <!-- 指定部门 / 部门负责人 -->
          <el-form-item v-if="nodeConfig.candidateType === 'DEPARTMENT' || nodeConfig.candidateType === 'DEPARTMENT_LEADER'" label="选择部门">
            <el-tree-select v-model="nodeConfig.deptIds" :data="deptTree" multiple
                            :props="{ label: 'name', value: 'id', children: 'children' }"
                            placeholder="请选择部门" :disabled="isView" check-strictly style="width: 100%" />
          </el-form-item>

          <!-- 发起人所属部门 -->
          <el-form-item v-if="nodeConfig.candidateType === 'INITIATOR_DEPT'">
            <el-text type="info" size="small">自动使用流程发起人所属部门的成员</el-text>
          </el-form-item>
        </el-form>
      </div>

      <!-- 事件回调 -->
      <div v-if="isUserTask" class="section">
        <div class="section-title">事件回调</div>
        <el-form label-width="80px" size="small">
          <el-form-item label="处理器">
            <el-input v-model="nodeConfig.eventHandler" placeholder="如 password_change" :disabled="isView" />
          </el-form-item>
        </el-form>
      </div>

      <!-- 操作按钮 -->
      <div v-if="isUserTask && !isView" class="panel-footer">
        <el-button type="primary" size="small" @click="handleSave">应用配置</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { Close } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getCandidateRoles, getCandidateDepartments } from '@/api/workflow'
import { pageUsers } from '@/api/user'

const props = defineProps({
  visible: { type: Boolean, default: false },
  selectedElement: { type: Object, default: null },
  modeler: { type: Object, default: null },
  isView: { type: Boolean, default: false }
})

const emit = defineEmits(['close'])

const candidateTypes = [
  { value: 'ROLE', label: '指定角色' },
  { value: 'USER', label: '指定用户' },
  { value: 'DEPARTMENT', label: '指定部门' },
  { value: 'DEPARTMENT_LEADER', label: '部门负责人' },
  { value: 'INITIATOR_DEPT', label: '发起人所属部门' }
]

const nodeConfig = reactive({
  candidateType: '',
  roleIds: [],
  userIds: [],
  deptIds: [],
  eventHandler: ''
})

const roleList = ref([])
const userList = ref([])
const userLoading = ref(false)
const deptTree = ref([])

const isUserTask = ref(false)
const dataLoaded = ref(false)

watch(() => props.selectedElement, (el) => {
  if (!el) {
    isUserTask.value = false
    return
  }
  isUserTask.value = el.type === 'bpmn:UserTask'
  if (isUserTask.value) {
    readNodeProperties(el)
    if (!dataLoaded.value) {
      dataLoaded.value = true
      loadRoles()
      loadDeptTree()
    }
  }
}, { immediate: true })

async function loadRoles() {
  try {
    const res = await getCandidateRoles()
    roleList.value = res.data || []
  } catch (e) {
    console.error('加载角色列表失败', e)
  }
}

async function loadDeptTree() {
  try {
    const res = await getCandidateDepartments()
    deptTree.value = res.data || []
  } catch (e) {
    console.error('加载部门树失败', e)
  }
}

async function searchUsers(query) {
  if (!query) return
  userLoading.value = true
  try {
    const res = await pageUsers({ keyword: query, pageNum: 1, pageSize: 20 })
    userList.value = res.data?.records || res.data?.list || []
  } catch (e) {
    console.error('搜索用户失败', e)
  } finally {
    userLoading.value = false
  }
}

function handleCandidateTypeChange() {
  nodeConfig.roleIds = []
  nodeConfig.userIds = []
  nodeConfig.deptIds = []
}

/**
 * 从 BPMN 元素的 flowable:properties 中读取节点配置
 */
function readNodeProperties(element) {
  nodeConfig.candidateType = ''
  nodeConfig.roleIds = []
  nodeConfig.userIds = []
  nodeConfig.deptIds = []
  nodeConfig.eventHandler = ''

  const bo = element.businessObject
  if (!bo.extensionElements) return

  const properties = bo.extensionElements.values?.find(
    ext => ext.$type === 'flowable:Properties'
  )
  if (!properties) return

  const propMap = {}
  for (const prop of (properties.values || [])) {
    propMap[prop.name] = prop.value
  }

  nodeConfig.candidateType = propMap.candidateType || ''
  nodeConfig.eventHandler = propMap.eventHandler || ''

  if (propMap.candidateConfig) {
    try {
      const config = JSON.parse(propMap.candidateConfig)
      nodeConfig.roleIds = (config.roleIds || []).map(Number)
      nodeConfig.userIds = (config.userIds || []).map(Number)
      nodeConfig.deptIds = (config.deptIds || []).map(Number)

      // 如果是用户类型，预加载已选用户信息
      if (nodeConfig.candidateType === 'USER' && nodeConfig.userIds.length > 0) {
        loadSelectedUsers(nodeConfig.userIds)
      }
    } catch (e) {
      console.error('解析候选人配置失败', e)
    }
  }
}

async function loadSelectedUsers(userIds) {
  try {
    const res = await pageUsers({ pageNum: 1, pageSize: 100 })
    const allUsers = res.data?.records || res.data?.list || []
    userList.value = allUsers.filter(u => userIds.includes(u.id))
  } catch (e) {
    console.error('加载用户列表失败', e)
  }
}

/**
 * 将配置写入 BPMN 元素的 flowable:properties
 */
function handleSave() {
  const element = props.selectedElement
  const modeler = props.modeler
  if (!element || !modeler) return

  const modeling = modeler.get('modeling')
  const moddle = modeler.get('moddle')
  const bo = element.businessObject

  // 构建候选人配置 JSON
  let candidateConfig = ''
  if (nodeConfig.candidateType === 'ROLE' && nodeConfig.roleIds.length > 0) {
    candidateConfig = JSON.stringify({ roleIds: nodeConfig.roleIds })
  } else if (nodeConfig.candidateType === 'USER' && nodeConfig.userIds.length > 0) {
    candidateConfig = JSON.stringify({ userIds: nodeConfig.userIds })
  } else if ((nodeConfig.candidateType === 'DEPARTMENT' || nodeConfig.candidateType === 'DEPARTMENT_LEADER') && nodeConfig.deptIds.length > 0) {
    candidateConfig = JSON.stringify({ deptIds: nodeConfig.deptIds })
  }

  // 构建 flowable:Property 列表
  const propertyList = []
  if (nodeConfig.candidateType) {
    propertyList.push(moddle.create('flowable:Property', { name: 'candidateType', value: nodeConfig.candidateType }))
    if (candidateConfig) {
      propertyList.push(moddle.create('flowable:Property', { name: 'candidateConfig', value: candidateConfig }))
    }
  }
  if (nodeConfig.eventHandler) {
    propertyList.push(moddle.create('flowable:Property', { name: 'eventHandler', value: nodeConfig.eventHandler }))
  }

  // 构建 flowable:Properties
  const flowableProps = moddle.create('flowable:Properties', { values: propertyList })

  // 构建 taskListener（候选人分配监听器）
  const taskListener = moddle.create('flowable:TaskListener', {
    event: 'create',
    delegateExpression: '${candidateAssignmentListener}'
  })

  // 保留已有的非 flowable 扩展元素，替换 flowable 部分
  const existingExtensions = bo.extensionElements?.values?.filter(
    ext => ext.$type !== 'flowable:Properties' && ext.$type !== 'flowable:TaskListener'
  ) || []

  const extensionElements = moddle.create('bpmn:ExtensionElements', {
    values: [...existingExtensions, flowableProps, taskListener]
  })

  modeling.updateProperties(element, { extensionElements })

  ElMessage.success('节点配置已应用')
}
</script>

<style scoped>
.node-properties-panel {
  width: 320px;
  height: 100%;
  border-left: 1px solid var(--el-border-color-lighter, #ebeef5);
  background: var(--el-bg-color, #fff);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  flex-shrink: 0;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid var(--el-border-color-lighter, #ebeef5);
  flex-shrink: 0;
}

.panel-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.panel-body {
  flex: 1;
  overflow-y: auto;
  padding: 12px 16px;
}

.section {
  margin-bottom: 16px;
}

.section-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--el-text-color-regular);
  margin-bottom: 10px;
  padding-bottom: 6px;
  border-bottom: 1px solid var(--el-border-color-extra-light, #f0f0f0);
}

.section :deep(.el-form-item) {
  margin-bottom: 12px;
}

.section :deep(.el-form-item__label) {
  font-size: 12px;
}

.panel-footer {
  padding: 12px 0;
  text-align: center;
}
</style>
