import { mount, shallowMount } from '@vue/test-utils'
import { createStore } from 'vuex'
import UserAddress from '@/views/UserViews/UserAddress.vue'

// stub 子组件
jest.mock('@/components/topNav.vue', () => ({
  default: { name: 'TopNav', render: () => null }
}))

// 拦截配置与网络
jest.mock('@/config.js', () => ({
  BASE_URL: 'http://localhost:12345',
  fetchWithTimeout: jest.fn()
}))
import { fetchWithTimeout } from '@/config.js'

describe('UserAddress.vue', () => {
  let store
  let routerMock

  beforeAll(() => {
    jest.spyOn(console, 'error').mockImplementation(() => {})
    jest.spyOn(window, 'alert').mockImplementation(() => {})
    if (!global.confirm) global.confirm = () => true
  })

  beforeEach(() => {
    jest.clearAllMocks()
    localStorage.setItem('token', 't-123')
    routerMock = { push: jest.fn(), go: jest.fn() }
    store = createStore({
      modules: {
        userStore: {
          namespaced: true,
          state: () => ({ userInfo: { userId: 'user-001' } })
        }
      }
    })
    // 安全默认
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ success: true, code: 200, data: [] }) })
  })

  // fetchAddress
  it('fetchAddress 正向：token 有效且 200 时填充地址列表', async () => {
    const wrapper = mount(UserAddress, {
      global: { plugins: [store], mocks: { $router: routerMock } }
    })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({
      json: async () => ({
        success: true,
        code: 200,
        data: [
          { id: 1, name: 'A', phone: '13800000000', fullAddress: '路1', current: true },
          { id: 2, name: 'B', phone: '13900000000', fullAddress: '路2', current: false }
        ]
      })
    })

    await wrapper.vm.fetchAddress()

    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(wrapper.vm.addresses).toHaveLength(2)
  })

  it('fetchAddress 反向：无 token 时提示并跳转登录', async () => {
    localStorage.removeItem('token')
    const wrapper = mount(UserAddress, {
      global: { plugins: [store], mocks: { $router: routerMock } }
    })
    fetchWithTimeout.mockClear()

    await wrapper.vm.fetchAddress()

    expect(window.alert).toHaveBeenCalledWith('请先登录')
    expect(routerMock.push).toHaveBeenCalledWith('/login')
    expect(fetchWithTimeout).not.toHaveBeenCalled()
  })

  // deleteAddress
  it('deleteAddress 正向：确认后 success=true 重新拉取列表', async () => {
    jest.spyOn(window, 'confirm').mockReturnValue(true)
    const wrapper = shallowMount(UserAddress, {
      global: { plugins: [store], mocks: { $router: routerMock } }
    })
    wrapper.vm.fetchAddress = jest.fn().mockResolvedValue()

    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ success: true }) })

    await wrapper.vm.deleteAddress(10)

    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(fetchWithTimeout.mock.calls[0][0]).toBe('http://localhost:12345/address/delete')
    expect(fetchWithTimeout.mock.calls[0][1].method).toBe('DELETE')
    expect(wrapper.vm.fetchAddress).toHaveBeenCalled()
  })

  it('deleteAddress 反向：取消确认不发送请求', async () => {
    jest.spyOn(window, 'confirm').mockReturnValue(false)
    const wrapper = shallowMount(UserAddress, {
      global: { plugins: [store], mocks: { $router: routerMock } }
    })
    fetchWithTimeout.mockClear()

    await wrapper.vm.deleteAddress(10)

    expect(fetchWithTimeout).not.toHaveBeenCalled()
  })

  // setCurrentAddress
  it('setCurrentAddress 正向：success=true 后刷新列表', async () => {
    const wrapper = shallowMount(UserAddress, {
      global: { plugins: [store], mocks: { $router: routerMock } }
    })
    wrapper.vm.fetchAddress = jest.fn().mockResolvedValue()

    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ success: true }) })

    await wrapper.vm.setCurrentAddress(2)

    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(fetchWithTimeout.mock.calls[0][0]).toBe('http://localhost:12345/address/current')
    expect(fetchWithTimeout.mock.calls[0][1].method).toBe('POST')
    expect(wrapper.vm.fetchAddress).toHaveBeenCalled()
  })

  it('setCurrentAddress 反向：success=false 弹出更改失败', async () => {
    const wrapper = shallowMount(UserAddress, {
      global: { plugins: [store], mocks: { $router: routerMock } }
    })

    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ success: false }) })

    await wrapper.vm.setCurrentAddress(2)

    expect(window.alert).toHaveBeenCalledWith('更改失败')
  })

  // handleEdit
  it('handleEdit 正向：跳转到编辑页', () => {
    const wrapper = shallowMount(UserAddress, {
      global: { plugins: [store], mocks: { $router: routerMock } }
    })
    wrapper.vm.handleEdit({ id: 7 })
    expect(routerMock.push).toHaveBeenCalledWith('/user/address/edit/7')
  })

  it('handleEdit 反向：路由抛错时向外抛出', () => {
    const badRouter = { push: jest.fn(() => { throw new Error('router failed') }) }
    const wrapper = shallowMount(UserAddress, {
      global: { plugins: [store], mocks: { $router: badRouter } }
    })
    expect(() => wrapper.vm.handleEdit({ id: 7 })).toThrow('router failed')
  })
})