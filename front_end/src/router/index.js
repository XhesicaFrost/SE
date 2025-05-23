import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store' // 导入vuex store
import { debug_AuthCheck } from '@/config'

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
    component: () => import('@/views/UserViews/UserHome.vue'),
    meta: { allow: ['user'] }
  },
  // 示例：只有商家能进入
  {
    path: '/merchant',
    name: 'MerchantHome',
    component: () => import('@/views/MerchantViews/MerchantHome.vue'),
    meta: { allow: ['merchant'] }
  },
  {
    path: '/merchant/register',
    name: 'MerchantRegister',
    component: () => import('@/views/MerchantViews/MerchantRegister.vue'),
    meta: { allow: ['merchant'] }
  },
  {
    path: '/merchant/shop',
    name: 'MerchantShop',
    component: () => import('@/views/MerchantViews/MerchantShop.vue'),
    meta: { allow: ['merchant'] }
  },
  {
    path: '/merchant/shop/edit',
    name: 'MerchantShopEdit',
    component: () => import('@/views/MerchantViews/MerchantShopEdit.vue'),
    meta: { allow: ['merchant'] }
  },
  {
    path: '/merchant/item/register',
    name: 'MerchantItemRegister',
    component: () => import('@/views/MerchantViews/MerchantItemRegister.vue'),
    meta: { allow: ['merchant'] }
  },
  {
    path: '/merchant/item/:id',
    name: 'MerchantItemEdit',
    component: () => import('@/views/MerchantViews/MerchantItemEdit.vue'),
    meta: { allow: ['merchant'] }
  },
  {
    path: '/merchant/order',
    name: 'MerchantOrder',
    component: () => import('@/views/MerchantViews/MerchantOrder.vue'),
    meta: { allow: ['merchant'] }
  },
  {
    path:'/merchant/Approval',
    name:'MerchantApproval',
    component: () => import('@/views/MerchantViews/MerchantApproval.vue'),
    meta: { allow: ['merchant'] }
  },
  {
    path:'/merchant/promotion',
    name:'MerchantPromotion',
    component: () => import('@/views/MerchantViews/MerchantPromotion.vue'),
    meta: { allow: ['merchant'] }
  },
  {
    path:'/merchant/promotion/:id',
    name:'MerchantPromotionEdit',
    component: () => import('@/views/MerchantViews/MerchantPromotionEdit.vue'),
    meta: { allow: ['merchant'] }
  },
  {
    path:'/merchant/promotion/register',
    name:'MerchantPromotionRegister',
    component: () => import('@/views/MerchantViews/MerchantPromotionRegister.vue'),
    meta: { allow: ['merchant'] }
  },
  {
    path:'/merchant/data',
    name:'MerchantData',
    component: () => import('@/views/MerchantViews/MerchantData.vue'),
    meta: { allow: ['merchant'] }
  },
  {
    path: '/rider',
    name: 'RiderHome',
    component: () => import('@/views/RiderViews/RiderHome.vue'),
    meta: { allow: ['rider'] }
  },
  {
    path: '/rider/orders',
    name: 'RiderOrders',
    component: () => import('@/views/RiderViews/RiderOrdersView.vue'),
    meta: { allow: ['rider'] }
  },
  {
    path: '/rider/history',
    name: 'RiderHistory',
    component: () => import('@/views/RiderViews/RiderHistoryView.vue'),
    meta: { allow: ['rider'] }
  },
  {
    path: '/rider/order/:id',
    name: 'RiderOrderDetail',
    component: () => import('@/views/RiderViews/RiderOrderDetailView.vue'),
    meta: { allow: ['rider'] }
  },
  {
    path: '/admin',
    name: 'AdminHome',
    component: () => import('@/views/AdminViews/AdminHome.vue'),
    meta: { allow: ['admin'] }
  },
  {
    path: '/',
    redirect: '/login'
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫
//进行跳转的权限检查
router.beforeEach((to, from, next) => {
    if(debug_AuthCheck==false){
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
// 当用户离开骑手相关页面时停止位置追踪
router.beforeEach((to, from, next) => {
  if (from.path.startsWith('/rider') && !to.path.startsWith('/rider')) {
    store.dispatch('locationStore/stopLocationTracking')
  }
  next()
})

export default router