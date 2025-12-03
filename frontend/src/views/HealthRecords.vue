<template>
  <div class="panel-card">
    <div class="section-title">健康记录</div>
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="选择老人">
        <el-select v-model="query.elderId" filterable placeholder="请选择老人" style="width:200px">
          <el-option v-for="e in elders" :key="e.id" :label="e.name" :value="e.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="时间范围">
        <el-date-picker v-model="query.range" type="daterange" value-format="YYYY-MM-DDTHH:mm:ss" start-placeholder="开始" end-placeholder="结束" />
      </el-form-item>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="openDialog()">新增记录</el-button>
    </el-form>

    <el-table :data="table.records" border>
      <el-table-column prop="recordDate" label="记录时间" width="180" />
      <el-table-column prop="bloodPressure" label="血压" />
      <el-table-column prop="heartRate" label="心率">
        <template #default="scope">
          <span :style="isHeartAbnormal(scope.row.heartRate) ? 'color:red' : ''">{{ scope.row.heartRate }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="temperature" label="体温" />
      <el-table-column prop="healthDesc" label="说明" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button link type="primary" @click="openDialog(scope.row)">编辑</el-button>
          <el-button link type="danger" @click="remove(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialog.visible" :title="dialog.form.id ? '编辑健康记录' : '新增健康记录'">
      <el-form :model="dialog.form" label-width="120px">
        <el-form-item label="老人">
          <el-select v-model="dialog.form.elderId" filterable style="width:200px">
            <el-option v-for="e in elders" :key="e.id" :label="e.name" :value="e.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="血压"><el-input v-model="dialog.form.bloodPressure" /></el-form-item>
        <el-form-item label="心率"><el-input v-model="dialog.form.heartRate" /></el-form-item>
        <el-form-item label="体温"><el-input v-model="dialog.form.temperature" /></el-form-item>
        <el-form-item label="血糖"><el-input v-model="dialog.form.bloodSugar" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="dialog.form.healthDesc" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible=false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import api from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const elders = ref([])
const query = reactive({ elderId: '', range: [] })
const table = reactive({ records: [] })
const dialog = reactive({ visible: false, form: {} })

const loadElders = async () => {
  const res = await api.get('/elders', { params: { page: 0, size: 100 } })
  if (res.code === 0) {
    elders.value = res.data.content
    if (!query.elderId && elders.value.length) query.elderId = elders.value[0].id
  }
}

const load = async () => {
  if (!query.elderId) return
  const params = { elderId: query.elderId }
  if (query.range && query.range.length === 2) {
    params.start = query.range[0]
    params.end = query.range[1]
  }
  const res = await api.get('/health-records', { params })
  if (res.code === 0) {
    table.records = res.data.content
  }
}

const openDialog = (row = null) => {
  dialog.visible = true
  dialog.form = row ? { ...row } : { elderId: query.elderId, bloodPressure: '', heartRate: '', temperature: '', bloodSugar: '', healthDesc: '' }
}

const save = async () => {
  if (dialog.form.id) {
    await api.put(`/health-records/${dialog.form.id}`, dialog.form)
  } else {
    await api.post('/health-records', dialog.form)
  }
  ElMessage.success('保存成功')
  dialog.visible = false
  load()
}

const remove = async (id) => {
  await ElMessageBox.confirm('确认删除该记录吗？', '提示', { type: 'warning' })
  await api.delete(`/health-records/${id}`)
  ElMessage.success('已删除')
  load()
}

const isHeartAbnormal = (rate) => rate && (rate > 100 || rate < 50)

onMounted(async () => {
  await loadElders()
  await load()
})
</script>
