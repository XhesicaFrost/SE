import { shallowMount, mount } from '@vue/test-utils'
import { createStore } from 'vuex'
import SellerApproval from '@/views/SellerViews/SellerApproval.vue'

// stub 子组件，避免引入内部资源
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

describe('SellerApproval.vue', () => {
  let store

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
    // 默认返回空数组，避免 mounted 干扰
    fetchWithTimeout.mockResolvedValue({
      json: async () => ({ code: 200, data: [] })
    })
  })

  // statusClass
  it('statusClass 正向：返回对应样式类名', () => {
    const wrapper = shallowMount(SellerApproval, { global: { plugins: [store] } })
    expect(wrapper.vm.statusClass('审批中')).toBe('status-pending')
    expect(wrapper.vm.statusClass('否决')).toBe('status-reject')
    expect(wrapper.vm.statusClass('通过')).toBe('status-pass')
  })
  it('statusClass 反向：未知状态返回空', () => {
    const wrapper = shallowMount(SellerApproval, { global: { plugins: [store] } })
    expect(wrapper.vm.statusClass('未知')).toBe('')
  })

  // getImageUrl
  it('getImageUrl 正向：长 base64 自动补全 data url', () => {
    const wrapper = shallowMount(SellerApproval, { global: { plugins: [store] } })
    const longBase64 = 'A'.repeat(120)
    expect(wrapper.vm.getImageUrl(longBase64)).toBe(`data:image/png;base64,${longBase64}`)
    const dataUrl = 'data:image/png;base64,XYZ'
    expect(wrapper.vm.getImageUrl(dataUrl)).toBe(dataUrl)
  })
  it('getImageUrl 反向：空值返回空字符串', () => {
    const wrapper = shallowMount(SellerApproval, { global: { plugins: [store] } })
    expect(wrapper.vm.getImageUrl('')).toBe('')
  })

  // fetchShopApprovals
  it('fetchShopApprovals 正向：code=200 且为数组时填充数据', async () => {
    const wrapper = mount(SellerApproval, { global: { plugins: [store] } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({
      json: async () => ({
        code: 200,
        data: [{ id: 1, name: 'ShopA', image: 'IMG', address: 'Addr', status: '审批中' }]
      })
    })

    await wrapper.vm.fetchShopApprovals()
    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(wrapper.vm.shopApprovals).toHaveLength(1)
    expect(wrapper.vm.shopApprovals[0].name).toBe('ShopA')
  })

  it('fetchShopApprovals 反向：code!=200 或异常时置空', async () => {
    const wrapper = mount(SellerApproval, { global: { plugins: [store] } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 500 }) })

    await wrapper.vm.fetchShopApprovals()
    expect(wrapper.vm.shopApprovals).toEqual([])

    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockRejectedValue(new Error('net'))
    await wrapper.vm.fetchShopApprovals()
    expect(wrapper.vm.shopApprovals).toEqual([])
  })

  // fetchItemApprovals
  it('fetchItemApprovals 正向：code=200 且为数组时填充数据', async () => {
    const wrapper = mount(SellerApproval, { global: { plugins: [store] } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({
      json: async () => ({
        code: 200,
        data: [{ id: 2, name: 'ItemA', image: 'IMG', price: 9.9, status: '通过' }]
      })
    })

    await wrapper.vm.fetchItemApprovals()
    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(wrapper.vm.itemApprovals).toHaveLength(1)
    expect(wrapper.vm.itemApprovals[0].name).toBe('ItemA')
  })

  it('fetchItemApprovals 反向：code!=200 或异常时置空', async () => {
    const wrapper = mount(SellerApproval, { global: { plugins: [store] } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 400 }) })

    await wrapper.vm.fetchItemApprovals()
    expect(wrapper.vm.itemApprovals).toEqual([])

    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockRejectedValue(new Error('net'))
    await wrapper.vm.fetchItemApprovals()
    expect(wrapper.vm.itemApprovals).toEqual([])
  })
})