import { shallowMount, mount } from '@vue/test-utils'
import { createStore } from 'vuex'
import SellerHome from '@/views/SellerViews/SellerHome.vue'

// stub 子组件
jest.mock('@/components/bottomNav.vue', () => ({
  default: { name: 'BottomNav', render: () => null }
}))

// 拦截配置与网络
jest.mock('@/config.js', () => ({
  BASE_URL: 'http://localhost:12345',
  debug_seller_created: false,
  fetchWithTimeout: jest.fn()
}))
import { fetchWithTimeout } from '@/config.js'

describe('SellerHome.vue', () => {
  let store
  let routerMock

  beforeAll(() => {
    // window 确认与提示
    if (!global.confirm) global.confirm = () => true
    if (!global.alert) global.alert = () => {}
    jest.spyOn(console, 'log').mockImplementation(() => {})
  })

  beforeEach(() => {
    jest.clearAllMocks()
    routerMock = { push: jest.fn() }
    store = createStore({
      modules: {
        userStore: {
          namespaced: true,
          state: () => ({ userInfo: { userId: 'user-001' } }),
          actions: {
            logout: jest.fn().mockResolvedValue(undefined)
          }
        },
        sellerStore: {
          namespaced: true,
          state: () => ({ sellerId: '', sellerName: '' }),
          mutations: {
            SET_seller_ID(state, id) { state.sellerId = id },
            SET_seller_NAME(state, name) { state.sellerName = name }
          }
        }
      }
    })
    // 避免 mounted 的 fetchsellerInfo 失败
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200, data: {} }) })
  })

  // goTo
  it('goTo 正向：logout 确认后调用 logout 并跳转 /login', async () => {
    jest.spyOn(window, 'confirm').mockReturnValue(true)
    const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {})

    const wrapper = shallowMount(SellerHome, {
      global: { plugins: [store], mocks: { $router: routerMock } }
    })

    const commitSpy = jest.spyOn(store, 'commit')
    await wrapper.vm.goTo('logout')

    // 提示与跳转
    expect(alertSpy).toHaveBeenCalled()
    expect(routerMock.push).toHaveBeenCalledWith('/login')
    // 提交了 seller 清空（第三个参数 undefined）
    expect(commitSpy).toHaveBeenCalledWith('sellerStore/SET_seller_ID', '', undefined)
    expect(commitSpy).toHaveBeenCalledWith('sellerStore/SET_seller_NAME', '', undefined)
  })

  it('goTo 反向：logout 取消则不登出且不跳转 /login', async () => {
    jest.spyOn(window, 'confirm').mockReturnValue(false)
    const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {})

    const wrapper = shallowMount(SellerHome, {
      global: { plugins: [store], mocks: { $router: routerMock } }
    })

    await wrapper.vm.goTo('logout')
    expect(alertSpy).not.toHaveBeenCalled()
    expect(routerMock.push).not.toHaveBeenCalledWith('/login')
  })

  // fetchsellerHomeData
  it('fetchsellerHomeData 正向：code=200 时填充首页数据', async () => {
    const wrapper = mount(SellerHome, {
      global: { plugins: [store], mocks: { $router: routerMock, $toast: jest.fn() } }
    })

    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({
      json: async () => ({
        code: 200,
        data: {
          todayRevenue: 123,
          todayOrderCount: 7,
          latestComments: [{ username: 'u', content: 'c' }]
        }
      })
    })

    await wrapper.vm.fetchsellerHomeData('seller-001')
    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(wrapper.vm.todayRevenue).toBe(123)
    expect(wrapper.vm.todayOrderCount).toBe(7)
    expect(wrapper.vm.latestComments).toHaveLength(1)
  })

  it('fetchsellerHomeData 反向：非 200 时提示 toast', async () => {
    const toast = jest.fn()
    const wrapper = mount(SellerHome, {
      global: { plugins: [store], mocks: { $router: routerMock, $toast: toast } }
    })

    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 500, message: 'bad' }) })

    await wrapper.vm.fetchsellerHomeData('seller-001')
    expect(toast).toHaveBeenCalled()
  })

  // fetchsellerInfo
  it('fetchsellerInfo 正向：正常状态设置导航并拉取首页数据', async () => {
    const wrapper = mount(SellerHome, {
      global: { plugins: [store], mocks: { $router: routerMock, $toast: jest.fn() } }
    })
    // 忽略 mounted 的自动一次
    fetchWithTimeout.mockClear()

    // mock 返回正常状态
    fetchWithTimeout.mockResolvedValue({
      json: async () => ({
        code: 200,
        data: { sellerId: 'seller-001', sellerName: 'S', sellerStatus: '正常' }
      })
    })
    // 覆写实例方法为 jest.fn，避免 spyOn 报错
    wrapper.vm.fetchsellerHomeData = jest.fn().mockResolvedValue()

    await wrapper.vm.fetchsellerInfo()

    expect(wrapper.vm.fetchsellerHomeData).toHaveBeenCalledWith('seller-001')
    // 导航包含 4 个功能项
    expect(wrapper.vm.navItems.map(i => i.label)).toEqual(
      expect.arrayContaining(['管理店铺', '管理订单', '查看数据', '退出登录'])
    )
    // Vuex 状态已更新
    expect(store.state.sellerStore.sellerId).toBe('seller-001')
    expect(store.state.sellerStore.sellerName).toBe('S')
  })

  it('fetchsellerInfo 反向：非 200 时调用 toast', async () => {
    const toast = jest.fn()
    const wrapper = mount(SellerHome, {
      global: { plugins: [store], mocks: { $router: routerMock, $toast: toast } }
    })

    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 500, message: 'err' }) })

    await wrapper.vm.fetchsellerInfo()
    expect(toast).toHaveBeenCalled()
  })
})