import { shallowMount } from '@vue/test-utils'
import { createStore } from 'vuex'
import SellerRegister from '@/views/SellerViews/SellerRegister.vue'

// stub 子组件
jest.mock('@/components/topNav.vue', () => ({ default: { name: 'TopNav', render: () => null } }))

jest.mock('@/config.js', () => ({
  BASE_URL: 'http://localhost:12345',
  fetchWithTimeout: jest.fn()
}))
import { fetchWithTimeout } from '@/config.js'

describe('SellerRegister.vue', () => {
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
        userStore: {
          namespaced: true,
          state: () => ({ userInfo: { userId: 'user-001', token: 't-123' } })
        }
      }
    })
    routerMock = { push: jest.fn() }
  })

  // toggleTag
  it('toggleTag 正向：可选择/取消且不超过3个', async () => {
    const wrapper = shallowMount(SellerRegister, { global: { plugins: [store] } })
    wrapper.vm.toggleTag('快餐')
    wrapper.vm.toggleTag('奶茶')
    wrapper.vm.toggleTag('咖啡')
    wrapper.vm.toggleTag('甜品') // 超出忽略
    expect(wrapper.vm.shopTags.length).toBe(3)
    wrapper.vm.toggleTag('奶茶')
    expect(wrapper.vm.shopTags).not.toContain('奶茶')
  })

  it('toggleTag 反向：当 submitStatus=success 时不变', async () => {
    const wrapper = shallowMount(SellerRegister, { global: { plugins: [store] } })
    await wrapper.setData({ submitStatus: 'success', shopTags: [] })
    wrapper.vm.toggleTag('快餐')
    expect(wrapper.vm.shopTags).toEqual([])
  })

  // onImageChange
  it('onImageChange 正向：选择图片后更新', async () => {
    const wrapper = shallowMount(SellerRegister, { global: { plugins: [store] } })
    const file = new File(['img'], 'test.png', { type: 'image/png' })
    const input = wrapper.find('input[type="file"]')
    Object.defineProperty(input.element, 'files', { value: [file] })
    await input.trigger('change')
    expect(wrapper.vm.shopImage).toBe(file)
    expect(wrapper.vm.shopImageUrl).toBe('blob:mocked-url')
  })

  it('onImageChange 反向：未选择文件不更新', async () => {
    const wrapper = shallowMount(SellerRegister, { global: { plugins: [store] } })
    await wrapper.setData({ shopImage: null, shopImageUrl: '' })
    const input = wrapper.find('input[type="file"]')
    Object.defineProperty(input.element, 'files', { value: [] })
    await input.trigger('change')
    expect(wrapper.vm.shopImage).toBe(null)
    expect(wrapper.vm.shopImageUrl).toBe('')
  })

  // handleRegister
  it('handleRegister 正向：完整信息返回 success，状态 success', async () => {
    const wrapper = shallowMount(SellerRegister, { global: { plugins: [store], mocks: { $router: routerMock } } })

    const file = new File(['img'], 'a.png', { type: 'image/png' })
    await wrapper.setData({
      shopName: '小店',
      shopAddress: '地址',
      shopImage: file,
      shopTags: ['快餐']
    })

    fetchWithTimeout.mockResolvedValue({ json: async () => ({ data: { status: 'success' } }) })

    await wrapper.vm.handleRegister()

    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(fetchWithTimeout.mock.calls[0][0]).toBe('http://localhost:12345/seller/register')
    expect(fetchWithTimeout.mock.calls[0][1].method).toBe('POST')
    expect(fetchWithTimeout.mock.calls[0][1].headers.Authorization).toBe('Bearer t-123')
    expect(wrapper.vm.submitStatus).toBe('success')
    expect(wrapper.vm.errorMessage).toBe('')
  })

  it('handleRegister 反向：缺少图片或标签不发送请求并提示', async () => {
    const wrapper = shallowMount(SellerRegister, { global: { plugins: [store] } })
    await wrapper.setData({
      shopName: '小店',
      shopAddress: '地址',
      shopImage: null, // 缺图片
      shopTags: []
    })
    await wrapper.vm.handleRegister()
    expect(wrapper.vm.errorMessage).toMatch(/请填写完整信息并上传图片|请至少选择一个店铺类型标签/)
    expect(fetchWithTimeout).not.toHaveBeenCalled()
  })

  // goBack
  it('goBack 正向：跳转到 /seller', () => {
    const wrapper = shallowMount(SellerRegister, { global: { plugins: [store], mocks: { $router: routerMock } } })
    wrapper.vm.goBack()
    expect(routerMock.push).toHaveBeenCalledWith('/seller')
  })

  it('goBack 反向：router.push 抛错时向外抛出', () => {
    const badRouter = { push: jest.fn(() => { throw new Error('router failed') }) }
    const wrapper = shallowMount(SellerRegister, { global: { plugins: [store], mocks: { $router: badRouter } } })
    expect(() => wrapper.vm.goBack()).toThrow('router failed')
  })
})