import { shallowMount, mount } from '@vue/test-utils'
import { createStore } from 'vuex'

// stub
jest.mock('@/components/bottomNav.vue', () => ({
  default: { name: 'BottomNav', render: () => null }
}))

jest.mock('@/config.js', () => ({
  BASE_URL: 'http://localhost:12345',
  fetchWithTimeout: jest.fn()
}))
import { fetchWithTimeout } from '@/config.js'

describe('UserShopCart.vue', () => {
  let store
  let routerMock

  beforeAll(() => {
    jest.spyOn(window, 'alert').mockImplementation(() => {})
    jest.spyOn(console, 'error').mockImplementation(() => {})
  })

  beforeEach(() => {
    jest.clearAllMocks()
    store = createStore({
      modules: { userStore: { namespaced: true, state: () => ({ userInfo: { userId: 'u-1' } }) } }
    })
    routerMock = { push: jest.fn() }
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200, data: [] }) })
  })

  it('getImageUrl 正向：返回 base64 dataURL', async () => {
    const comp = (await import('@/views/UserViews/UserShopCart.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })
    expect(wrapper.vm.getImageUrl('img')).toBe('data:image/jpeg;base64,img')
  })

  it('getImageUrl 反向：空返回空', async () => {
    const comp = (await import('@/views/UserViews/UserShopCart.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })
    expect(wrapper.vm.getImageUrl('')).toBe('')
  })

  it('fetchCartItems 正向：200 且数组填充 groupedCartItems', async () => {
    const comp = (await import('@/views/UserViews/UserShopCart.vue')).default
    const wrapper = mount(comp, { global: { plugins: [store] } })
    fetchWithTimeout.mockClear()
    const data = [{ shop: { id: 1 }, items: [] }]
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200, data }) })
    await wrapper.vm.fetchCartItems()
    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(wrapper.vm.groupedCartItems).toEqual(data)
  })

  it('fetchCartItems 反向：无 userId 置空且不请求', async () => {
    const badStore = createStore({ modules: { userStore: { namespaced: true, state: () => ({ userInfo: { userId: '' } }) } } })
    const comp = (await import('@/views/UserViews/UserShopCart.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [badStore], mocks: { $router: routerMock } } })
    fetchWithTimeout.mockClear()
    await wrapper.vm.fetchCartItems()
    expect(fetchWithTimeout).not.toHaveBeenCalled()
    expect(wrapper.vm.groupedCartItems).toEqual([])
  })

  it('submitCartItems 正向：success 刷新列表', async () => {
    const comp = (await import('@/views/UserViews/UserShopCart.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store] } })
    wrapper.vm.fetchCartItems = jest.fn().mockResolvedValue()
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ success: true }) })
    await wrapper.vm.submitCartItems(3, 9, 1)
    expect(fetchWithTimeout).toHaveBeenCalled()
    expect(fetchWithTimeout.mock.calls[0][1].method).toBe('POST')
    expect(wrapper.vm.fetchCartItems).toHaveBeenCalled()
  })

  it('submitCartItems 反向：后端失败弹窗', async () => {
    const comp = (await import('@/views/UserViews/UserShopCart.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store] } })
    wrapper.vm.fetchCartItems = jest.fn()
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ success: false }) })
    await wrapper.vm.submitCartItems(3, 9, -1)
    expect(window.alert).toHaveBeenCalledWith('购物车修改失败，请重试！')
    expect(wrapper.vm.fetchCartItems).not.toHaveBeenCalled()
  })

  it('updateQuantity 正向：调用提交与刷新', async () => {
    const comp = (await import('@/views/UserViews/UserShopCart.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })
    wrapper.vm.submitCartItems = jest.fn()
    wrapper.vm.fetchCartItems = jest.fn()
    const item = { product: { id: 10 } }
    wrapper.vm.updateQuantity(2, item, 1)
    expect(wrapper.vm.submitCartItems).toHaveBeenCalledWith(2, 10, 1)
    expect(wrapper.vm.fetchCartItems).toHaveBeenCalled()
  })

  it('updateQuantity 反向：减少数量同样调用', async () => {
    const comp = (await import('@/views/UserViews/UserShopCart.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })
    wrapper.vm.submitCartItems = jest.fn()
    wrapper.vm.fetchCartItems = jest.fn()
    const item = { product: { id: 10 } }
    wrapper.vm.updateQuantity(2, item, -1)
    expect(wrapper.vm.submitCartItems).toHaveBeenCalledWith(2, 10, -1)
    expect(wrapper.vm.fetchCartItems).toHaveBeenCalled()
  })

  it('calculateShopTotal 正向：累加金额', async () => {
    const comp = (await import('@/views/UserViews/UserShopCart.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })
    const total = wrapper.vm.calculateShopTotal([{ product: { price: 2 }, quantity: 3 }, { product: { price: 1.5 }, quantity: 2 }])
    expect(total).toBe(9)
  })

  it('calculateShopTotal 反向：空数组为 0', async () => {
    const comp = (await import('@/views/UserViews/UserShopCart.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })
    expect(wrapper.vm.calculateShopTotal([])).toBe(0)
  })

  it('goToCheckout 正向：跳转进店结算', async () => {
    const comp = (await import('@/views/UserViews/UserShopCart.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })
    wrapper.vm.goToCheckout(8)
    expect(routerMock.push).toHaveBeenCalledWith('/user/shopping/8')
  })

  it('mounted 正向：有 userId 会触发拉取', async () => {
    const comp = (await import('@/views/UserViews/UserShopCart.vue')).default
    fetchWithTimeout.mockClear()
    shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })
    expect(fetchWithTimeout.mock.calls.length).toBeGreaterThanOrEqual(1)
  })

  it('mounted 反向：无 userId 重定向登录', async () => {
    const badStore = createStore({ modules: { userStore: { namespaced: true, state: () => ({ userInfo: { userId: '' } }) } } })
    const comp = (await import('@/views/UserViews/UserShopCart.vue')).default
    shallowMount(comp, { global: { plugins: [badStore], mocks: { $router: routerMock } } })
    expect(routerMock.push).toHaveBeenCalledWith('/login')
  })
})