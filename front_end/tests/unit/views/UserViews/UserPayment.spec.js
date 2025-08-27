import { mount, shallowMount } from '@vue/test-utils'
import { createStore } from 'vuex'

// stub 子组件
jest.mock('@/components/topNav.vue', () => ({
  default: { name: 'TopNav', render: () => null }
}))

// mock 配置与网络
jest.mock('@/config.js', () => ({
  BASE_URL: 'http://localhost:12345',
  fetchWithTimeout: jest.fn()
}))
import { fetchWithTimeout } from '@/config.js'

describe('UserPayment.vue', () => {
  let store
  let routerMock
  let routeMock

  beforeAll(() => {
    jest.spyOn(console, 'error').mockImplementation(() => {})
    jest.spyOn(console, 'warn').mockImplementation(() => {})
    jest.spyOn(window, 'alert').mockImplementation(() => {})
  })

  beforeEach(() => {
    jest.clearAllMocks()
    localStorage.setItem('token', 't-123')
    store = createStore({
      modules: {
        userStore: {
          namespaced: true,
          state: () => ({ userInfo: { userId: 'u-001' } })
        }
      }
    })
    routerMock = { push: jest.fn(), go: jest.fn() }
    routeMock = { params: { shopId: 's-1' } }
    // 避免 mounted 的三次拉取报错
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200, data: [] }) })
  })

  // getImageUrl
  it('getImageUrl 正向：长串作为 base64 返回 data URL', async () => {
    const comp = (await import('@/views/UserViews/UserPayment.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock, $route: routeMock } } })
    const longStr = 'a'.repeat(120)
    expect(wrapper.vm.getImageUrl(longStr)).toBe(`data:image/png;base64,${longStr}`)
    expect(wrapper.vm.getImageUrl('data:image/png;base64,xyz')).toBe('data:image/png;base64,xyz')
  })

  it('getImageUrl 反向：空字符串返回空', async () => {
    const comp = (await import('@/views/UserViews/UserPayment.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock, $route: routeMock } } })
    expect(wrapper.vm.getImageUrl('')).toBe('')
  })

  // fetchShopInfo
  it('fetchShopInfo 正向：code=200 时设置 shopInfo', async () => {
    const comp = (await import('@/views/UserViews/UserPayment.vue')).default
    const wrapper = mount(comp, { global: { plugins: [store], mocks: { $router: routerMock, $route: routeMock } } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({
      json: async () => ({
        code: 200,
        data: { name: '店A', image: 'IMG', address: 'ADDR', rating: 4.5, monthlySales: 100, deliveryTime: '30min' }
      })
    })
    await wrapper.vm.fetchShopInfo()
    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(wrapper.vm.shopInfo.name).toBe('店A')
    expect(wrapper.vm.shopInfo.address).toBe('ADDR')
  })

  it('fetchShopInfo 反向：非 200 不更新（保持默认）', async () => {
    const comp = (await import('@/views/UserViews/UserPayment.vue')).default
    const wrapper = mount(comp, { global: { plugins: [store], mocks: { $router: routerMock, $route: routeMock } } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 500 }) })
    await wrapper.vm.fetchShopInfo()
    // 组件未写入默认值，因此为 undefined
    expect(wrapper.vm.shopInfo.name).toBeUndefined()
  })

  // fetchCartItems
  it('fetchCartItems 正向：找到对应店铺 items 填充 cart', async () => {
    const comp = (await import('@/views/UserViews/UserPayment.vue')).default
    const wrapper = mount(comp, { global: { plugins: [store], mocks: { $router: routerMock, $route: { params: { shopId: '2' } } } } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({
      json: async () => ({
        code: 200,
        data: [
          { shop: { id: '1' }, items: [{ product: { id: 10, name: 'X', price: 2 }, quantity: 3 }] },
          { shop: { id: '2' }, items: [{ product: { id: 11, name: 'Y', price: 5 }, quantity: 1 }] }
        ]
      })
    })
    await wrapper.vm.fetchCartItems()
    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(wrapper.vm.cart).toEqual([{ product: { id: 11, name: 'Y', price: 5 }, quantity: 1 }])
  })

  it('fetchCartItems 反向：无 userId 直接置空且不请求', async () => {
    const emptyStore = createStore({
      modules: { userStore: { namespaced: true, state: () => ({ userInfo: { userId: '' } }) } }
    })
    const comp = (await import('@/views/UserViews/UserPayment.vue')).default
    const wrapper = mount(comp, { global: { plugins: [emptyStore], mocks: { $router: routerMock, $route: routeMock } } })
    fetchWithTimeout.mockClear()
    await wrapper.vm.fetchCartItems()
    expect(fetchWithTimeout).not.toHaveBeenCalled()
    expect(wrapper.vm.cart).toEqual([])
  })

  // fetchDiscountedPrice
  it('fetchDiscountedPrice 正向：读取当前店铺优惠价与总价', async () => {
    const comp = (await import('@/views/UserViews/UserPayment.vue')).default
    const wrapper = mount(comp, { global: { plugins: [store], mocks: { $router: routerMock, $route: { params: { shopId: '9' } } } } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({
      json: async () => ({
        code: 200,
        data: [{ shop: { id: '9', discountedPrice: 88.8, totalPrice: 100 }, items: [] }]
      })
    })
    await wrapper.vm.fetchDiscountedPrice()
    expect(wrapper.vm.discountedPrice).toBe(88.8)
    expect(wrapper.vm.totalPrice).toBe(100)
  })

  it('fetchDiscountedPrice 反向：非 200 时置空优惠价', async () => {
    const comp = (await import('@/views/UserViews/UserPayment.vue')).default
    const wrapper = mount(comp, { global: { plugins: [store], mocks: { $router: routerMock, $route: routeMock } } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 500 }) })
    await wrapper.vm.fetchDiscountedPrice()
    expect(wrapper.vm.discountedPrice).toBeNull()
  })

  // handlePayment
  it('handlePayment 正向：200 提示成功并跳转历史页', async () => {
    const comp = (await import('@/views/UserViews/UserPayment.vue')).default
    const wrapper = shallowMount(comp, {
      global: { plugins: [store], mocks: { $router: routerMock, $route: routeMock } }
    })
    await wrapper.setData({ cart: [{ product: { id: 1, price: 3 }, quantity: 2 }] })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200 }) })
    await wrapper.vm.handlePayment()
    expect(window.alert).toHaveBeenCalledWith('支付成功！')
    expect(routerMock.push).toHaveBeenCalledWith('/user/history')
  })

  it('handlePayment 反向：非 200 提示失败', async () => {
    const comp = (await import('@/views/UserViews/UserPayment.vue')).default
    const wrapper = shallowMount(comp, {
      global: { plugins: [store], mocks: { $router: routerMock, $route: routeMock } }
    })
    await wrapper.setData({ cart: [] })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 500 }) })
    await wrapper.vm.handlePayment()
    expect(window.alert).toHaveBeenCalledWith('支付失败，请重试！')
  })
})