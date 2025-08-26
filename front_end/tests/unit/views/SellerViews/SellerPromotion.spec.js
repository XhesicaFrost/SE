import { shallowMount, mount } from '@vue/test-utils'
import { createStore } from 'vuex'
import SellerPromotion from '@/views/SellerViews/SellerPromotion.vue'

// stub 子组件
jest.mock('@/components/bottomNav.vue', () => ({ default: { name: 'BottomNav', render: () => null } }))
jest.mock('@/components/topNav.vue', () => ({ default: { name: 'TopNav', render: () => null } }))

jest.mock('@/config.js', () => ({
  BASE_URL: 'http://localhost:12345',
  fetchWithTimeout: jest.fn()
}))
import { fetchWithTimeout } from '@/config.js'

describe('SellerPromotion.vue', () => {
  let store
  let routerMock

  beforeAll(() => {
    if (!global.confirm) global.confirm = () => true
    if (!global.alert) global.alert = () => {}
    jest.spyOn(console, 'log').mockImplementation(() => {})
    jest.spyOn(console, 'warn').mockImplementation(() => {})
    jest.spyOn(console, 'error').mockImplementation(() => {})
  })

  beforeEach(() => {
    jest.clearAllMocks()
    store = createStore({
      modules: {
        sellerStore: {
          namespaced: true,
          state: () => ({ sellerId: 'seller-001' })
        }
      }
    })
    routerMock = { push: jest.fn() }
    // 避免 mounted 首次请求导致错误
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200, data: [] }) })
  })

  // fetchPromotions
  it('fetchPromotions 正向：200 且数组时填充 promotions', async () => {
    const wrapper = mount(SellerPromotion, {
      global: { plugins: [store], mocks: { $router: routerMock } }
    })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({
      json: async () => ({
        code: 200,
        data: [{ promotionId: 'p1', promotionName: 'A', full: 10, minus: 2, startTime: 's', endTime: 'e' }]
      })
    })

    await wrapper.vm.fetchPromotions()
    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(wrapper.vm.promotions).toHaveLength(1)
  })

  it('fetchPromotions 反向：非 200 时置空列表', async () => {
    const wrapper = mount(SellerPromotion, {
      global: { plugins: [store], mocks: { $router: routerMock } }
    })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 500 }) })

    await wrapper.vm.fetchPromotions()
    expect(wrapper.vm.promotions).toEqual([])
  })

  // deletePromotion
  it('deletePromotion 正向：确认后 200，提示成功并刷新列表', async () => {
    jest.spyOn(window, 'confirm').mockReturnValue(true)
    const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {})

    const wrapper = shallowMount(SellerPromotion, {
      global: { plugins: [store], mocks: { $router: routerMock } }
    })
    // 覆写，避免额外请求
    wrapper.vm.fetchPromotions = jest.fn()

    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200 }) })

    await wrapper.vm.deletePromotion('p1')

    expect(alertSpy).toHaveBeenCalledWith('删除成功')
    expect(wrapper.vm.fetchPromotions).toHaveBeenCalled()
    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(fetchWithTimeout.mock.calls[0][0]).toBe('http://localhost:12345/seller/promotion')
    expect(fetchWithTimeout.mock.calls[0][1].method).toBe('DELETE')
  })

  it('deletePromotion 反向：取消确认不发送请求', async () => {
    jest.spyOn(window, 'confirm').mockReturnValue(false)
    const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {})

    const wrapper = shallowMount(SellerPromotion, {
      global: { plugins: [store], mocks: { $router: routerMock } }
    })

    // 清掉 mounted 阶段触发的获取请求
    fetchWithTimeout.mockClear()

    await wrapper.vm.deletePromotion('p1')

    expect(fetchWithTimeout).not.toHaveBeenCalled()
    expect(alertSpy).not.toHaveBeenCalled()
  })

  // editPromotion
  it('editPromotion 正向：跳转到编辑页（带命名路由与参数）', () => {
    const wrapper = shallowMount(SellerPromotion, {
      global: { plugins: [store], mocks: { $router: routerMock } }
    })
    wrapper.vm.editPromotion('p2')
    expect(routerMock.push).toHaveBeenCalledWith({
      name: 'sellerPromotionEdit',
      params: { promotionId: 'p2' }
    })
  })

  it('editPromotion 反向：路由抛错时向外抛出错误', () => {
    const badRouter = { push: jest.fn(() => { throw new Error('router failed') }) }
    const wrapper = shallowMount(SellerPromotion, {
      global: { plugins: [store], mocks: { $router: badRouter } }
    })
    expect(() => wrapper.vm.editPromotion('p2')).toThrow('router failed')
  })
})