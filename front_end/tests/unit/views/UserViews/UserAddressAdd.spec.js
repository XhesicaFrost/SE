import { shallowMount } from '@vue/test-utils'
import { createStore } from 'vuex'
import UserAddressAdd from '@/views/UserViews/UserAddressAdd.vue'

// stub 子组件
jest.mock('@/components/topNav.vue', () => ({
  default: { name: 'TopNav', render: () => null }
}))

// 配置 BASE_URL
jest.mock('@/config.js', () => ({
  BASE_URL: 'http://localhost:12345'
}))

describe('UserAddressAdd.vue', () => {
  let store
  let routerMock

  beforeAll(() => {
    jest.spyOn(console, 'error').mockImplementation(() => {})
    jest.spyOn(window, 'alert').mockImplementation(() => {})
  })

  beforeEach(() => {
    jest.clearAllMocks()
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
    global.fetch = jest.fn()
  })

  // submitAddress
  it('submitAddress 正向：信息完整且成功，提示并返回上一页', async () => {
    const wrapper = shallowMount(UserAddressAdd, {
      global: { plugins: [store], mocks: { $router: routerMock } }
    })
    await wrapper.setData({
      address: { name: '张三', phone: '13800000000', fullAddress: 'A路1号', current: true }
    })

    global.fetch.mockResolvedValue({ json: async () => ({ success: true }) })

    await wrapper.vm.submitAddress()

    expect(global.fetch).toHaveBeenCalledTimes(1)
    expect(global.fetch.mock.calls[0][0]).toBe('http://localhost:12345/address/add')
    expect(global.fetch.mock.calls[0][1].method).toBe('POST')
    expect(global.fetch.mock.calls[0][1].headers.Authorization).toBe('Bearer t-123')
    expect(window.alert).toHaveBeenCalledWith('地址添加成功！')
    expect(routerMock.go).toHaveBeenCalledWith(-1)
  })

  it('submitAddress 反向：缺少必填项直接提示且不请求', async () => {
    const wrapper = shallowMount(UserAddressAdd, {
      global: { plugins: [store], mocks: { $router: routerMock } }
    })
    await wrapper.setData({
      address: { name: '', phone: '13800000000', fullAddress: 'A路1号', current: false }
    })
    global.fetch.mockClear()

    await wrapper.vm.submitAddress()

    expect(window.alert).toHaveBeenCalledWith('请填写完整信息')
    expect(global.fetch).not.toHaveBeenCalled()
  })
})