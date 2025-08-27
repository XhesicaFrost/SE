import { shallowMount } from '@vue/test-utils'
import { createStore } from 'vuex'

// stub
jest.mock('@/components/topNav.vue', () => ({ default: { name: 'TopNav', render: () => null } }))

jest.mock('@/config.js', () => ({
  BASE_URL: 'http://localhost:12345',
  fetchWithTimeout: jest.fn()
}))
import { fetchWithTimeout } from '@/config.js'

describe('UserPersonalEdit.vue', () => {
  let store
  let routerMock

  beforeAll(() => {
    jest.spyOn(window, 'alert').mockImplementation(() => {})
    if (!global.URL) global.URL = {}
    if (!global.URL.createObjectURL) global.URL.createObjectURL = jest.fn(() => 'blob:avatar')
  })

  beforeEach(() => {
    jest.clearAllMocks()
    localStorage.setItem('token', 't-123')
    store = createStore({
      modules: {
        userStore: {
          namespaced: true,
          state: () => ({ userInfo: { userId: 'u-1', userName: '张三', userPhone: '138', image: 'img0' } })
        }
      }
    })
    routerMock = { push: jest.fn() }
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200, data: {} }) })
  })

  it('onImageChange 正向：选择图片更新预览', async () => {
    const comp = (await import('@/views/UserViews/UserPersonalEdit.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store] } })
    const file = new File(['x'], 'a.png', { type: 'image/png', size: 100 })
    const input = wrapper.find('input[type="file"]')
    Object.defineProperty(input.element, 'files', { value: [file] })
    await input.trigger('change')
    expect(wrapper.vm.avatarFile).toBe(file)
    expect(wrapper.vm.avatarUrl).toBe('blob:avatar')
  })

  it('onImageChange 反向：非法类型提示且不更新', async () => {
    const comp = (await import('@/views/UserViews/UserPersonalEdit.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store] } })
    const file = new File(['x'], 'a.txt', { type: 'text/plain' })
    const input = wrapper.find('input[type="file"]')
    Object.defineProperty(input.element, 'files', { value: [file] })
    await input.trigger('change')
    expect(window.alert).toHaveBeenCalledWith('请选择图片文件')
    expect(wrapper.vm.avatarFile).toBe(null)
  })

  it('submitEdit 正向：成功后 dispatch 并跳转', async () => {
    const comp = (await import('@/views/UserViews/UserPersonalEdit.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })
    // 设置表单
    await wrapper.setData({ formData: { id: 0, name: ' 李四 ', phone: ' 139 ', image: '' } })
    // 模拟后端成功带 avatarUrl
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200, data: { avatarUrl: 'http://img' } }) })
    // 覆写 dispatch
    wrapper.vm.$store.dispatch = jest.fn().mockResolvedValue()

    await wrapper.vm.submitEdit()

    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(fetchWithTimeout.mock.calls[0][0]).toBe('http://localhost:12345/personal/edit')
    expect(fetchWithTimeout.mock.calls[0][1].method).toBe('POST')
    expect(fetchWithTimeout.mock.calls[0][1].headers.Authorization).toBe('Bearer t-123')
    expect(wrapper.vm.$store.dispatch).toHaveBeenCalledWith('userStore/updateUserInfo', expect.objectContaining({
      userName: '李四',
      userPhone: '139',
      image: 'http://img',
      userImage: 'http://img'
    }))
    expect(routerMock.push).toHaveBeenCalledWith('/user/personal')
  })

  it('submitEdit 反向：无 token 提示并跳转登录', async () => {
    localStorage.removeItem('token')
    const comp = (await import('@/views/UserViews/UserPersonalEdit.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })

    await wrapper.vm.submitEdit()

    expect(window.alert).toHaveBeenCalledWith('请先登录')
    expect(routerMock.push).toHaveBeenCalledWith('/login')
    expect(fetchWithTimeout).not.toHaveBeenCalled()
  })

  it('initUserData 正向：根据 userInfo 预填表单', async () => {
    const comp = (await import('@/views/UserViews/UserPersonalEdit.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store] } })
    await wrapper.vm.initUserData()
    expect(wrapper.vm.formData.name).toBe('张三')
    expect(wrapper.vm.formData.phone).toBe('138')
    expect(wrapper.vm.avatarUrl).toBe('img0')
    expect(wrapper.vm.isDataLoaded).toBe(true)
  })

  it('initUserData 反向：无 userInfo 使用空值', async () => {
    const emptyStore = createStore({ modules: { userStore: { namespaced: true, state: () => ({ userInfo: null }) } } })
    const comp = (await import('@/views/UserViews/UserPersonalEdit.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [emptyStore] } })
    await wrapper.vm.initUserData()
    expect(wrapper.vm.formData.name).toBe('')
    expect(wrapper.vm.formData.phone).toBe('')
    expect(wrapper.vm.avatarUrl).toBe('')
    expect(wrapper.vm.isDataLoaded).toBe(true)
  })

  it('waitForUserInfo 正向：存在 userInfo 立即初始化', async () => {
    const comp = (await import('@/views/UserViews/UserPersonalEdit.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store] } })
    wrapper.vm.initUserData = jest.fn()
    await wrapper.vm.waitForUserInfo()
    expect(wrapper.vm.initUserData).toHaveBeenCalled()
  })

  it('waitForUserInfo 反向：无 userInfo 超时也会初始化', async () => {
    jest.useFakeTimers()
    const emptyStore = createStore({ modules: { userStore: { namespaced: true, state: () => ({ userInfo: null }) } } })
    const comp = (await import('@/views/UserViews/UserPersonalEdit.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [emptyStore] } })
    wrapper.vm.initUserData = jest.fn()
    wrapper.vm.waitForUserInfo()
    jest.advanceTimersByTime(3100)
    expect(wrapper.vm.initUserData).toHaveBeenCalled()
    jest.useRealTimers()
  })
})