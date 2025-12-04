import axios from 'axios'
import { ElMessage } from 'element-plus'

const instance = axios.create({
  baseURL: '/api'
})

instance.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

instance.interceptors.response.use(
  (res) => res.data,
  (error) => {
    const msg = error.response?.data?.message || '请求失败'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export default instance
