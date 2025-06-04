import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store'
import { debug_AuthCheck, BASE_URL, fetchWithTimeout } from '@/config'

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
  {
    path: '/error',
    name: 'ErrorView',
    component: () => import('@/ErrorView.vue')
  },
  {
    path: '/user',
    name: 'UserHome',
    component: () => import('@/views/UserViews/UserHome.vue'),
    meta: { allow: ['user'] }
  },
  {
    path: '/user/search',
    name: 'UserSearch',
    component: () => import('@/views/UserViews/UserSearch.vue'),
    meta: { allow: ['user'] }
  },
  {
    path: '/user/shopping/:shopId',
    name: 'UserShopInfo',
    component: () => import('@/views/UserViews/UserShopInfo.vue'),
    meta: { allow: ['user'] },
    props: true
  },
  {
    path: '/user/shopcart',
    name: 'UserShopCart',
    component: () => import('@/views/UserViews/UserShopCart.vue'),
    meta: { allow: ['user'] }
  },
  {
    path: '/user/personal',
    name: 'UserPersonal',
    component: () => import('@/views/UserViews/UserPersonal.vue'),
    meta: { allow: ['user'] }
  },
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
    path: '/admin/shops',
    name: 'AdminShopManage',
    component: () => import('@/views/AdminViews/AdminShopManage.vue'),
    meta: { allow: ['admin'] }
  },
  {
    path: '/admin/items',
    name: 'AdminItemManage',
    component: () => import('@/views/AdminViews/AdminItemManage.vue'),
    meta: { allow: ['admin'] }
  },
  {
    path: '/admin/orders',
    name: 'AdminOrderManage',
    component: () => import('@/views/AdminViews/AdminOrderManage.vue'),
    meta: { allow: ['admin'] }
  },
  {
    path: '/',
    redirect: '/login'
  },
  // 404 路由匹配 - 必须放在最后
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    redirect: '/error'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 从后端获取用户类型的函数
async function getUserKindFromBackend(userId) {
  try {
    if (!userId) {
      return null
    }
    
    const params = new URLSearchParams({ userId }).toString()
    const response = await fetchWithTimeout(`${BASE_URL}/user/kind?${params}`)
    const result = await response.json()
    
    if (result.success && result.data) {
      return result.data.userKind
    } else {
      console.error('获取用户类型失败:', result.message)
      return null
    }
  } catch (error) {
    console.error('请求用户类型失败:', error)
    return null
  }
}

// 全局前置守卫
// 进行跳转的权限检查
router.beforeEach(async (to, from, next) => {
  if (debug_AuthCheck == false) {
    next()
    return
  }
  
  const allow = to.meta.allow
  
  // 如果路由不需要权限检查，直接通过
  if (!allow || allow.length === 0) {
    next()
    return
  }
  
  try {
    // 从store获取userId
    const userId = store.state.userStore.userInfo.userId
    
    if (!userId) {
      console.warn('用户未登录，跳转到错误页面')
      next('/error')
      return
    }
    
    // 从后端获取用户类型
    const userKind = await getUserKindFromBackend(userId)
    
    if (userKind) {
      // 更新store中的userKind（保持同步）
      store.commit('userStore/SET_USER_INFO', {
        ...store.state.userStore.userInfo,
        userKind: userKind
      })
      
      // 检查权限
      if (allow.includes(userKind)) {
        next() // 有权限，继续访问
      } else {
        console.warn(`用户类型 ${userKind} 无权限访问 ${to.path}`)
        next('/error') // 无权限，跳转到错误页面
      }
    } else {
      // 获取用户类型失败
      console.warn('无法获取用户类型，跳转到错误页面')
      next('/error')
    }
  } catch (error) {
    console.error('权限验证失败:', error)
    next('/error') // 验证失败，跳转到错误页面
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