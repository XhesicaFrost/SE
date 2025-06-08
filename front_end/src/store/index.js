import { createStore } from 'vuex'
import createPersistedState from 'vuex-persistedstate'
import userStore from './modules/userStore'
import sellerStore from './modules/sellerStore'
import riderStore from './modules/riderStore'
import locationStore from './modules/locationStore'
const store = createStore({
  modules: {
    userStore,
    sellerStore,
    riderStore,
    locationStore
  },
  plugins: [
    // ✅ 添加插件作为备份持久化方案
    createPersistedState({
      // 使用不同的存储键，避免与原有代码冲突
      key: 'eleme-backup-state',
      paths: [
        'userStore.userInfo',
        'userStore.errorMessage'
      ],
      storage: window.localStorage,
      // ✅ 只在原有持久化失败时作为备份
    })
  ]
})

export default store