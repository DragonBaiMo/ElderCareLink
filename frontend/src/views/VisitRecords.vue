<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">探访记录</h2>
      <el-button type="primary" icon="Plus" @click="openDialog()">新增探访</el-button>
    </div>

    <div class="filter-container">
      <el-select v-model="query.volunteerId" clearable placeholder="志愿者" filterable style="width: 160px; margin-right: 16px;">
        <el-option v-for="v in volunteers" :key="v.id" :label="v.realName" :value="v.id" />
      </el-select>
      <el-select v-model="query.elderId" clearable placeholder="老人" filterable style="width: 160px; margin-right: 16px;">
        <el-option v-for="e in elders" :key="e.id" :label="e.name" :value="e.id" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
    </div>

    <div class="pro-card no-padding">
       <el-table :data="table.records" style="width: 100%">
          <el-table-column prop="visitTime" label="探访时间" width="180">
             <template #default="scope">{{ formatDate(scope.row.visitTime) }}</template>
          </el-table-column>
          <el-table-column prop="elderId" label="服务对象">
             <template #default="scope">{{ getElderName(scope.row.elderId) }}</template>
          </el-table-column>
          <el-table-column prop="volunteerId" label="执行志愿者">
             <template #default="scope">{{ getVolunteerName(scope.row.volunteerId) }}</template>
          </el-table-column>
          <el-table-column prop="visitType" label="方式" width="120">
             <template #default="scope"><el-tag effect="plain">{{ scope.row.visitType }}</el-tag></template>
          </el-table-column>
          <el-table-column prop="serviceContent" label="服务内容" show-overflow-tooltip />
          <el-table-column prop="elderStatus" label="老人状态" />
          <el-table-column label="操作" width="150">
             <template #default="scope">
                <el-button link type="primary" @click="openDialog(scope.row)">编辑</el-button>
                <el-button link type="danger" @click="remove(scope.row.id)">删除</el-button>
             </template>
          </el-table-column>
       </el-table>
    </div>

    <el-dialog v-model="dialog.visible" :title="dialog.form.id ? '编辑探访' : '新增探访'" width="600px">
       <el-form :model="dialog.form" label-width="100px">
          <el-form-item label="老人">
             <el-select v-model="dialog.form.elderId" filterable style="width:100%">
                <el-option v-for="e in elders" :key="e.id" :label="e.name" :value="e.id" />
             </el-select>
          </el-form-item>
          <el-form-item label="时间"><el-date-picker v-model="dialog.form.visitTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%" /></el-form-item>
          <el-form-item label="方式">
             <el-radio-group v-model="dialog.form.visitType">
                <el-radio-button label="入户探访" />
                <el-radio-button label="电话慰问" />
                <el-radio-button label="视频连线" />
             </el-radio-group>
          </el-form-item>
          <el-form-item label="服务内容"><el-input type="textarea" v-model="dialog.form.serviceContent" /></el-form-item>
          <el-form-item label="状态反馈"><el-input v-model="dialog.form.elderStatus" /></el-form-item>
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
import { Plus } from '@element-plus/icons-vue'

const query = reactive({ volunteerId: '', elderId: '' })
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
  const params = { ...query }
  const res = await api.get('/visit-records', { params })
  if (res.code === 0) table.records = res.data.content
}

const openDialog = (row = null) => {
  dialog.form = row ? { ...row } : { elderId: '', visitTime: '', visitType: '入户探访' }
  dialog.visible = true
}

const save = async () => {
  if (dialog.form.id) await api.put(`/visit-records/${dialog.form.id}`, dialog.form)
  else await api.post('/visit-records', dialog.form)
  ElMessage.success('保存成功')
  dialog.visible = false
  load()
}

const remove = async (id) => {
  await ElMessageBox.confirm('确认删除?')
  await api.delete(`/visit-records/${id}`)
  load()
}

const formatDate = (str) => str ? str.replace('T', ' ').substring(0, 16) : ''
const getElderName = (id) => elders.value.find(e => e.id === id)?.name || id
const getVolunteerName = (id) => volunteers.value.find(v => v.id === id)?.realName || id

onMounted(async () => {
  await loadBase()
  await load()
})
</script>