import { shallowMount, mount } from '@vue/test-utils'
import { createStore } from 'vuex'
import SellerItemEdit from '@/views/SellerViews/SellerItemEdit.vue'

jest.mock('@/config.js', () => ({
  BASE_URL: 'http://localhost:12345',
  fetchWithTimeout: jest.fn()
}))

import { fetchWithTimeout } from '@/config.js'

describe('SellerItemEdit.vue', () => {
  let store
  let routerMock
  let routeMock

  beforeAll(() => {
    if (!global.URL) global.URL = {}
    if (!global.URL.createObjectURL) {
      global.URL.createObjectURL = jest.fn(() => 'blob:mocked-url')
    }
    // 静音 mounted 的日志
    jest.spyOn(console, 'log').mockImplementation(() => {})
  })

  beforeEach(() => {
    jest.clearAllMocks()
    store = createStore({})
    routerMock = { push: jest.fn() }
    routeMock = { params: { id: 'item-001' } }
    // 修改：默认返回 200，避免 mounted 设置错误消息
    fetchWithTimeout.mockResolvedValue({
      json: async () => ({ code: 200, data: {} })
    })
  })

  // onImageChange
  it('onImageChange 正向：选择图片后应更新 itemImage 与预览地址', async () => {
    const wrapper = shallowMount(SellerItemEdit, {
      global: {
        plugins: [store],
        mocks: { $router: routerMock, $route: routeMock }
      }
    })

    const file = new File(['img'], 'test.png', { type: 'image/png' })
    const input = wrapper.find('input[type="file"]')
    // 修改：通过 defineProperty 设置 files，再触发 change
    Object.defineProperty(input.element, 'files', { value: [file] })
    await input.trigger('change')

    expect(wrapper.vm.itemImage).toBe(file)
    expect(wrapper.vm.itemImageUrl).toBe('blob:mocked-url')
    expect(URL.createObjectURL).toHaveBeenCalledWith(file)
  })

  it('onImageChange 反向：未选择文件时不应更新图片信息', async () => {
    const wrapper = shallowMount(SellerItemEdit, {
      global: {
        plugins: [store],
        mocks: { $router: routerMock, $route: routeMock }
      }
    })

    await wrapper.setData({ itemImage: null, itemImageUrl: '' })
    const input = wrapper.find('input[type="file"]')
    Object.defineProperty(input.element, 'files', { value: [] })
    await input.trigger('change')

    expect(wrapper.vm.itemImage).toBe(null)
    expect(wrapper.vm.itemImageUrl).toBe('')
  })

  // fetchItemInfo
  it('fetchItemInfo 正向：服务端返回 code=200 且有 data 时应填充描述', async () => {
    const wrapper = mount(SellerItemEdit, {
      global: {
        plugins: [store],
        mocks: { $router: routerMock, $route: routeMock }
      }
    })
    await wrapper.setData({ itemId: 'item-001' })

    // 重要：忽略 mounted 自动请求
    fetchWithTimeout.mockClear()

    fetchWithTimeout.mockResolvedValue({
      json: async () => ({
        code: 200,
        data: { itemDescription: '好吃的汉堡' }
      })
    })

    await wrapper.vm.fetchItemInfo()

    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(wrapper.vm.itemDescription).toBe('好吃的汉堡')
    expect(wrapper.vm.errorMessage).toBe('')
  })

  it('fetchItemInfo 反向：返回非 200 或无数据时应提示错误', async () => {
    const wrapper = mount(SellerItemEdit, {
      global: {
        plugins: [store],
        mocks: { $router: routerMock, $route: routeMock }
      }
    })
    await wrapper.setData({ itemId: 'item-001' })

    // 忽略 mounted 自动请求
    fetchWithTimeout.mockClear()

    fetchWithTimeout.mockResolvedValue({
      json: async () => ({ code: 500 })
    })

    await wrapper.vm.fetchItemInfo()

    expect(wrapper.vm.errorMessage).toBe('商品信息获取失败')
  })

  // handleEdit
  it('handleEdit 正向：信息完整且服务端返回 success 时应设为 success', async () => {
    const wrapper = mount(SellerItemEdit, {
      global: {
        plugins: [store],
        mocks: { $router: routerMock, $route: routeMock }
      }
    })

    await wrapper.setData({
      itemId: 'item-001',
      itemName: '薯条',
      itemPrice: 6.5,
      itemDescription: '热薯条'
    })

    // 忽略 mounted 自动请求
    fetchWithTimeout.mockClear()

    fetchWithTimeout.mockResolvedValue({
      json: async () => ({ data: { status: 'success' } })
    })

    await wrapper.vm.handleEdit()

    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(fetchWithTimeout.mock.calls[0][0]).toBe('http://localhost:12345/seller/item/edit')
    expect(fetchWithTimeout.mock.calls[0][1].method).toBe('POST')
    expect(wrapper.vm.submitStatus).toBe('success')
    expect(wrapper.vm.errorMessage).toBe('')
  })

  it('handleEdit 反向：缺少必填项时应提示错误且不发送请求', async () => {
    const wrapper = shallowMount(SellerItemEdit, {
      global: {
        plugins: [store],
        mocks: { $router: routerMock, $route: routeMock }
      }
    })

    await wrapper.setData({
      itemId: 'item-001',
      itemName: '', // 缺名称
      itemPrice: 6.5
    })

    // 忽略 mounted 自动请求
    fetchWithTimeout.mockClear()

    await wrapper.vm.handleEdit()

    expect(wrapper.vm.errorMessage).toBe('请填写完整信息')
    expect(fetchWithTimeout).not.toHaveBeenCalled()
  })

  // goBack
  it('goBack 正向：应跳转到 /seller/shop', () => {
    const wrapper = shallowMount(SellerItemEdit, {
      global: {
        plugins: [store],
        mocks: { $router: routerMock, $route: routeMock }
      }
    })

    wrapper.vm.goBack()
    expect(routerMock.push).toHaveBeenCalledWith('/seller/shop')
  })

  it('goBack 反向：当 router.push 抛错时应向外抛出错误', () => {
    const badRouter = { push: jest.fn(() => { throw new Error('router failed') }) }
    const wrapper = shallowMount(SellerItemEdit, {
      global: {
        plugins: [store],
        mocks: { $router: badRouter, $route: routeMock }
      }
    })

    expect(() => wrapper.vm.goBack()).toThrow('router failed')
  })
})