import { createStore } from 'vuex'
import userStore from './modules/userStore'
import sellerStore from './modules/sellerStore'
import riderStore from './modules/riderStore'
export default createStore({
  modules: {
    userStore,
    sellerStore,
    riderStore,
  }
})