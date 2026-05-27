<template>
  <div class="process-design">
    <!-- 顶部信息栏 -->
    <div class="design-header">
      <el-form :inline="true" :model="form" class="header-form">
        <el-form-item label="流程标识">
          <el-input v-model="form.processKey" :disabled="isView || isEdit" placeholder="流程标识" style="width: 160px" />
        </el-form-item>
        <el-form-item label="流程名称">
          <el-input v-model="form.processName" :disabled="isView" placeholder="流程名称" style="width: 160px" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="form.category" :disabled="isView" placeholder="分类" style="width: 120px" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" :disabled="isView" placeholder="描述" style="width: 200px" />
        </el-form-item>
      </el-form>
      <div class="header-actions">
        <el-button @click="handleBack">返回</el-button>
        <el-button v-if="!isView" @click="handleDownloadXml">导出XML</el-button>
        <el-button v-if="!isView" type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </div>
    </div>

    <!-- BPMN 画布 -->
    <div class="design-container">
      <div ref="bpmnCanvas" class="bpmn-canvas"></div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onBeforeUnmount, ref, reactive, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import BpmnModeler from 'bpmn-js/lib/Modeler'
import 'bpmn-js/dist/assets/diagram-js.css'
import 'bpmn-js/dist/assets/bpmn-js.css'
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn-embedded.css'
import getDefaultDiagram from './bpmn/defaultDiagram'
import customTranslate from './bpmn/zh-CN'
import {
    getProcessDefinition,
    getBpmnXml,
    saveProcessDefinition,
    updateProcessDefinition
} from '@/api/workflow'

const route = useRoute()
const router = useRouter()

const bpmnCanvas = ref(null)
const saving = ref(false)
let bpmnModeler = null

const form = reactive({
    processKey: '',
    processName: '',
    category: '',
    description: ''
})

const definitionId = computed(() => route.query.id)
const isView = computed(() => route.query.mode === 'view')
const isEdit = computed(() => Boolean(definitionId.value))

// 自定义翻译模块
function customTranslateModule(template, replacements) {
    return customTranslate[template] || template
}

const translateModule = {
    translate: ['value', customTranslateModule]
}

onMounted(async () => {
    await nextTick()
    initModeler()
    if (definitionId.value) {
        await loadDefinition()
    } else {
        await loadDefaultDiagram()
    }
})

onBeforeUnmount(() => {
    if (bpmnModeler) {
        bpmnModeler.destroy()
        bpmnModeler = null
    }
})

function initModeler() {
    bpmnModeler = new BpmnModeler({
        container: bpmnCanvas.value,
        additionalModules: [translateModule],
        moddleExtensions: {}
    })
}

async function loadDefaultDiagram() {
    const xml = getDefaultDiagram(form.processKey || 'process_1', form.processName || '新流程')
    try {
        await bpmnModeler.importXML(xml)
        bpmnModeler.get('canvas').zoom('fit-viewport')
    } catch (err) {
        console.error('加载默认流程图失败', err)
    }
}

async function loadDefinition() {
    try {
        const [defRes, xmlRes] = await Promise.all([
            getProcessDefinition(definitionId.value),
            getBpmnXml(definitionId.value)
        ])

        if (defRes.code === 200 && defRes.data) {
            form.processKey = defRes.data.processKey || ''
            form.processName = defRes.data.processName || ''
            form.category = defRes.data.category || ''
            form.description = defRes.data.description || ''
        }

        if (xmlRes.code === 200 && xmlRes.data) {
            await bpmnModeler.importXML(xmlRes.data)
            bpmnModeler.get('canvas').zoom('fit-viewport')
        } else {
            await loadDefaultDiagram()
        }
    } catch (err) {
        console.error('加载流程定义失败', err)
        ElMessage.error('加载流程定义失败')
        await loadDefaultDiagram()
    }
}

async function handleSave() {
    if (!form.processKey) {
        ElMessage.warning('流程标识不能为空')
        return
    }
    if (!form.processName) {
        ElMessage.warning('流程名称不能为空')
        return
    }

    saving.value = true
    try {
        const { xml } = await bpmnModeler.saveXML({ format: true })

        const payload = {
            processKey: form.processKey,
            processName: form.processName,
            category: form.category,
            description: form.description,
            bpmnXml: xml
        }

        if (definitionId.value) {
            await updateProcessDefinition(definitionId.value, payload)
            ElMessage.success('更新成功')
        } else {
            await saveProcessDefinition(payload)
            ElMessage.success('保存成功')
        }
        handleBack()
    } catch (err) {
        ElMessage.error(err.message || '保存失败')
    } finally {
        saving.value = false
    }
}

async function handleDownloadXml() {
    try {
        const { xml } = await bpmnModeler.saveXML({ format: true })
        const blob = new Blob([xml], { type: 'application/xml' })
        const url = URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `${form.processKey || 'process'}.bpmn20.xml`
        link.click()
        URL.revokeObjectURL(url)
    } catch (err) {
        ElMessage.error('导出失败')
    }
}

function handleBack() {
    router.push('/workflow/definition')
}
</script>

<style scoped>
.process-design {
    display: flex;
    flex-direction: column;
    height: calc(100vh - 84px);
}

.design-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 8px 16px;
    border-bottom: 1px solid var(--el-border-color-lighter, #ebeef5);
    background: var(--el-bg-color, #fff);
    flex-shrink: 0;
}

.header-form {
    flex: 1;
    margin-bottom: 0;
}

.header-form :deep(.el-form-item) {
    margin-bottom: 0;
    margin-right: 12px;
}

.header-actions {
    display: flex;
    gap: 8px;
    flex-shrink: 0;
}

.design-container {
    flex: 1;
    position: relative;
    overflow: hidden;
}

.bpmn-canvas {
    width: 100%;
    height: 100%;
}

/* bpmn.js 工具栏样式适配 */
.bpmn-canvas :deep(.djs-palette) {
    top: 10px;
    left: 10px;
}

.bpmn-canvas :deep(.djs-minimap) {
    bottom: 10px;
    right: 10px;
}
</style>
