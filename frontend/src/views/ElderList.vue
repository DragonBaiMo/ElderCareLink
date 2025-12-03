<template>
  <div>
    <el-form :inline="true" :model="query" class="mb-3">
      <el-form-item label="姓名">
        <el-input v-model="query.name" placeholder="输入姓名关键字" />
      </el-form-item>
      <el-form-item label="居住类型">
        <el-select v-model="query.livingType" clearable placeholder="全部">
          <el-option v-for="item in livingTypes" :key="item.id" :label="item.dictValue" :value="item.dictKey" />
        </el-select>
      </el-form-item>
      <el-form-item label="健康等级">
        <el-select v-model="query.healthLevel" clearable placeholder="全部">
          <el-option v-for="item in healthLevels" :key="item.id" :label="item.dictValue" :value="item.dictKey" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" clearable placeholder="全部">
          <el-option label="在册" value="ACTIVE" />
          <el-option label="已注销" value="DISABLED" />
        </el-select>
      </el-form-item>
      <el-form-item label="责任志愿者">
        <el-select v-model="query.volunteerId" clearable placeholder="全部">
          <el-option v-for="v in volunteers" :key="v.id" :label="v.realName" :value="v.id" />
        </el-select>
      </el-form-item>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="openDialog()">新增</el-button>
      <el-button @click="exportData">导出 Excel</el-button>
    </el-form>

    <el-table :data="table.records" border style="width:100%">
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="gender" label="性别" width="80" />
      <el-table-column prop="age" label="年龄" width="80" />
      <el-table-column prop="livingType" label="居住类型" />
      <el-table-column prop="healthLevel" label="健康等级" />
      <el-table-column prop="responsibleVolunteerName" label="志愿者" />
      <el-table-column prop="status" label="状态" />
      <el-table-column label="操作" width="240">
        <template #default="scope">
          <el-button link type="primary" @click="openDialog(scope.row)">编辑</el-button>
          <el-button link type="info" @click="viewDetail(scope.row)">详情</el-button>
          <el-button link type="danger" @click="disable(scope.row.id)">注销</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination class="mt-3" layout="prev, pager, next" :total="table.total" :page-size="query.size" @current-change="(p)=>{query.page=p;load()}" />

    <el-dialog v-model="dialog.visible" :title="dialog.form.id ? '编辑老人' : '新增老人'" width="680px">
      <el-form :model="dialog.form" label-width="120px">
        <el-form-item label="姓名"><el-input v-model="dialog.form.name" /></el-form-item>
        <el-form-item label="性别"><el-select v-model="dialog.form.gender"><el-option label="男" value="男" /><el-option label="女" value="女" /></el-select></el-form-item>
        <el-form-item label="年龄"><el-input v-model.number="dialog.form.age" type="number" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="dialog.form.phone" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="dialog.form.address" /></el-form-item>
        <el-form-item label="居住类型"><el-select v-model="dialog.form.livingType"><el-option v-for="i in livingTypes" :key="i.id" :label="i.dictValue" :value="i.dictKey" /></el-select></el-form-item>
        <el-form-item label="健康等级"><el-select v-model="dialog.form.healthLevel"><el-option v-for="i in healthLevels" :key="i.id" :label="i.dictValue" :value="i.dictKey" /></el-select></el-form-item>
        <el-form-item label="紧急联系人"><el-input v-model="dialog.form.emergencyContactName" /></el-form-item>
        <el-form-item label="紧急电话"><el-input v-model="dialog.form.emergencyContactPhone" /></el-form-item>
        <el-form-item label="志愿者">
          <el-select v-model="dialog.form.responsibleVolunteerId" clearable placeholder="选择志愿者" @change="syncVolunteerName">
            <el-option v-for="v in volunteers" :key="v.id" :label="v.realName" :value="v.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input type="textarea" v-model="dialog.form.remarks" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible=false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" title="老人详情" size="50%">
      <div v-if="detail">
        <p>姓名：{{ detail.elder.name }} | 电话：{{ detail.elder.phone }} | 志愿者：{{ detail.elder.responsibleVolunteerName }}</p>
        <h4>最近健康记录</h4>
        <el-timeline>
          <el-timeline-item v-for="h in detail.recentHealthRecords" :key="h.id" :timestamp="h.recordDate">
            {{ h.healthDesc || '无描述' }}，血压：{{ h.bloodPressure }}，心率：{{ h.heartRate }}
          </el-timeline-item>
        </el-timeline>
        <h4>最近探访</h4>
        <el-timeline>
          <el-timeline-item v-for="v in detail.recentVisitRecords" :key="v.id" :timestamp="v.visitTime">
            {{ v.serviceContent }}（{{ v.elderStatus }}）
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import api from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const query = reactive({ page: 1, size: 10, name: '', livingType: '', healthLevel: '', status: '', volunteerId: '' })
const table = reactive({ records: [], total: 0 })
const dialog = reactive({ visible: false, form: {} })
const livingTypes = ref([])
const healthLevels = ref([])
const volunteers = ref([])
const detailVisible = ref(false)
const detail = ref(null)

const loadDicts = async () => {
  const living = await api.get('/dicts/living_type')
  const health = await api.get('/dicts/health_level')
  livingTypes.value = living.code === 0 ? living.data : []
  healthLevels.value = health.code === 0 ? health.data : []
}

const loadVolunteers = async () => {
  const res = await api.get('/users', { params: { role: 'VOLUNTEER', page: 0, size: 50 } })
  volunteers.value = res.code === 0 ? res.data.content : []
}

const load = async () => {
  const res = await api.get('/elders', { params: { page: query.page - 1, size: query.size, ...query } })
  if (res.code === 0) {
    table.records = res.data.content
    table.total = res.data.totalElements
  }
}

const openDialog = (row = null) => {
  dialog.visible = true
  dialog.form = row ? { ...row } : { name: '', gender: '女', age: 70, livingType: '', healthLevel: '', phone: '', address: '', emergencyContactName: '', emergencyContactPhone: '', responsibleVolunteerId: '', remarks: '' }
}

const syncVolunteerName = (val) => {
  const user = volunteers.value.find(v => v.id === val)
  if (user) {
    dialog.form.responsibleVolunteerName = user.realName
  }
}

const save = async () => {
  if (dialog.form.id) {
    await api.put(`/elders/${dialog.form.id}`, dialog.form)
  } else {
    await api.post('/elders', dialog.form)
  }
  ElMessage.success('保存成功')
  dialog.visible = false
  load()
}

const disable = async (id) => {
  await ElMessageBox.confirm('确认将老人标记为注销吗？', '提示', { type: 'warning' })
  await api.patch(`/elders/${id}/disable`)
  ElMessage.success('已处理')
  load()
}

const viewDetail = async (row) => {
  const res = await api.get(`/elders/${row.id}`)
  if (res.code === 0) {
    detail.value = res.data
    detailVisible.value = true
  }
}

const exportData = async () => {
  const res = await api.get('/elders/export', { responseType: 'blob' })
  const blob = new Blob([res])
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = '老人信息.xlsx'
  a.click()
  window.URL.revokeObjectURL(url)
}

onMounted(() => {
  loadDicts()
  loadVolunteers()
  load()
})
</script>
