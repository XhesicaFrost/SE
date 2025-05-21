import { createStore } from 'vuex'
import userStore from './modules/userStore'

export default createStore({
  modules: {
    userStore
  }
})