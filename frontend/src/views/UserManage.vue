<template>
  <div class="panel-card">
    <div class="section-title">用户管理</div>
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="角色">
        <el-select v-model="query.role" placeholder="全部角色" style="width:160px">
          <el-option label="全部" value="" />
          <el-option label="管理员" value="ADMIN" />
          <el-option label="志愿者" value="VOLUNTEER" />
          <el-option label="家属" value="FAMILY" />
        </el-select>
      </el-form-item>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="openCreate">新增用户</el-button>
    </el-form>

    <el-table :data="list.records" border style="width:100%" class="table-actions">
      <el-table-column prop="username" label="账号" width="120" />
      <el-table-column prop="realName" label="姓名" />
      <el-table-column prop="phone" label="电话" />
      <el-table-column prop="role" label="角色" />
      <el-table-column prop="status" label="状态" />
      <el-table-column label="操作" width="220">
        <template #default="scope">
          <el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button>
          <el-button link type="danger" @click="disable(scope.row.id)">禁用</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="mt-3"
      layout="prev, pager, next"
      :total="list.total"
      :page-size="query.size"
      @current-change="(p)=>{query.page=p;load()}"
    />

    <el-dialog v-model="dialog.visible" :title="dialog.isEdit ? '编辑用户' : '新增用户'" width="520px">
      <el-form :model="dialog.form" label-width="80px">
        <el-form-item label="账号" v-if="!dialog.isEdit">
          <el-input v-model="dialog.form.username" placeholder="登录账号" />
        </el-form-item>
        <el-form-item label="密码" v-if="!dialog.isEdit">
          <el-input type="password" v-model="dialog.form.password" placeholder="默认密码" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="dialog.form.realName" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="dialog.form.phone" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="dialog.form.role" placeholder="请选择角色">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="志愿者" value="VOLUNTEER" />
            <el-option label="家属" value="FAMILY" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" v-if="dialog.isEdit">
          <el-select v-model="dialog.form.status">
            <el-option label="正常" value="NORMAL" />
            <el-option label="禁用" value="DISABLED" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属机构" v-if="dialog.form.role==='VOLUNTEER'">
          <el-input v-model="dialog.form.organization" placeholder="志愿者机构" />
        </el-form-item>
        <el-form-item label="备注" v-if="dialog.form.role==='VOLUNTEER'">
          <el-input v-model="dialog.form.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible=false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import api from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const query = reactive({ page: 1, size: 10, role: '' })
const list = reactive({ records: [], total: 0 })
const dialog = reactive({ visible: false, isEdit: false, form: { username: '', password: '123456', realName: '', phone: '', role: 'VOLUNTEER', status: 'NORMAL', organization: '', remark: '' } })

const load = async () => {
  const res = await api.get('/users', { params: { page: query.page - 1, size: query.size, role: query.role } })
  if (res.code === 0) {
    list.records = res.data.content
    list.total = res.data.totalElements
  }
}

const openCreate = () => {
  dialog.visible = true
  dialog.isEdit = false
  Object.assign(dialog.form, { username: '', password: '123456', realName: '', phone: '', role: 'VOLUNTEER', status: 'NORMAL', organization: '', remark: '' })
}

const openEdit = (row) => {
  dialog.visible = true
  dialog.isEdit = true
  Object.assign(dialog.form, { ...row, organization: row.organization || '', remark: row.remark || '' })
}

const submit = async () => {
  if (dialog.isEdit) {
    await api.put(`/users/${dialog.form.id}`, dialog.form)
  } else {
    await api.post('/users', dialog.form)
  }
  ElMessage.success('操作成功')
  dialog.visible = false
  load()
}

const disable = async (id) => {
  await ElMessageBox.confirm('确认禁用该用户吗？', '提示', { type: 'warning' })
  await api.patch(`/users/${id}/disable`)
  ElMessage.success('已禁用')
  load()
}

onMounted(load)
</script>
