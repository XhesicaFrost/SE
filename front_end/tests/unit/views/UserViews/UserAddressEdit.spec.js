import { mount, shallowMount } from '@vue/test-utils'
import { createStore } from 'vuex'
import UserAddressEdit from '@/views/UserViews/UserAddressEdit.vue'

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

describe('UserAddressEdit.vue', () => {
  let store
  let routerMock
  let routeMock

  beforeAll(() => {
    jest.spyOn(console, 'error').mockImplementation(() => {})
    jest.spyOn(console, 'warn').mockImplementation(() => {})
    jest.spyOn(window, 'alert').mockImplementation(() => {})
  })

  beforeEach(() => {
    jest.clearAllMocks()
    // 缺省 token
    localStorage.setItem('token', 't-123')
    store = createStore({
      modules: {
        userStore: {
          namespaced: true,
          state: () => ({ userInfo: { userId: 'user-001' } })
        }
      }
    })
    routerMock = { push: jest.fn(), go: jest.fn() }
    routeMock = { params: { addressId: '1' } }
    // 避免 mounted 首次拉取写入错误
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ success: true, code: 200, data: [] }) })
    global.fetch = jest.fn()
  })

  // fetchAddressInfo
  it('fetchAddressInfo 正向：token 与 addressId 有效，填充地址', async () => {
    const wrapper = mount(UserAddressEdit, {
      global: { plugins: [store], mocks: { $router: routerMock, $route: routeMock } }
    })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({
      json: async () => ({
        success: true,
        code: 200,
        data: [
          { id: 1, name: '张三', phone: '13800000000', fullAddress: 'A路1号', current: true },
          { id: 2, name: '李四', phone: '13900000000', fullAddress: 'B路2号', current: false }
        ]
      })
    })

    await wrapper.vm.fetchAddressInfo()

    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(wrapper.vm.address.id).toBe(1)
    expect(wrapper.vm.address.name).toBe('张三')
    expect(wrapper.vm.address.phone).toBe('13800000000')
    // 不强求是否回退，只要未跳到登录页即可
    expect(routerMock.push).not.toHaveBeenCalledWith('/login')
  })

  it('fetchAddressInfo 反向：无 token 时提示并跳转登录', async () => {
    localStorage.removeItem('token')
    const wrapper = mount(UserAddressEdit, {
      global: { plugins: [store], mocks: { $router: routerMock, $route: routeMock } }
    })
    fetchWithTimeout.mockClear()

    await wrapper.vm.fetchAddressInfo()

    expect(window.alert).toHaveBeenCalledWith('请先登录')
    expect(routerMock.push).toHaveBeenCalledWith('/login')
    expect(fetchWithTimeout).not.toHaveBeenCalled()
  })

  // submitAddress
  it('submitAddress 正向：提交成功后提示并返回上一页', async () => {
    const wrapper = shallowMount(UserAddressEdit, {
      global: { plugins: [store], mocks: { $router: routerMock, $route: routeMock } }
    })
    await wrapper.setData({
      address: { id: 3, name: '王五', phone: '13600000000', fullAddress: 'C路3号', current: false }
    })

    global.fetch.mockResolvedValue({
      json: async () => ({ success: true })
    })

    await wrapper.vm.submitAddress()

    expect(global.fetch).toHaveBeenCalledTimes(1)
    expect(global.fetch.mock.calls[0][0]).toBe('http://localhost:12345/address/edit')
    expect(global.fetch.mock.calls[0][1].method).toBe('POST')
    expect(global.fetch.mock.calls[0][1].headers.Authorization).toBe('Bearer t-123')
    expect(window.alert).toHaveBeenCalledWith('地址修改成功！')
    expect(routerMock.go).toHaveBeenCalledWith(-1)
  })

  it('submitAddress 反向：缺少地址ID直接提示错误且不提交', async () => {
    const wrapper = shallowMount(UserAddressEdit, {
      global: { plugins: [store], mocks: { $router: routerMock, $route: routeMock } }
    })
    await wrapper.setData({
      address: { id: null, name: '王五', phone: '13600000000', fullAddress: 'C路3号', current: false }
    })
    global.fetch.mockClear()

    await wrapper.vm.submitAddress()

    expect(window.alert).toHaveBeenCalledWith('地址ID无效')
    expect(global.fetch).not.toHaveBeenCalled()
  })
})