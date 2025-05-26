import { createStore } from 'vuex'
import userStore from './modules/userStore'
import merchantStore from './modules/merchantStore'
import riderStore from './modules/riderStore'
export default createStore({
  modules: {
    userStore,
    merchantStore,
    riderStore,
    userStore,
  }
})