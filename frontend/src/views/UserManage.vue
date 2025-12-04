<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">用户与权限管理</h2>
      <el-button type="primary" icon="Plus" @click="openCreate">新增用户</el-button>
    </div>

    <div class="filter-container">
      <el-radio-group v-model="query.role" @change="load">
        <el-radio-button label="">全部角色</el-radio-button>
        <el-radio-button label="ADMIN">管理员</el-radio-button>
        <el-radio-button label="VOLUNTEER">志愿者</el-radio-button>
        <el-radio-button label="FAMILY">家属</el-radio-button>
      </el-radio-group>
    </div>

    <div class="pro-card no-padding">
      <el-table :data="list.records" style="width: 100%">
        <el-table-column prop="username" label="账号" width="150" />
        <el-table-column prop="realName" label="姓名" width="150">
          <template #default="scope">
            <div class="flex-center" style="justify-content: flex-start">
              <el-avatar :size="24" style="background:#87d068; margin-right: 8px;">{{ scope.row.realName?.charAt(0) }}</el-avatar>
              {{ scope.row.realName }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="role" label="角色">
           <template #default="scope">
              <el-tag :type="getRoleType(scope.row.role)">{{ formatRole(scope.row.role) }}</el-tag>
           </template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="organization" label="所属机构" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
           <template #default="scope">
              <span :style="{color: scope.row.status === 'NORMAL' ? '#52c41a' : '#ff4d4f'}">
                 ● {{ scope.row.status === 'NORMAL' ? '正常' : '禁用' }}
              </span>
           </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" @click="disable(scope.row.id)" v-if="scope.row.status === 'NORMAL'">禁用</el-button>
            <el-button link type="success" @click="enable(scope.row.id)" v-else>启用</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="padding: 16px; display: flex; justify-content: flex-end;">
        <el-pagination layout="prev, pager, next" :total="list.total" :page-size="query.size" @current-change="(p)=>{query.page=p;load()}" background />
      </div>
    </div>

    <el-dialog v-model="dialog.visible" :title="dialog.isEdit ? '编辑用户' : '新增用户'" width="480px">
      <el-form :model="dialog.form" label-width="80px">
        <el-form-item label="账号" v-if="!dialog.isEdit"><el-input v-model="dialog.form.username" /></el-form-item>
        <el-form-item label="密码" v-if="!dialog.isEdit"><el-input type="password" v-model="dialog.form.password" show-password placeholder="默认 123456" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="dialog.form.realName" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="dialog.form.phone" /></el-form-item>
        <el-form-item label="角色">
           <el-select v-model="dialog.form.role" style="width:100%">
              <el-option label="管理员" value="ADMIN" />
              <el-option label="志愿者" value="VOLUNTEER" />
              <el-option label="家属" value="FAMILY" />
           </el-select>
        </el-form-item>
        <template v-if="dialog.form.role === 'VOLUNTEER'">
           <el-form-item label="机构"><el-input v-model="dialog.form.organization" /></el-form-item>
           <el-form-item label="备注"><el-input v-model="dialog.form.remark" /></el-form-item>
        </template>
      </el-form>
      <template #footer>
         <el-button @click="dialog.visible=false">取消</el-button>
         <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import api from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const query = reactive({ page: 1, size: 10, role: '' })
const list = reactive({ records: [], total: 0 })
const dialog = reactive({ visible: false, isEdit: false, form: {} })

const load = async () => {
  const res = await api.get('/users', { params: { page: query.page - 1, size: query.size, role: query.role } })
  if (res.code === 0) {
    list.records = res.data.content
    list.total = res.data.totalElements
  }
}

const openCreate = () => {
  dialog.isEdit = false
  dialog.form = { username: '', password: '123456', realName: '', phone: '', role: 'VOLUNTEER', status: 'NORMAL' }
  dialog.visible = true
}

const openEdit = (row) => {
  dialog.isEdit = true
  dialog.form = { ...row }
  dialog.visible = true
}

const submit = async () => {
  if (dialog.isEdit) await api.put(`/users/${dialog.form.id}`, dialog.form)
  else await api.post('/users', dialog.form)
  ElMessage.success('保存成功')
  dialog.visible = false
  load()
}

const disable = async (id) => {
  await ElMessageBox.confirm('确认禁用?')
  await api.patch(`/users/${id}/disable`)
  load()
}

const enable = async (id) => { // Assuming API supports specific enable or just toggle status manually via update, but simplified here
  ElMessage.info('暂未实现启用接口')
}

const getRoleType = (role) => {
  if (role === 'ADMIN') return 'danger'
  if (role === 'VOLUNTEER') return 'primary'
  return 'info'
}

const formatRole = (role) => ({ ADMIN: '管理员', VOLUNTEER: '志愿者', FAMILY: '家属' }[role] || role)

onMounted(load)
</script>