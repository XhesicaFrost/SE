import { shallowMount, mount } from '@vue/test-utils'

// stubs
jest.mock('@/components/bottomNav.vue', () => ({ default: { name: 'BottomNav', render: () => null } }))
jest.mock('@/components/topNav.vue', () => ({ default: { name: 'TopNav', render: () => null } }))

jest.mock('@/config.js', () => ({
  BASE_URL: 'http://localhost:12345',
  fetchWithTimeout: jest.fn()
}))
import { fetchWithTimeout } from '@/config.js'

describe('UserSearch.vue', () => {
  let routerMock

  beforeAll(() => {
    jest.spyOn(console, 'log').mockImplementation(() => {})
  })

  beforeEach(() => {
    jest.clearAllMocks()
    routerMock = { push: jest.fn() }
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200, data: [] }) })
    jest.useFakeTimers()
  })

  afterEach(() => {
    jest.runOnlyPendingTimers()
    jest.useRealTimers()
  })

  it('getImageUrl 正向：长串转为 dataURL', async () => {
    const comp = (await import('@/views/UserViews/UserSearch.vue')).default
    const wrapper = shallowMount(comp)
    const s = 'a'.repeat(150)
    expect(wrapper.vm.getImageUrl(s)).toBe(`data:image/png;base64,${s}`)
  })

  it('getImageUrl 反向：空返回空', async () => {
    const comp = (await import('@/views/UserViews/UserSearch.vue')).default
    const wrapper = shallowMount(comp)
    expect(wrapper.vm.getImageUrl('')).toBe('')
  })

  it('setFilter 正向：设置筛选', async () => {
    const comp = (await import('@/views/UserViews/UserSearch.vue')).default
    const wrapper = shallowMount(comp)
    wrapper.vm.setFilter('price')
    expect(wrapper.vm.activeFilter).toBe('price')
  })

  it('fetchShops 正向：200 填充 allShops', async () => {
    const comp = (await import('@/views/UserViews/UserSearch.vue')).default
    const wrapper = mount(comp)
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200, data: [{ id: 1, name: '店A', tags: [] }] }) })
    await wrapper.vm.fetchShops()
    expect(wrapper.vm.allShops).toHaveLength(1)
  })

  it('fetchShops 反向：非 200 清空', async () => {
    const comp = (await import('@/views/UserViews/UserSearch.vue')).default
    const wrapper = mount(comp)
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 500 }) })
    await wrapper.vm.fetchShops()
    expect(wrapper.vm.allShops).toEqual([])
  })

  it('performSearch 正向：按关键词过滤（异步）', async () => {
    const comp = (await import('@/views/UserViews/UserSearch.vue')).default
    const wrapper = shallowMount(comp, { global: { mocks: { $router: routerMock } } })
    await wrapper.setData({
      allShops: [
        { id: 1, name: '奶茶店', tags: ['饮品'], image: '', rating: 4.5, avgPrice: 10, distance: 1, deliveryTime: 30, products: [] },
        { id: 2, name: '咖啡屋', tags: ['咖啡'], image: '', rating: 4.2, avgPrice: 20, distance: 2, deliveryTime: 25, products: [] }
      ]
    })
    wrapper.vm.performSearch('咖啡')
    expect(wrapper.vm.isLoading).toBe(true)
    jest.advanceTimersByTime(800)
    expect(wrapper.vm.isLoading).toBe(false)
    expect(wrapper.vm.searchResults).toHaveLength(1)
    expect(wrapper.vm.searchResults[0].id).toBe(2)
  })

  it('performSearch 反向：空白关键字不触发搜索', async () => {
    const comp = (await import('@/views/UserViews/UserSearch.vue')).default
    const wrapper = shallowMount(comp)
    wrapper.vm.performSearch('   ')
    expect(wrapper.vm.isLoading).toBe(false)
    expect(wrapper.vm.searchResults).toEqual([])
  })

  it('viewShop 正向：跳转到店铺详情', async () => {
    const comp = (await import('@/views/UserViews/UserSearch.vue')).default
    const wrapper = shallowMount(comp, { global: { mocks: { $router: routerMock } } })
    wrapper.vm.viewShop(9)
    expect(routerMock.push).toHaveBeenCalledWith('/user/shopping/9')
  })
})