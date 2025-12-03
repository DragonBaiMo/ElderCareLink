<template>
  <el-container class="layout-shell">
    <el-aside width="220px" class="layout-aside">
      <div class="brand">关爱空巢老人</div>
      <el-menu :default-active="active" background-color="transparent" text-color="#e8f4ff" router class="menu-panel">
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
      <el-header class="layout-header">
        <div class="header-title">毕业设计演示环境</div>
        <div class="header-actions">
          <el-tag type="success" effect="dark">在线</el-tag>
          <el-button type="primary" link @click="logout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="main-panel">
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

<style scoped>
.layout-shell {
  height: 100vh;
  background: transparent;
}

.layout-aside {
  background: linear-gradient(180deg, rgba(15, 76, 117, 0.95), rgba(27, 38, 44, 0.95));
  color: #fff;
  box-shadow: 6px 0 18px rgba(0, 0, 0, 0.08);
}

.brand {
  padding: 22px;
  font-weight: 700;
  font-size: 18px;
  letter-spacing: 1px;
}

.menu-panel {
  border-right: none;
}

.layout-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 22px;
  background: #f7f9fc;
  border-bottom: 1px solid #e5e9f2;
}

.header-title {
  font-weight: 600;
  color: #1f2d3d;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>
