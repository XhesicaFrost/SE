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
    path: '/seller',
    name: 'sellerHome',
    component: () => import('@/views/SellerViews/SellerHome.vue'),
    meta: { allow: ['seller'] }
  },
  {
    path: '/seller/register',
    name: 'sellerRegister',
    component: () => import('@/views/SellerViews/SellerRegister.vue'),
    meta: { allow: ['seller'] }
  },
  {
    path: '/seller/shop',
    name: 'sellerShop',
    component: () => import('@/views/SellerViews/SellerShop.vue'),
    meta: { allow: ['seller'] }
  },
  {
    path: '/seller/shop/edit',
    name: 'sellerShopEdit',
    component: () => import('@/views/SellerViews/SellerShopEdit.vue'),
    meta: { allow: ['seller'] }
  },
  {
    path: '/seller/item/register',
    name: 'sellerItemRegister',
    component: () => import('@/views/SellerViews/SellerItemRegister.vue'),
    meta: { allow: ['seller'] }
  },
  {
    path: '/seller/item/:id',
    name: 'sellerItemEdit',
    component: () => import('@/views/SellerViews/SellerItemEdit.vue'),
    meta: { allow: ['seller'] }
  },
  {
    path: '/seller/order',
    name: 'sellerOrder',
    component: () => import('@/views/SellerViews/SellerOrder.vue'),
    meta: { allow: ['seller'] }
  },
  {
    path:'/seller/Approval',
    name:'sellerApproval',
    component: () => import('@/views/SellerViews/SellerApproval.vue'),
    meta: { allow: ['seller'] }
  },
  {
    path:'/seller/promotion',
    name:'sellerPromotion',
    component: () => import('@/views/SellerViews/SellerPromotion.vue'),
    meta: { allow: ['seller'] }
  },
  {
    path:'/seller/promotion/:id',
    name:'sellerPromotionEdit',
    component: () => import('@/views/SellerViews/SellerPromotionEdit.vue'),
    meta: { allow: ['seller'] }
  },
  {
    path:'/seller/promotion/register',
    name:'sellerPromotionRegister',
    component: () => import('@/views/SellerViews/SellerPromotionRegister.vue'),
    meta: { allow: ['seller'] }
  },
  {
    path:'/seller/data',
    name:'sellerData',
    component: () => import('@/views/SellerViews/SellerData.vue'),
    meta: { allow: ['seller'] }
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