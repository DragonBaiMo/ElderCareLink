<template>
  <div class="panel-card">
    <div class="section-title">探访记录</div>
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="志愿者">
        <el-select v-model="query.volunteerId" clearable placeholder="全部" style="width:180px">
          <el-option v-for="v in volunteers" :key="v.id" :label="v.realName" :value="v.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="老人">
        <el-select v-model="query.elderId" clearable filterable placeholder="全部" style="width:180px">
          <el-option v-for="e in elders" :key="e.id" :label="e.name" :value="e.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="时间范围">
        <el-date-picker v-model="query.range" type="daterange" value-format="YYYY-MM-DDTHH:mm:ss" start-placeholder="开始" end-placeholder="结束" />
      </el-form-item>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="openDialog()">新增探访</el-button>
    </el-form>

    <el-table :data="table.records" border>
      <el-table-column prop="visitTime" label="探访时间" width="180" />
      <el-table-column prop="elderId" label="老人" />
      <el-table-column prop="volunteerId" label="志愿者" />
      <el-table-column prop="visitType" label="方式" />
      <el-table-column prop="serviceContent" label="服务内容" />
      <el-table-column prop="elderStatus" label="老人状态" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button link type="primary" @click="openDialog(scope.row)">编辑</el-button>
          <el-button link type="danger" @click="remove(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialog.visible" :title="dialog.form.id ? '编辑探访' : '新增探访'">
      <el-form :model="dialog.form" label-width="120px">
        <el-form-item label="老人">
          <el-select v-model="dialog.form.elderId" filterable style="width:200px">
            <el-option v-for="e in elders" :key="e.id" :label="e.name" :value="e.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="探访时间"><el-date-picker v-model="dialog.form.visitTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item>
        <el-form-item label="探访方式"><el-input v-model="dialog.form.visitType" /></el-form-item>
        <el-form-item label="服务内容"><el-input v-model="dialog.form.serviceContent" /></el-form-item>
        <el-form-item label="老人状态"><el-input v-model="dialog.form.elderStatus" /></el-form-item>
        <el-form-item label="建议"><el-input v-model="dialog.form.nextAdvice" /></el-form-item>
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

const query = reactive({ volunteerId: '', elderId: '', range: [] })
const volunteers = ref([])
const elders = ref([])
const table = reactive({ records: [] })
const dialog = reactive({ visible: false, form: {} })

const loadBase = async () => {
  const users = await api.get('/users', { params: { role: 'VOLUNTEER', page: 0, size: 50 } })
  volunteers.value = users.code === 0 ? users.data.content : []
  const elderRes = await api.get('/elders', { params: { page: 0, size: 100 } })
  elders.value = elderRes.code === 0 ? elderRes.data.content : []
}

const load = async () => {
  const params = {}
  if (query.volunteerId) params.volunteerId = query.volunteerId
  if (query.elderId) params.elderId = query.elderId
  if (query.range && query.range.length === 2) {
    params.start = query.range[0]
    params.end = query.range[1]
  }
  const res = await api.get('/visit-records', { params })
  if (res.code === 0) {
    table.records = res.data.content
  }
}

const openDialog = (row = null) => {
  dialog.visible = true
  dialog.form = row ? { ...row } : { elderId: query.elderId || '', visitTime: '', visitType: '入户探访', serviceContent: '', elderStatus: '', nextAdvice: '' }
}

const save = async () => {
  if (dialog.form.id) {
    await api.put(`/visit-records/${dialog.form.id}`, dialog.form)
  } else {
    await api.post('/visit-records', dialog.form)
  }
  ElMessage.success('保存成功')
  dialog.visible = false
  load()
}

const remove = async (id) => {
  await ElMessageBox.confirm('确定删除该记录吗？', '提示', { type: 'warning' })
  await api.delete(`/visit-records/${id}`)
  ElMessage.success('已删除')
  load()
}

onMounted(async () => {
  await loadBase()
  await load()
})
</script>
