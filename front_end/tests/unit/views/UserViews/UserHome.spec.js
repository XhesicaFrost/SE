import { mount, shallowMount } from '@vue/test-utils'

// stub 组件
jest.mock('@/components/bottomNav.vue', () => ({
  default: { name: 'BottomNav', render: () => null }
}))

// mock 配置与网络
jest.mock('@/config.js', () => ({
  BASE_URL: 'http://localhost:12345',
  fetchWithTimeout: jest.fn()
}))
import { fetchWithTimeout } from '@/config.js'

describe('UserHome.vue', () => {
  let routerMock

  beforeAll(() => {
    jest.spyOn(window, 'alert').mockImplementation(() => {})
    jest.spyOn(console, 'error').mockImplementation(() => {})
  })

  beforeEach(() => {
    jest.clearAllMocks()
    routerMock = { push: jest.fn() }
    // 默认安全返回
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200, data: [] }) })
  })

  it('getImageUrl 正向：长串转为 data URL', async () => {
    const comp = (await import('@/views/UserViews/UserHome.vue')).default
    const wrapper = shallowMount(comp, { global: { mocks: { $router: routerMock } } })
    expect(wrapper.vm.getImageUrl('a'.repeat(150))).toBe(`data:image/png;base64,${'a'.repeat(150)}`)
  })

  it('getImageUrl 反向：空返回空', async () => {
    const comp = (await import('@/views/UserViews/UserHome.vue')).default
    const wrapper = shallowMount(comp, { global: { mocks: { $router: routerMock } } })
    expect(wrapper.vm.getImageUrl('')).toBe('')
  })

  it('fetchRecommendedShops 正向：token 存在且 200 填充数据', async () => {
    localStorage.setItem('token', 't-1')
    const comp = (await import('@/views/UserViews/UserHome.vue')).default
    const wrapper = mount(comp, { global: { mocks: { $router: routerMock } } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200, data: [{ id: 1, name: 'A' }] }) })
    await wrapper.vm.fetchRecommendedShops()
    expect(wrapper.vm.recommendedShops).toHaveLength(1)
  })

  it('fetchRecommendedShops 反向：无 token 提示并跳转登录', async () => {
    localStorage.removeItem('token')
    const comp = (await import('@/views/UserViews/UserHome.vue')).default
    const wrapper = mount(comp, { global: { mocks: { $router: routerMock } } })
    fetchWithTimeout.mockClear()
    await wrapper.vm.fetchRecommendedShops()
    expect(window.alert).toHaveBeenCalledWith('请先登录')
    expect(routerMock.push).toHaveBeenCalledWith('/login')
    expect(fetchWithTimeout).not.toHaveBeenCalled()
  })

  it('goToShop 正向：跳转到店铺页', async () => {
    const comp = (await import('@/views/UserViews/UserHome.vue')).default
    const wrapper = shallowMount(comp, { global: { mocks: { $router: routerMock } } })
    wrapper.vm.goToShop(9)
    expect(routerMock.push).toHaveBeenCalledWith('/user/shopping/9')
  })

  it('fetchRecommendedShops 反向：非 200 清空并提示失败', async () => {
    localStorage.setItem('token', 't-1')
    const comp = (await import('@/views/UserViews/UserHome.vue')).default
    const wrapper = mount(comp, { global: { mocks: { $router: routerMock } } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 500 }) })
    await wrapper.vm.fetchRecommendedShops()
    expect(wrapper.vm.recommendedShops).toEqual([])
  })
})