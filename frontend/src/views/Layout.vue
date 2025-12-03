<template>
  <el-container style="height: 100vh">
    <el-aside width="200px" style="background:#1f2d3d;color:#fff">
      <div style="padding:20px;font-weight:bold">关爱空巢老人</div>
      <el-menu :default-active="active" background-color="#1f2d3d" text-color="#fff" router>
        <el-menu-item index="/">仪表盘</el-menu-item>
        <el-menu-item index="/elders">老人信息</el-menu-item>
        <el-menu-item index="/health">健康记录</el-menu-item>
        <el-menu-item index="/visits">探访记录</el-menu-item>
        <el-menu-item index="/announcements">公告管理</el-menu-item>
        <el-menu-item index="/dicts">数据字典</el-menu-item>
        <el-menu-item index="/users">用户管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="display:flex;justify-content:space-between;align-items:center">
        <div>毕业设计演示环境</div>
        <div>
          <el-button type="text" @click="logout">退出登录</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const active = computed(() => route.path)

const logout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>
