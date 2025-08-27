import { shallowMount, mount } from '@vue/test-utils'
import { createStore } from 'vuex'
import SellerPromotionEdit from '@/views/SellerViews/SellerPromotionEdit.vue'

jest.mock('@/config.js', () => ({
  BASE_URL: 'http://localhost:12345',
  fetchWithTimeout: jest.fn()
}))
import { fetchWithTimeout } from '@/config.js'

describe('SellerPromotionEdit.vue', () => {
  let store
  let routerMock
  let routeMock

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
    routeMock = { params: { promotionId: 'promo-100' } }
    // 避免 mounted 初次请求写入错误
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200, data: {} }) })
  })

  // fetchPromotionInfo
  it('fetchPromotionInfo 正向：200 且有数据，填充表单', async () => {
    const wrapper = mount(SellerPromotionEdit, {
      global: { plugins: [store], mocks: { $router: routerMock, $route: routeMock } }
    })
    await wrapper.setData({ promotionId: 'promo-100' })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({
      json: async () => ({
        code: 200,
        data: {
          promotionName: '暑期活动',
          full: 50,
          minus: 10,
          startTime: '2025-07-01T00:00',
          endTime: '2025-07-31T23:59'
        }
      })
    })

    await wrapper.vm.fetchPromotionInfo()

    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(wrapper.vm.promotionName).toBe('暑期活动')
    expect(wrapper.vm.full).toBe(50)
    expect(wrapper.vm.minus).toBe(10)
    expect(wrapper.vm.errorMessage).toBe('')
  })

  it('fetchPromotionInfo 反向：非 200 时设置错误消息', async () => {
    const wrapper = mount(SellerPromotionEdit, {
      global: { plugins: [store], mocks: { $router: routerMock, $route: routeMock } }
    })
    await wrapper.setData({ promotionId: 'promo-100' })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 500 }) })

    await wrapper.vm.fetchPromotionInfo()
    expect(wrapper.vm.errorMessage).toBe('促销活动信息获取失败')
  })

  // handleEdit
  it('handleEdit 正向：完整信息返回 success，状态为 success', async () => {
    const wrapper = mount(SellerPromotionEdit, {
      global: { plugins: [store], mocks: { $router: routerMock, $route: routeMock } }
    })
    await wrapper.setData({
      promotionId: 'promo-100',
      promotionName: '暑期活动',
      full: 50,
      minus: 10,
      startTime: '2025-07-01T00:00',
      endTime: '2025-07-31T23:59'
    })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ data: { status: 'success' } }) })

    await wrapper.vm.handleEdit()

    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(fetchWithTimeout.mock.calls[0][0]).toBe('http://localhost:12345/seller/promotion/edit')
    expect(fetchWithTimeout.mock.calls[0][1].method).toBe('POST')
    expect(wrapper.vm.submitStatus).toBe('success')
    expect(wrapper.vm.errorMessage).toBe('')
  })

  it('handleEdit 反向：缺少必填项不发送请求并提示错误', async () => {
    const wrapper = shallowMount(SellerPromotionEdit, {
      global: { plugins: [store], mocks: { $router: routerMock, $route: routeMock } }
    })
    await wrapper.setData({
      promotionId: 'promo-100',
      promotionName: '', // 缺名称
      full: 50,
      minus: 10,
      startTime: '2025-07-01T00:00',
      endTime: '2025-07-31T23:59'
    })
    fetchWithTimeout.mockClear()

    await wrapper.vm.handleEdit()

    expect(wrapper.vm.errorMessage).toBe('请填写完整信息')
    expect(fetchWithTimeout).not.toHaveBeenCalled()
  })

  // goBack
  it('goBack 正向：跳转到促销管理', () => {
    const wrapper = shallowMount(SellerPromotionEdit, {
      global: { plugins: [store], mocks: { $router: routerMock, $route: routeMock } }
    })
    wrapper.vm.goBack()
    expect(routerMock.push).toHaveBeenCalledWith('/seller/promotion')
  })

  it('goBack 反向：路由抛错时向外抛出错误', () => {
    const badRouter = { push: jest.fn(() => { throw new Error('router failed') }) }
    const wrapper = shallowMount(SellerPromotionEdit, {
      global: { plugins: [store], mocks: { $router: badRouter, $route: routeMock } }
    })
    expect(() => wrapper.vm.goBack()).toThrow('router failed')
  })
})