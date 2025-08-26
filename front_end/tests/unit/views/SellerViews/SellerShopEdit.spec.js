import { shallowMount, mount } from '@vue/test-utils'
import { createStore } from 'vuex'
import SellerShopEdit from '@/views/SellerViews/SellerShopEdit.vue'

jest.mock('@/config.js', () => ({
  BASE_URL: 'http://localhost:12345',
  fetchWithTimeout: jest.fn()
}))
import { fetchWithTimeout } from '@/config.js'

describe('SellerShopEdit.vue', () => {
  let store
  let routerMock

  beforeAll(() => {
    if (!global.URL) global.URL = {}
    if (!global.URL.createObjectURL) global.URL.createObjectURL = jest.fn(() => 'blob:mocked-url')
  })

  beforeEach(() => {
    jest.clearAllMocks()
    store = createStore({
      modules: {
        sellerStore: { namespaced: true, state: () => ({ sellerId: 'seller-001' }) }
      }
    })
    routerMock = { push: jest.fn() }
    // 避免 mounted 的 fetchShopInfo 写入错误
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200, data: {} }) })
  })

  // toggleTag
  it('toggleTag 正向：可选择/取消且不超过3个', async () => {
    const wrapper = shallowMount(SellerShopEdit, { global: { plugins: [store] } })
    wrapper.vm.toggleTag('快餐')
    wrapper.vm.toggleTag('奶茶')
    wrapper.vm.toggleTag('咖啡')
    wrapper.vm.toggleTag('甜品') // 超过3个应忽略
    expect(wrapper.vm.shopTags).toEqual(expect.arrayContaining(['快餐', '奶茶', '咖啡']))
    expect(wrapper.vm.shopTags.length).toBe(3)
    // 取消选择
    wrapper.vm.toggleTag('奶茶')
    expect(wrapper.vm.shopTags).toEqual(expect.arrayContaining(['快餐', '咖啡']))
    expect(wrapper.vm.shopTags).not.toContain('奶茶')
  })

  it('toggleTag 反向：提交成功后不再允许修改', async () => {
    const wrapper = shallowMount(SellerShopEdit, { global: { plugins: [store] } })
    await wrapper.setData({ submitStatus: 'success', shopTags: [] })
    wrapper.vm.toggleTag('快餐')
    expect(wrapper.vm.shopTags).toEqual([])
  })

  // onImageChange
  it('onImageChange 正向：选择图片后应更新 shopImage 与预览地址', async () => {
    const wrapper = shallowMount(SellerShopEdit, { global: { plugins: [store] } })
    const file = new File(['img'], 'test.png', { type: 'image/png' })
    const input = wrapper.find('input[type="file"]')
    Object.defineProperty(input.element, 'files', { value: [file] })
    await input.trigger('change')

    expect(wrapper.vm.shopImage).toBe(file)
    expect(wrapper.vm.shopImageUrl).toBe('blob:mocked-url')
    expect(URL.createObjectURL).toHaveBeenCalledWith(file)
  })

  it('onImageChange 反向：未选择文件时不更新', async () => {
    const wrapper = shallowMount(SellerShopEdit, { global: { plugins: [store] } })
    await wrapper.setData({ shopImage: null, shopImageUrl: '' })
    const input = wrapper.find('input[type="file"]')
    Object.defineProperty(input.element, 'files', { value: [] })
    await input.trigger('change')
    expect(wrapper.vm.shopImage).toBe(null)
    expect(wrapper.vm.shopImageUrl).toBe('')
  })

  // fetchShopInfo
  it('fetchShopInfo 正向：code=200 且有 data 时填充字段与图片/标签解析', async () => {
    const wrapper = mount(SellerShopEdit, { global: { plugins: [store] } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({
      json: async () => ({
        code: 200,
        data: {
          shopName: '小店',
          shopAddress: '地址1',
          shopTags: '["快餐","奶茶"]',
          shopImg: 'BASE64DATA'
        }
      })
    })

    await wrapper.vm.fetchShopInfo()

    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(wrapper.vm.shopName).toBe('小店')
    expect(wrapper.vm.shopAddress).toBe('地址1')
    expect(wrapper.vm.shopTags).toEqual(['快餐', '奶茶'])
    expect(wrapper.vm.shopImageUrl).toBe('data:image/png;base64,BASE64DATA')
    expect(wrapper.vm.errorMessage).toBe('')
  })

  it('fetchShopInfo 反向：非 200 设置错误消息', async () => {
    const wrapper = mount(SellerShopEdit, { global: { plugins: [store] } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 500 }) })

    await wrapper.vm.fetchShopInfo()
    expect(wrapper.vm.errorMessage).toBe('店铺信息获取失败')
  })

  // handleEdit
  it('handleEdit 正向：完整信息 code=200 设置 success', async () => {
    const wrapper = mount(SellerShopEdit, { global: { plugins: [store] } })
    await wrapper.setData({
      shopName: '店A', shopAddress: '地址A', shopTags: ['快餐']
    })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200 }) })

    await wrapper.vm.handleEdit()

    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(fetchWithTimeout.mock.calls[0][0]).toBe('http://localhost:12345/seller/edit')
    expect(fetchWithTimeout.mock.calls[0][1].method).toBe('POST')
    expect(wrapper.vm.submitStatus).toBe('success')
    expect(wrapper.vm.errorMessage).toBe('')
  })

  it('handleEdit 反向：缺少标签不发送请求并提示', async () => {
    const wrapper = shallowMount(SellerShopEdit, { global: { plugins: [store] } })
    await wrapper.setData({ shopName: '店A', shopAddress: '地址A', shopTags: [] })
    fetchWithTimeout.mockClear()

    await wrapper.vm.handleEdit()
    expect(wrapper.vm.errorMessage).toBe('请至少选择一个店铺类型标签')
    expect(fetchWithTimeout).not.toHaveBeenCalled()
  })

  // goBack
  it('goBack 正向：跳转到 /seller/shop', () => {
    const wrapper = shallowMount(SellerShopEdit, { global: { plugins: [store], mocks: { $router: routerMock } } })
    wrapper.vm.goBack()
    expect(routerMock.push).toHaveBeenCalledWith('/seller/shop')
  })

  it('goBack 反向：router.push 抛错时向外抛出', () => {
    const badRouter = { push: jest.fn(() => { throw new Error('router failed') }) }
    const wrapper = shallowMount(SellerShopEdit, { global: { plugins: [store], mocks: { $router: badRouter } } })
    expect(() => wrapper.vm.goBack()).toThrow('router failed')
  })
})