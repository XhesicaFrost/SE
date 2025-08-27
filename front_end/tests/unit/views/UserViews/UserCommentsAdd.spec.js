import { shallowMount } from '@vue/test-utils'
import { createStore } from 'vuex'

// stub 组件
jest.mock('@/components/topNav.vue', () => ({
  default: { name: 'TopNav', render: () => null }
}))

// mock 配置
jest.mock('@/config.js', () => ({
  BASE_URL: 'http://localhost:12345'
}))

describe('UserCommentsAdd.vue', () => {
  let store
  let routerMock
  let routeMock

  beforeAll(() => {
    jest.spyOn(window, 'alert').mockImplementation(() => {})
    if (!global.URL) global.URL = {}
    if (!global.URL.createObjectURL) global.URL.createObjectURL = jest.fn(() => 'blob:mock')
  })

  beforeEach(() => {
    jest.clearAllMocks()
    localStorage.setItem('token', 't-123')
    store = createStore({
      modules: { userStore: { namespaced: true, state: () => ({ userInfo: { userId: 'u-1' } }) } }
    })
    routerMock = { push: jest.fn(), go: jest.fn() }
    routeMock = { params: { orderId: 'o-9' } }
    global.fetch = jest.fn()
  })

  it('handleImage 正向：选择图片且通过校验，设置预览', async () => {
    const comp = (await import('@/views/UserViews/UserCommentsAdd.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock, $route: routeMock } } })
    const file = new File(['x'], 'a.png', { type: 'image/png' })
    const input = wrapper.find('input[type="file"]')
    Object.defineProperty(input.element, 'files', { value: [file] })
    await input.trigger('change')
    expect(wrapper.vm.comment.image).toBe(file)
    expect(wrapper.vm.imageUrl).toBe('blob:mock')
  })

  it('handleImage 反向：类型错误提示且不设置', async () => {
    const comp = (await import('@/views/UserViews/UserCommentsAdd.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock, $route: routeMock } } })
    const file = new File(['x'], 'a.txt', { type: 'text/plain' })
    const input = wrapper.find('input[type="file"]')
    Object.defineProperty(input.element, 'files', { value: [file] })
    await input.trigger('change')
    expect(window.alert).toHaveBeenCalledWith('请选择图片文件')
    expect(wrapper.vm.comment.image).toBeNull()
  })

  it('submitComment 正向：必填项齐全且 success，提示并返回', async () => {
    const comp = (await import('@/views/UserViews/UserCommentsAdd.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock, $route: routeMock } } })
    await wrapper.setData({ comment: { type: 'good', detail: 'nice', image: null } })
    global.fetch.mockResolvedValue({ json: async () => ({ success: true }) })
    await wrapper.vm.submitComment()
    expect(global.fetch).toHaveBeenCalledTimes(1)
    expect(window.alert).toHaveBeenCalledWith('评论提交成功！')
    expect(routerMock.go).toHaveBeenCalledWith(-1)
  })

  it('submitComment 反向：缺少必填项直接提示且不请求', async () => {
    const comp = (await import('@/views/UserViews/UserCommentsAdd.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock, $route: routeMock } } })
    await wrapper.setData({ comment: { type: '', detail: '', image: null } })
    global.fetch.mockClear()
    await wrapper.vm.submitComment()
    expect(window.alert).toHaveBeenCalledWith('请填写完整信息')
    expect(global.fetch).not.toHaveBeenCalled()
  })
})