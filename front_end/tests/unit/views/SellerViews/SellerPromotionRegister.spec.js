import { shallowMount } from '@vue/test-utils'
import { createStore } from 'vuex'
import SellerPromotionRegister from '@/views/SellerViews/SellerPromotionRegister.vue'

jest.mock('@/config.js', () => ({
  BASE_URL: 'http://localhost:12345',
  fetchWithTimeout: jest.fn()
}))
import { fetchWithTimeout } from '@/config.js'

describe('SellerPromotionRegister.vue', () => {
  let store
  let routerMock

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
  })

  // handleRegister
  it('handleRegister 正向：完整信息提交成功，状态为 success', async () => {
    const wrapper = shallowMount(SellerPromotionRegister, {
      global: { plugins: [store], mocks: { $router: routerMock } }
    })

    await wrapper.setData({
      promotionName: '双11满减',
      full: 100,
      minus: 20,
      startTime: '2025-11-10T00:00',
      endTime: '2025-11-12T23:59'
    })

    fetchWithTimeout.mockResolvedValue({
      json: async () => ({ data: { status: 'success' } })
    })

    await wrapper.vm.handleRegister()

    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(fetchWithTimeout.mock.calls[0][0]).toBe('http://localhost:12345/seller/promotion/register')
    expect(fetchWithTimeout.mock.calls[0][1].method).toBe('POST')
    expect(wrapper.vm.submitStatus).toBe('success')
    expect(wrapper.vm.errorMessage).toBe('')
  })

  it('handleRegister 反向：缺少必填项不发送请求并提示错误', async () => {
    const wrapper = shallowMount(SellerPromotionRegister, {
      global: { plugins: [store], mocks: { $router: routerMock } }
    })

    await wrapper.setData({
      promotionName: '', // 缺少名称
      full: 100,
      minus: 20,
      startTime: '2025-11-10T00:00',
      endTime: '2025-11-12T23:59'
    })

    await wrapper.vm.handleRegister()

    expect(wrapper.vm.errorMessage).toBe('请填写完整信息')
    expect(fetchWithTimeout).not.toHaveBeenCalled()
  })

  // goBack
  it('goBack 正向：跳转到促销管理', () => {
    const wrapper = shallowMount(SellerPromotionRegister, {
      global: { plugins: [store], mocks: { $router: routerMock } }
    })
    wrapper.vm.goBack()
    expect(routerMock.push).toHaveBeenCalledWith('/seller/promotion')
  })

  it('goBack 反向：路由抛错时向外抛出错误', () => {
    const badRouter = { push: jest.fn(() => { throw new Error('router failed') }) }
    const wrapper = shallowMount(SellerPromotionRegister, {
      global: { plugins: [store], mocks: { $router: badRouter } }
    })
    expect(() => wrapper.vm.goBack()).toThrow('router failed')
  })
})