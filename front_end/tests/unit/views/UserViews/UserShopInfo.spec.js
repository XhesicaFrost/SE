import { shallowMount, mount } from '@vue/test-utils'
import { createStore } from 'vuex'

// stub 组件
jest.mock('@/components/topNav.vue', () => ({
  default: { name: 'TopNav', render: () => null }
}))

// mock 配置与网络
jest.mock('@/config.js', () => ({
  BASE_URL: 'http://localhost:12345',
  fetchWithTimeout: jest.fn()
}))
import { fetchWithTimeout } from '@/config.js'

describe('UserShopInfo.vue', () => {
  let store
  let routerMock
  let routeMock

  beforeAll(() => {
    jest.spyOn(window, 'alert').mockImplementation(() => {})
    jest.spyOn(console, 'error').mockImplementation(() => {})
    jest.spyOn(console, 'log').mockImplementation(() => {})
  })

  beforeEach(() => {
    jest.clearAllMocks()
    store = createStore({
      modules: {
        userStore: {
          namespaced: true,
          state: () => ({ userInfo: { userId: 'u-1' } })
        }
      }
    })
    routerMock = { push: jest.fn(), go: jest.fn() }
    routeMock = { params: { shopId: '2' } }
    // 安全默认，避免 mounted 报错
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200, data: [] }) })
  })

  it('getImageUrl 正向：返回 base64 dataURL', async () => {
    const comp = (await import('@/views/UserViews/UserShopInfo.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store] } })
    expect(wrapper.vm.getImageUrl('abc')).toBe('data:image/jpeg;base64,abc')
  })

  it('getImageUrl 反向：空返回空串', async () => {
    const comp = (await import('@/views/UserViews/UserShopInfo.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store] } })
    expect(wrapper.vm.getImageUrl('')).toBe('')
  })

  it('fetchAddress 正向：token 存在且 200 填充地址', async () => {
    localStorage.setItem('token', 't-1')
    const comp = (await import('@/views/UserViews/UserShopInfo.vue')).default
    const wrapper = mount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ success: true, code: 200, data: [{ id: 1 }] }) })
    await wrapper.vm.fetchAddress()
    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(wrapper.vm.addresses).toEqual([{ id: 1 }])
  })

  it('fetchAddress 反向：无 token 提示并跳转登录', async () => {
    localStorage.removeItem('token')
    const comp = (await import('@/views/UserViews/UserShopInfo.vue')).default
    const wrapper = mount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })
    fetchWithTimeout.mockClear()
    await wrapper.vm.fetchAddress()
    expect(window.alert).toHaveBeenCalledWith('请先登录')
    expect(routerMock.push).toHaveBeenCalledWith('/login')
    expect(fetchWithTimeout).not.toHaveBeenCalled()
  })

  it('submitCartItems 正向：success 调用 fetchCartItems 刷新', async () => {
    const comp = (await import('@/views/UserViews/UserShopInfo.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store] } })
    wrapper.vm.fetchCartItems = jest.fn().mockResolvedValue()
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ success: true }) })
    await wrapper.vm.submitCartItems(9, 101, 1)
    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(fetchWithTimeout.mock.calls[0][0]).toBe('http://localhost:12345/edit/shopcart?userId=u-1')
    expect(fetchWithTimeout.mock.calls[0][1].method).toBe('POST')
    expect(wrapper.vm.fetchCartItems).toHaveBeenCalled()
  })

  it('submitCartItems 反向：后端返回失败弹窗，不刷新', async () => {
    const comp = (await import('@/views/UserViews/UserShopInfo.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store] } })
    wrapper.vm.fetchCartItems = jest.fn()
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ success: false }) })
    await wrapper.vm.submitCartItems(9, 101, -1)
    expect(window.alert).toHaveBeenCalledWith('购物车修改失败，请重试！')
    expect(wrapper.vm.fetchCartItems).not.toHaveBeenCalled()
  })

  it('fetchShopInfo 正向：200 填充店铺信息', async () => {
    const comp = (await import('@/views/UserViews/UserShopInfo.vue')).default
    const wrapper = mount(comp, { global: { plugins: [store], mocks: { $route: routeMock } } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({
      json: async () => ({ code: 200, data: { id: 2, name: '店A', image: 'IMG', address: 'ADDR', rating: 4.1, sales: 12, deliverTime: '30m' } })
    })
    await wrapper.vm.fetchShopInfo()
    expect(wrapper.vm.shopInfo.name).toBe('店A')
    expect(wrapper.vm.shopInfo.address).toBe('ADDR')
  })

  it('fetchShopInfo 反向：非 200 不更新 name（保持未定义/默认）', async () => {
    const comp = (await import('@/views/UserViews/UserShopInfo.vue')).default
    const wrapper = mount(comp, { global: { plugins: [store], mocks: { $route: routeMock } } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 500 }) })
    await wrapper.vm.fetchShopInfo()
    expect(wrapper.vm.shopInfo.name === '' || wrapper.vm.shopInfo.name === undefined).toBe(true)
  })

  it('fetchproducts 正向：仅保留状态正常的商品', async () => {
    const comp = (await import('@/views/UserViews/UserShopInfo.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $route: routeMock } } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({
      json: async () => ({ code: 200, data: [{ id: 1, status: '正常' }, { id: 2, status: '下架' }] })
    })
    await wrapper.vm.fetchproducts()
    expect(wrapper.vm.products).toEqual([{ id: 1, status: '正常' }])
  })

  it('fetchproducts 反向：非 200 置空', async () => {
    const comp = (await import('@/views/UserViews/UserShopInfo.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $route: routeMock } } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 500 }) })
    await wrapper.vm.fetchproducts()
    expect(wrapper.vm.products).toEqual([])
  })

  it('getCartItemQuantity 正向：命中返回数量', async () => {
    const comp = (await import('@/views/UserViews/UserShopInfo.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store] } })
    await wrapper.setData({ cart: [{ product: { id: 10 }, quantity: 3 }] })
    expect(wrapper.vm.getCartItemQuantity(10)).toBe(3)
  })

  it('getCartItemQuantity 反向：缺失返回 0', async () => {
    const comp = (await import('@/views/UserViews/UserShopInfo.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store] } })
    await wrapper.setData({ cart: [] })
    expect(wrapper.vm.getCartItemQuantity(999)).toBe(0)
  })

  it('fetchCartItems 正向：选中当前店铺的购物车', async () => {
    const comp = (await import('@/views/UserViews/UserShopInfo.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $route: routeMock } } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({
      json: async () => ({
        code: 200,
        data: [
          { shop: { id: '1' }, items: [{ product: { id: 1 }, quantity: 1 }] },
          { shop: { id: '2' }, items: [{ product: { id: 2 }, quantity: 2 }] }
        ]
      })
    })
    await wrapper.vm.fetchCartItems()
    expect(wrapper.vm.cart).toEqual([{ product: { id: 2 }, quantity: 2 }])
  })

  it('fetchCartItems 反向：无 userId 置空不请求', async () => {
    const noUserStore = createStore({ modules: { userStore: { namespaced: true, state: () => ({ userInfo: { userId: '' } }) } } })
    const comp = (await import('@/views/UserViews/UserShopInfo.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [noUserStore], mocks: { $route: routeMock } } })
    fetchWithTimeout.mockClear()
    await wrapper.vm.fetchCartItems()
    expect(fetchWithTimeout).not.toHaveBeenCalled()
    expect(wrapper.vm.cart).toEqual([])
  })

  it('showProductDetail 正向：设置当前商品', async () => {
    const comp = (await import('@/views/UserViews/UserShopInfo.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store] } })
    const p = { id: 1 }
    wrapper.vm.showProductDetail(p)
    // 使用深相等，避免代理对象造成引用不等
    expect(wrapper.vm.currentProduct).toEqual({ id: 1 })
  })

  it('showProductDetail 反向：传 null 关闭弹窗', async () => {
    const comp = (await import('@/views/UserViews/UserShopInfo.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store] } })
    wrapper.vm.showProductDetail(null)
    expect(wrapper.vm.currentProduct).toBe(null)
  })

  it('addToCart 正向：调用提交与刷新', async () => {
    const comp = (await import('@/views/UserViews/UserShopInfo.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store] } })
    await wrapper.setData({ shopInfo: { id: 7 } })
    wrapper.vm.submitCartItems = jest.fn()
    wrapper.vm.fetchCartItems = jest.fn()
    const product = { id: 3 }
    wrapper.vm.addToCart(product)
    expect(wrapper.vm.submitCartItems).toHaveBeenCalledWith(7, 3, 1)
    expect(wrapper.vm.fetchCartItems).toHaveBeenCalled()
  })

  it('decreaseQuantity 反向：减少数量也会提交与刷新', async () => {
    const comp = (await import('@/views/UserViews/UserShopInfo.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store] } })
    await wrapper.setData({ shopInfo: { id: 7 } })
    wrapper.vm.submitCartItems = jest.fn()
    wrapper.vm.fetchCartItems = jest.fn()
    const product = { id: 3 }
    wrapper.vm.decreaseQuantity(product)
    expect(wrapper.vm.submitCartItems).toHaveBeenCalledWith(7, 3, -1)
    expect(wrapper.vm.fetchCartItems).toHaveBeenCalled()
  })

  it('toPayment 正向：有数量且有地址跳转支付页', async () => {
    const comp = (await import('@/views/UserViews/UserShopInfo.vue')).default
    const wrapper = shallowMount(comp, {
      global: { plugins: [store], mocks: { $router: routerMock, $route: routeMock } }
    })
    await wrapper.setData({ addresses: [{ id: 1 }] })
    wrapper.vm.toPayment(1)
    expect(routerMock.push).toHaveBeenCalledWith('/user/payment/2')
  })

  it('toPayment 反向：无地址弹窗并跳转地址页', async () => {
    const comp = (await import('@/views/UserViews/UserShopInfo.vue')).default
    const wrapper = shallowMount(comp, {
      global: { plugins: [store], mocks: { $router: routerMock, $route: routeMock } }
    })
    await wrapper.setData({ addresses: [] })
    wrapper.vm.toPayment(1)
    expect(window.alert).toHaveBeenCalledWith('您还没有设置收货地址！')
    expect(routerMock.push).toHaveBeenCalledWith('/user/address')
  })
})