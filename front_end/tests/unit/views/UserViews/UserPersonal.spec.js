// filepath: g:/2025spring/SE/project/code/software-engineering-big-work/front_end/tests/unit/UserPersonal.spec.js
import { shallowMount } from '@vue/test-utils'
import { createStore } from 'vuex'

// stub
jest.mock('@/components/bottomNav.vue', () => ({ default: { name: 'BottomNav', render: () => null } }))

describe('UserPersonal.vue', () => {
  let store
  let routerMock
  let logoutAction

  beforeAll(() => {
    jest.spyOn(window, 'alert').mockImplementation(() => {})
    jest.spyOn(window, 'confirm').mockImplementation(() => true)
  })

  beforeEach(() => {
    jest.clearAllMocks()
    logoutAction = jest.fn().mockResolvedValue()
    store = createStore({
      modules: {
        userStore: {
          namespaced: true,
          state: () => ({ userInfo: { userName: '张三', userPhone: '138', image: 'img' } }),
          actions: { logout: logoutAction }
        }
      }
    })
    routerMock = { push: jest.fn() }
  })

  it('goToEdit 正向：跳转编辑资料', async () => {
    const comp = (await import('@/views/UserViews/UserPersonal.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })
    wrapper.vm.goToEdit()
    expect(routerMock.push).toHaveBeenCalledWith('/user/personal/edit')
  })

  it('goToAddress 正向：跳转我的地址', async () => {
    const comp = (await import('@/views/UserViews/UserPersonal.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })
    wrapper.vm.goToAddress()
    expect(routerMock.push).toHaveBeenCalledWith('/user/address')
  })

  it('goToOrders 正向：跳转历史订单', async () => {
    const comp = (await import('@/views/UserViews/UserPersonal.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })
    wrapper.vm.goToOrders()
    expect(routerMock.push).toHaveBeenCalledWith('/user/history')
  })

  it('goToService 正向：弹出提示', async () => {
    const comp = (await import('@/views/UserViews/UserPersonal.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store] } })
    wrapper.vm.goToService()
    expect(window.alert).toHaveBeenCalledWith('暂未开放')
  })

  it('goToPayment 正向：弹出提示', async () => {
    const comp = (await import('@/views/UserViews/UserPersonal.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store] } })
    wrapper.vm.goToPayment()
    expect(window.alert).toHaveBeenCalledWith('奶龙已经设置过了！')
  })

  it('goTologout 正向：确认后调用 action 并跳转', async () => {
    jest.spyOn(window, 'confirm').mockReturnValue(true)
    const comp = (await import('@/views/UserViews/UserPersonal.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })
    await wrapper.vm.goTologout()
    expect(logoutAction).toHaveBeenCalled()
    expect(window.alert).toHaveBeenCalledWith('已成功退出登录')
    expect(routerMock.push).toHaveBeenCalledWith('/login')
  })

  it('goTologout 反向：action 抛错提示失败', async () => {
    jest.spyOn(window, 'confirm').mockReturnValue(true)
    logoutAction.mockRejectedValue(new Error('bad'))
    const comp = (await import('@/views/UserViews/UserPersonal.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })
    await wrapper.vm.goTologout()
    expect(window.alert).toHaveBeenCalledWith('退出登录失败，请重试')
  })

  it('goTologout 反向：用户取消不触发登出与跳转', async () => {
    jest.spyOn(window, 'confirm').mockReturnValue(false)
    const comp = (await import('@/views/UserViews/UserPersonal.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })
    await wrapper.vm.goTologout()
    expect(logoutAction).not.toHaveBeenCalled()
    expect(routerMock.push).not.toHaveBeenCalled()
    expect(window.alert).not.toHaveBeenCalledWith('已成功退出登录')
  })

  it('方法存在性：导航与交互函数已定义', async () => {
    const comp = (await import('@/views/UserViews/UserPersonal.vue')).default
    const wrapper = shallowMount(comp, { global: { plugins: [store], mocks: { $router: routerMock } } })
    expect(typeof wrapper.vm.goToEdit).toBe('function')
    expect(typeof wrapper.vm.goToAddress).toBe('function')
    expect(typeof wrapper.vm.goToOrders).toBe('function')
    expect(typeof wrapper.vm.goToService).toBe('function')
    expect(typeof wrapper.vm.goToPayment).toBe('function')
    expect(typeof wrapper.vm.goTologout).toBe('function')
  })
})