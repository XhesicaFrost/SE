import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import { setStoreInstance } from './config.js'


setStoreInstance(store)

const app = createApp(App)
app.use(store)
app.use(router)
app.mount('#app')


setTimeout(() => {
  try {
    store.dispatch('userStore/restoreFromStorage')
    console.log('🔄 手动恢复持久化数据完成')
  } catch (error) {
    console.error('手动恢复失败，插件会自动处理:', error)
  }
}, 100) // 延迟一点确保应用完全初始化