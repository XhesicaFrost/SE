import { createStore } from 'vuex'
import userStore from './modules/userStore'
import merchantStore from './modules/merchantStore'
export default createStore({
  modules: {
    userStore,
    merchantStore
  }
})