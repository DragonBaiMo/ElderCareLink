<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">公告管理</h2>
      <el-button type="primary" icon="Plus" @click="openDialog()">发布公告</el-button>
    </div>
    
    <div class="filter-container">
       <el-radio-group v-model="query.status" @change="load">
          <el-radio-button label="">全部状态</el-radio-button>
          <el-radio-button label="PUBLISHED">已发布</el-radio-button>
          <el-radio-button label="DRAFT">草稿</el-radio-button>
       </el-radio-group>
    </div>

    <div class="pro-card no-padding">
      <el-table :data="list.records" style="width: 100%">
        <el-table-column prop="title" label="标题" show-overflow-tooltip>
           <template #default="scope">
              <span style="font-weight: 500">{{ scope.row.title }}</span>
           </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="120">
           <template #default="scope">
              <el-tag effect="light">{{ scope.row.type }}</el-tag>
           </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
           <template #default="scope">
              <el-badge is-dot :type="scope.row.status === 'PUBLISHED' ? 'success' : 'warning'" style="margin-right: 6px; vertical-align: middle;" />
              {{ scope.row.status === 'PUBLISHED' ? '已发布' : '草稿' }}
           </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
             <el-button link type="primary" @click="view(scope.row)">预览</el-button>
             <el-button link type="primary" @click="openDialog(scope.row)">编辑</el-button>
             <el-button link type="danger" @click="remove(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialog.visible" :title="dialog.form.id ? '编辑公告' : '发布公告'" width="600px">
       <el-form :model="dialog.form" label-width="80px">
          <el-form-item label="标题"><el-input v-model="dialog.form.title" /></el-form-item>
          <el-form-item label="类型">
             <el-select v-model="dialog.form.type" style="width: 100%">
                <el-option label="活动通知" value="活动通知" />
                <el-option label="系统公告" value="系统公告" />
                <el-option label="健康提示" value="健康提示" />
             </el-select>
          </el-form-item>
          <el-form-item label="内容"><el-input type="textarea" :rows="6" v-model="dialog.form.content" /></el-form-item>
          <el-form-item label="状态">
             <el-radio-group v-model="dialog.form.status">
                <el-radio label="PUBLISHED">立即发布</el-radio>
                <el-radio label="DRAFT">存为草稿</el-radio>
             </el-radio-group>
          </el-form-item>
       </el-form>
       <template #footer>
          <el-button @click="dialog.visible=false">取消</el-button>
          <el-button type="primary" @click="save">确认</el-button>
       </template>
    </el-dialog>
    
    <el-dialog v-model="showDetail" title="公告详情">
       <div class="detail-view">
          <h3 class="text-center">{{ current.title }}</h3>
          <div class="text-center text-gray-500 text-sm mb-6">{{ current.publishTime }}</div>
          <p style="white-space: pre-line; line-height: 1.8;">{{ current.content }}</p>
       </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import api from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const list = reactive({ records: [] })
const dialog = reactive({ visible: false, form: {} })
const query = reactive({ status: '' })
const showDetail = ref(false)
const current = reactive({})

const load = async () => {
  const res = await api.get('/announcements', { params: { status: query.status } })
  if (res.code === 0) list.records = res.data.content
}

const openDialog = (row = null) => {
  dialog.form = row ? { ...row } : { title: '', content: '', type: '活动通知', status: 'PUBLISHED' }
  dialog.visible = true
}

const save = async () => {
  if (dialog.form.id) await api.put(`/announcements/${dialog.form.id}`, dialog.form)
  else await api.post('/announcements', dialog.form)
  ElMessage.success('已保存')
  dialog.visible = false
  load()
}

const remove = async (id) => {
  await ElMessageBox.confirm('确认删除?')
  await api.delete(`/announcements/${id}`)
  load()
}

const view = (row) => {
  Object.assign(current, row)
  showDetail.value = true
}

onMounted(load)
</script>