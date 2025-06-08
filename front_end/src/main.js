import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import { setStoreInstance } from './config.js'

console.log('🚀 开始初始化应用')

// 设置 store 实例
setStoreInstance(store)

// 创建应用
const app = createApp(App)
app.use(store)
app.use(router)
app.mount('#app')

console.log('✅ 应用挂载成功')

// ✅ 添加：在应用挂载后恢复数据
setTimeout(() => {
  try {
    // 调用原有的恢复方法
    store.dispatch('userStore/restoreFromStorage')
    console.log('🔄 手动恢复持久化数据完成')
  } catch (error) {
    console.error('❌ 手动恢复失败，插件会自动处理:', error)
  }
}, 100) // 延迟一点确保应用完全初始化

// ✅ 简单的错误处理
app.config.errorHandler = (err, instance, info) => {
  console.error('❌ Vue应用错误:', err, info)
}