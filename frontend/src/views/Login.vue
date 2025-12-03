<template>
  <div style="display:flex;justify-content:center;align-items:center;height:100vh;background:#f5f6f7">
    <el-card style="width:360px">
      <h3>关爱空巢老人管理系统</h3>
      <el-form :model="form" @submit.prevent="onSubmit">
        <el-form-item label="账号">
          <el-input v-model="form.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-button type="primary" style="width:100%" @click="onSubmit">登录</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const form = reactive({ username: 'admin', password: '123456' })

const onSubmit = async () => {
  const res = await api.post('/auth/login', form)
  if (res.code === 0) {
    localStorage.setItem('token', res.data.token)
    ElMessage.success('登录成功')
    router.push('/')
  }
}
</script>
