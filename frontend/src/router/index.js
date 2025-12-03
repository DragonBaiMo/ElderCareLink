import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Layout from '../views/Layout.vue'
import Dashboard from '../views/Dashboard.vue'
import ElderList from '../views/ElderList.vue'
import HealthRecords from '../views/HealthRecords.vue'
import VisitRecords from '../views/VisitRecords.vue'
import AnnouncementList from '../views/AnnouncementList.vue'
import DictManage from '../views/DictManage.vue'
import UserManage from '../views/UserManage.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: Login },
    {
      path: '/',
      component: Layout,
      children: [
        { path: '', component: Dashboard },
        { path: 'elders', component: ElderList },
        { path: 'health', component: HealthRecords },
        { path: 'visits', component: VisitRecords },
        { path: 'announcements', component: AnnouncementList },
        { path: 'dicts', component: DictManage },
        { path: 'users', component: UserManage }
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
