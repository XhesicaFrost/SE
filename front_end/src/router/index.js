import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store' // 导入vuex store
import { AuthCheck } from '@/config'

const routes = [
  {
    path: '/login',
    name: 'LoginView',
    component: () => import('@/views/LoginView.vue')
  },
  {
    path: '/register',
    name: 'RegisterView',
    component: () => import('@/views/RegisterView.vue')
  },
  // 示例：只有 userKind 为 'user' 才能进入
  {
    path: '/user',
    name: 'UserHome',
    component: () => import('@/views/UserHome.vue'),
    meta: { allow: ['user'] }
  },
  // 示例：只有商家能进入
  {
    path: '/merchant',
    name: 'MerchantHome',
    component: () => import('@/views/MerchantHome.vue'),
    meta: { allow: ['merchant'] }
  },
  {
    path: '/rider',
    name: 'RiderHome',
    component: () => import('@/views/RiderHome.vue'),
    meta: { allow: ['rider'] }
  },
  {
    path: '/',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫
//进行跳转的权限检查
router.beforeEach((to, from, next) => {
    if(AuthCheck==false){
        next()
        return
    }
  const allow = to.meta.allow
  const userKind = store.state.userStore.userInfo.userKind
  if (allow && !allow.includes(userKind)) {
    // 没有权限，跳转到登录或其他页面
    next('/login')
  } else {
    next()
  }
})

export default router