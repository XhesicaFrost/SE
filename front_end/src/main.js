import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import { setStoreInstance } from './config.js'
setStoreInstance(store)
createApp(App)
  .use(store)
  .use(router)
  .mount('#app')