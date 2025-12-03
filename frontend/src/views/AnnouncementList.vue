<template>
  <div>
    <el-form :inline="true" :model="query" class="mb-3">
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable>
          <el-option label="草稿" value="DRAFT" />
          <el-option label="已发布" value="PUBLISHED" />
          <el-option label="下架" value="DISABLED" />
        </el-select>
      </el-form-item>
      <el-button type="primary" @click="load">筛选</el-button>
      <el-button type="success" @click="openDialog()" style="margin-bottom:10px">发布公告</el-button>
    </el-form>

    <el-table :data="list.records" border>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="type" label="类型" width="120" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column prop="publishTime" label="发布时间" width="180" />
      <el-table-column label="操作" width="240">
        <template #default="scope">
          <el-button link type="primary" @click="view(scope.row)">查看</el-button>
          <el-button link type="primary" @click="openDialog(scope.row)">编辑</el-button>
          <el-button link type="danger" @click="remove(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialog.visible" :title="dialog.form.id ? '编辑公告' : '新增公告'">
      <el-form :model="dialog.form" label-width="100px">
        <el-form-item label="标题"><el-input v-model="dialog.form.title" /></el-form-item>
        <el-form-item label="内容"><el-input type="textarea" v-model="dialog.form.content" /></el-form-item>
        <el-form-item label="类型"><el-input v-model="dialog.form.type" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="dialog.form.status"><el-option label="已发布" value="PUBLISHED" /><el-option label="草稿" value="DRAFT" /><el-option label="下架" value="DISABLED" /></el-select></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible=false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showDetail" title="公告详情">
      <h3>{{ current.title }}</h3>
      <p>{{ current.content }}</p>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import api from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = reactive({ records: [] })
const showDetail = ref(false)
const current = reactive({})
const dialog = reactive({ visible: false, form: {} })
const query = reactive({ status: '' })

const load = async () => {
  const res = await api.get('/announcements', { params: { status: query.status } })
  if (res.code === 0) {
    list.records = res.data.content
  }
}

const openDialog = (row = null) => {
  dialog.visible = true
  dialog.form = row ? { ...row } : { title: '', content: '', type: '活动通知', status: 'PUBLISHED' }
}

const save = async () => {
  if (dialog.form.id) {
    await api.put(`/announcements/${dialog.form.id}`, dialog.form)
  } else {
    await api.post('/announcements', dialog.form)
  }
  ElMessage.success('已保存')
  dialog.visible = false
  load()
}

const view = (row) => {
  Object.assign(current, row)
  showDetail.value = true
}

const remove = async (id) => {
  await ElMessageBox.confirm('确认删除该公告吗？', '提示', { type: 'warning' })
  await api.delete(`/announcements/${id}`)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>
