import { mount, shallowMount } from '@vue/test-utils'
import { createStore } from 'vuex'

// stub 组件
jest.mock('@/components/topNav.vue', () => ({
  default: { name: 'TopNav', render: () => null }
}))

// mock 配置与网络
jest.mock('@/config.js', () => ({
  BASE_URL: 'http://localhost:12345',
  fetchWithTimeout: jest.fn()
}))
import { fetchWithTimeout } from '@/config.js'

describe('UserHistory.vue', () => {
  let store
  let routerMock

  beforeAll(() => {
    jest.spyOn(console, 'error').mockImplementation(() => {})
  })

  beforeEach(() => {
    jest.clearAllMocks()
    routerMock = { push: jest.fn() }
    store = createStore({
      modules: { userStore: { namespaced: true, state: () => ({ userInfo: { userId: 'u-1' } }) } }
    })
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200, data: [] }) })
  })

  it('getImageUrl 正向：base64 直接返回', async () => {
    const comp = (await import('@/views/UserViews/UserHistory.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })
    const b64 = 'data:image/png;base64,xyz'
    expect(wrapper.vm.getImageUrl(b64)).toBe(b64)
  })

  it('getImageUrl 反向：空返回空', async () => {
    const comp = (await import('@/views/UserViews/UserHistory.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })
    expect(wrapper.vm.getImageUrl('')).toBe('')
  })

  it('fetchOrderItems 正向：200 且数组填充 groupedOrderItems', async () => {
    const comp = (await import('@/views/UserViews/UserHistory.vue')).default
    const wrapper = mount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })
    fetchWithTimeout.mockClear()
    const data = [{ id: 'g1', shop: { image: '', name: 'A', address: 'addr' }, state: 'completed', items: [] }]
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200, data }) })
    await wrapper.vm.fetchOrderItems()
    expect(wrapper.vm.groupedOrderItems).toEqual(data)
    expect(wrapper.vm.totalOrders).toBe(1)
  })

  it('fetchOrderItems 反向：非 200 清空', async () => {
    const comp = (await import('@/views/UserViews/UserHistory.vue')).default
    const wrapper = mount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 500 }) })
    await wrapper.vm.fetchOrderItems()
    expect(wrapper.vm.groupedOrderItems).toEqual([])
  })

  it('calculateShopTotal 正向：正确累计金额', async () => {
    const comp = (await import('@/views/UserViews/UserHistory.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store] } })
    const total = wrapper.vm.calculateShopTotal([
      { product: { price: 10 }, quantity: 2 },
      { product: { price: 3.5 }, quantity: 1 }
    ])
    expect(total).toBe(23.5)
  })

  it('calculateShopTotal 反向：空数组返回 0', async () => {
    const comp = (await import('@/views/UserViews/UserHistory.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store] } })
    expect(wrapper.vm.calculateShopTotal([])).toBe(0)
  })
})