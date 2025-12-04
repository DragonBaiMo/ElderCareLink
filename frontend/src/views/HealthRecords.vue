<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">健康数据记录</h2>
      <el-button type="primary" icon="Plus" @click="openDialog()">录入数据</el-button>
    </div>

    <div class="filter-container">
       <el-form :inline="true" :model="query" class="search-form">
          <el-form-item label="选择老人">
             <el-select v-model="query.elderId" filterable placeholder="输入姓名搜索" style="width: 200px" @change="load">
                <el-option v-for="e in elders" :key="e.id" :label="e.name" :value="e.id" />
             </el-select>
          </el-form-item>
          <el-form-item label="日期范围">
             <el-date-picker v-model="query.range" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DDTHH:mm:ss" />
          </el-form-item>
          <el-form-item>
             <el-button type="primary" @click="load">查询</el-button>
          </el-form-item>
       </el-form>
    </div>

    <div class="pro-card no-padding">
       <el-table :data="table.records" style="width: 100%">
          <el-table-column prop="recordDate" label="记录时间" width="180">
             <template #default="scope">{{ formatDate(scope.row.recordDate) }}</template>
          </el-table-column>
          <el-table-column prop="bloodPressure" label="血压 (mmHg)" width="150">
             <template #default="scope">
                <span :style="getBPColor(scope.row.bloodPressure)">{{ scope.row.bloodPressure }}</span>
             </template>
          </el-table-column>
          <el-table-column prop="heartRate" label="心率 (bpm)" width="120">
             <template #default="scope">
                <span :class="{'text-danger': scope.row.heartRate > 100 || scope.row.heartRate < 50}">{{ scope.row.heartRate }}</span>
             </template>
          </el-table-column>
          <el-table-column prop="temperature" label="体温 (°C)" width="120" />
          <el-table-column prop="healthDesc" label="健康摘要" show-overflow-tooltip />
          <el-table-column label="操作" width="150">
             <template #default="scope">
                <el-button link type="primary" @click="openDialog(scope.row)">编辑</el-button>
                <el-button link type="danger" @click="remove(scope.row.id)">删除</el-button>
             </template>
          </el-table-column>
       </el-table>
    </div>

    <el-dialog v-model="dialog.visible" :title="dialog.form.id ? '编辑记录' : '录入体征'" width="500px">
       <el-form :model="dialog.form" label-width="80px">
          <el-form-item label="老人">
             <el-select v-model="dialog.form.elderId" filterable style="width:100%">
                <el-option v-for="e in elders" :key="e.id" :label="e.name" :value="e.id" />
             </el-select>
          </el-form-item>
          <el-row :gutter="20">
             <el-col :span="12"><el-form-item label="血压"><el-input v-model="dialog.form.bloodPressure" /></el-form-item></el-col>
             <el-col :span="12"><el-form-item label="心率"><el-input v-model="dialog.form.heartRate" /></el-form-item></el-col>
          </el-row>
          <el-row :gutter="20">
             <el-col :span="12"><el-form-item label="体温"><el-input v-model="dialog.form.temperature" /></el-form-item></el-col>
             <el-col :span="12"><el-form-item label="血糖"><el-input v-model="dialog.form.bloodSugar" /></el-form-item></el-col>
          </el-row>
          <el-form-item label="备注"><el-input type="textarea" v-model="dialog.form.healthDesc" /></el-form-item>
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
import { Plus } from '@element-plus/icons-vue'

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
  if (res.code === 0) table.records = res.data.content
}

const openDialog = (row = null) => {
  dialog.form = row ? { ...row } : { elderId: query.elderId, bloodPressure: '', heartRate: '', temperature: '', bloodSugar: '', healthDesc: '' }
  dialog.visible = true
}

const save = async () => {
  if (dialog.form.id) await api.put(`/health-records/${dialog.form.id}`, dialog.form)
  else await api.post('/health-records', dialog.form)
  ElMessage.success('保存成功')
  dialog.visible = false
  load()
}

const remove = async (id) => {
  await ElMessageBox.confirm('确认删除?')
  await api.delete(`/health-records/${id}`)
  load()
}

const formatDate = (str) => str ? str.replace('T', ' ').substring(0, 16) : ''
const getBPColor = (bp) => {
   return bp && parseInt(bp.split('/')[0]) > 140 ? 'color: #ff4d4f' : ''
}

onMounted(async () => {
  await loadElders()
  await load()
})
</script>

<style scoped>
.text-danger { color: #ff4d4f; font-weight: 600; }
.search-form .el-form-item { margin-bottom: 0 !important; margin-right: 16px !important; }
</style>