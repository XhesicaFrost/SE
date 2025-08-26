import { shallowMount, mount } from '@vue/test-utils'
import { createStore } from 'vuex'
import SellerItemRegister from '@/views/SellerViews/SellerItemRegister.vue'

jest.mock('@/config.js', () => ({
  BASE_URL: 'http://localhost:12345',
  fetchWithTimeout: jest.fn()
}))

import { fetchWithTimeout } from '@/config.js'

describe('SellerItemRegister.vue', () => {
  let store
  let routerMock

  beforeAll(() => {
    if (!global.URL) global.URL = {}
    if (!global.URL.createObjectURL) {
      global.URL.createObjectURL = jest.fn(() => 'blob:mocked-url')
    }
  })

  beforeEach(() => {
    jest.clearAllMocks()
    const sellerStore = {
      namespaced: true,
      state: () => ({
        sellerId: 'seller-001'
      })
    }
    store = createStore({
      modules: { sellerStore }
    })
    routerMock = { push: jest.fn() }
  })

  // onImageChange
  it('onImageChange 正向：选择图片后应更新 itemImage 与预览地址', async () => {
    const wrapper = shallowMount(SellerItemRegister, {
      global: {
        plugins: [store],
        mocks: { $router: routerMock }
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
    const wrapper = shallowMount(SellerItemRegister, {
      global: {
        plugins: [store],
        mocks: { $router: routerMock }
      }
    })

    await wrapper.setData({ itemImage: null, itemImageUrl: '' })
    const input = wrapper.find('input[type="file"]')
    // 修改：设置为空数组并触发 change
    Object.defineProperty(input.element, 'files', { value: [] })
    await input.trigger('change')

    expect(wrapper.vm.itemImage).toBe(null)
    expect(wrapper.vm.itemImageUrl).toBe('')
  })

  // handleRegister
  it('handleRegister 正向：表单完整且服务端返回 code=200 时成功', async () => {
    const wrapper = mount(SellerItemRegister, {
      global: {
        plugins: [store],
        mocks: { $router: routerMock }
      }
    })
    await wrapper.setData({
      itemName: '可乐',
      itemImage: new File(['img'], 'test.png', { type: 'image/png' }),
      itemPrice: 3.5,
      itemDescription: '冰镇可乐'
    })

    fetchWithTimeout.mockResolvedValue({
      json: async () => ({ code: 200, data: {} })
    })

    await wrapper.vm.handleRegister()

    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(fetchWithTimeout.mock.calls[0][0]).toBe('http://localhost:12345/seller/item/register')
    expect(fetchWithTimeout.mock.calls[0][1].method).toBe('POST')
    expect(wrapper.vm.submitStatus).toBe('success')
    expect(wrapper.vm.errorMessage).toBe('')
  })

  it('handleRegister 反向：缺少必填项时应提示错误且不发送请求', async () => {
    const wrapper = shallowMount(SellerItemRegister, {
      global: {
        plugins: [store],
        mocks: { $router: routerMock }
      }
    })
    await wrapper.setData({
      itemName: '可乐',
      itemImage: null, // 缺图片
      itemPrice: 3.5
    })

    await wrapper.vm.handleRegister()

    expect(wrapper.vm.errorMessage).toBe('请填写完整信息并上传图片')
    expect(fetchWithTimeout).not.toHaveBeenCalled()
  })

  // goBack
  it('goBack 正向：应跳转到 /seller/shop', () => {
    const wrapper = shallowMount(SellerItemRegister, {
      global: {
        plugins: [store],
        mocks: { $router: routerMock }
      }
    })

    wrapper.vm.goBack()
    expect(routerMock.push).toHaveBeenCalledWith('/seller/shop')
  })

  it('goBack 反向：当 router.push 抛错时应向外抛出错误', () => {
    const badRouter = { push: jest.fn(() => { throw new Error('router failed') }) }
    const wrapper = shallowMount(SellerItemRegister, {
      global: {
        plugins: [store],
        mocks: { $router: badRouter }
      }
    })

    expect(() => wrapper.vm.goBack()).toThrow('router failed')
  })
})