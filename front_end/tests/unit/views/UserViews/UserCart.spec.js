import { shallowMount } from '@vue/test-utils'
import { createStore } from 'vuex'

// mock 配置与网络
jest.mock('@/config.js', () => ({
  BASE_URL: 'http://localhost:12345',
  fetchWithTimeout: jest.fn()
}))
import { fetchWithTimeout } from '@/config.js'

describe('UserCart.vue', () => {
  let store
  let routerMock

  beforeAll(() => {
    jest.spyOn(window, 'alert').mockImplementation(() => {})
    jest.spyOn(console, 'error').mockImplementation(() => {})
  })

  beforeEach(() => {
    jest.clearAllMocks()
    localStorage.setItem('token', 't-123')
    routerMock = { push: jest.fn() }
    store = createStore({
      modules: { userStore: { namespaced: true, state: () => ({ userInfo: { userId: '100' } }) } }
    })

    // 关键：把 mock 的常量/方法挂到全局，供组件直接使用
    global.BASE_URL = 'http://localhost:12345'
    global.fetchWithTimeout = fetchWithTimeout
  })

  it('fetchCart 正向：token 和 userId 有效，成功填充 cartItems', async () => {
    const comp = (await import('@/views/UserViews/UserCart.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })

    fetchWithTimeout.mockResolvedValue({
      json: async () => ({ success: true, code: 200, data: [{ id: 1 }] })
    })

    await wrapper.vm.fetchCart()

    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(fetchWithTimeout.mock.calls[0][0]).toBe('http://localhost:12345/shopcart')
    expect(fetchWithTimeout.mock.calls[0][1].headers.Authorization).toBe('Bearer t-123')
    expect(wrapper.vm.cartItems).toEqual([{ id: 1 }])
  })

  it('fetchCart 反向：无 token 提示并跳转登录', async () => {
    localStorage.removeItem('token')
    const comp = (await import('@/views/UserViews/UserCart.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })

    await wrapper.vm.fetchCart()

    expect(window.alert).toHaveBeenCalledWith('请先登录')
    expect(routerMock.push).toHaveBeenCalledWith('/login')
    expect(fetchWithTimeout).not.toHaveBeenCalled()
  })

  it('fetchCart 反向：userId 无效提示并跳转登录', async () => {
    const badStore = createStore({
      modules: { userStore: { namespaced: true, state: () => ({ userInfo: { userId: 'abc' } }) } }
    })
    const comp = (await import('@/views/UserViews/UserCart.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [badStore], mocks: { $router: routerMock } } })

    await wrapper.vm.fetchCart()

    expect(window.alert).toHaveBeenCalledWith('用户信息无效')
    expect(routerMock.push).toHaveBeenCalledWith('/login')
    expect(fetchWithTimeout).not.toHaveBeenCalled()
  })

  it('fetchCart 反向：接口失败弹窗', async () => {
    const comp = (await import('@/views/UserViews/UserCart.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })

    fetchWithTimeout.mockResolvedValue({
      json: async () => ({ success: false, code: 500 })
    })

    await wrapper.vm.fetchCart()

    expect(window.alert).toHaveBeenCalledWith('获取购物车信息失败')
  })
})