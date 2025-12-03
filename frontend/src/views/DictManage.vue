<template>
  <div>
    <el-form :inline="true" :model="query" style="margin-bottom:10px">
      <el-form-item label="类型"><el-input v-model="query.type" placeholder="living_type" /></el-form-item>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="showAdd=true">新增</el-button>
    </el-form>
    <el-table :data="list">
      <el-table-column prop="dictType" label="类型" />
      <el-table-column prop="dictKey" label="键" />
      <el-table-column prop="dictValue" label="值" />
    </el-table>

    <el-dialog v-model="showAdd" title="新增字典">
      <el-form :model="form">
        <el-form-item label="类型"><el-input v-model="form.dictType" /></el-form-item>
        <el-form-item label="键"><el-input v-model="form.dictKey" /></el-form-item>
        <el-form-item label="值"><el-input v-model="form.dictValue" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAdd=false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import api from '../api'
import { ElMessage } from 'element-plus'

const query = reactive({ type: 'living_type' })
const list = ref([])
const showAdd = ref(false)
const form = reactive({ dictType: 'living_type', dictKey: '', dictValue: '' })

const load = async () => {
  const res = await api.get(`/dicts/${query.type}`)
  if (res.code === 0) {
    list.value = res.data
  }
}

const save = async () => {
  await api.post('/dicts', form)
  ElMessage.success('保存成功')
  showAdd.value = false
  load()
}

load()
</script>
