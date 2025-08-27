import { shallowMount, mount } from '@vue/test-utils'
import { createStore } from 'vuex'
import SellerOrder from '@/views/SellerViews/SellerOrder.vue'

// stub 子组件
jest.mock('@/components/bottomNav.vue', () => ({ default: { name: 'BottomNav', render: () => null } }))
jest.mock('@/components/topNav.vue', () => ({ default: { name: 'TopNav', render: () => null } }))

jest.mock('@/config.js', () => ({
  BASE_URL: 'http://localhost:12345',
  fetchWithTimeout: jest.fn()
}))
import { fetchWithTimeout } from '@/config.js'

describe('SellerOrder.vue', () => {
  let store
  let routerMock

  beforeAll(() => {
    jest.useFakeTimers()
    if (!global.confirm) global.confirm = () => true
    if (!global.alert) global.alert = () => {}
    jest.spyOn(console, 'log').mockImplementation(() => {})
    jest.spyOn(console, 'warn').mockImplementation(() => {})
    jest.spyOn(console, 'error').mockImplementation(() => {})
  })

  beforeEach(() => {
    jest.clearAllMocks()
    routerMock = { push: jest.fn() }
    store = createStore({
      modules: {
        userStore: {
          namespaced: true,
          state: () => ({ userInfo: { userId: 'u-001' } })
        },
        sellerStore: {
          namespaced: true,
          state: () => ({ sellerId: 'seller-001' })
        }
      }
    })
    // 避免 mounted 初次获取/提醒报错
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200, data: [] }) })
  })

  afterEach(() => {
    jest.runOnlyPendingTimers()
    jest.clearAllTimers()
  })

  // shouldShowBadge
  it('shouldShowBadge 正向：有备餐订单且状态为全部显示红点', async () => {
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store], mocks: { $router: routerMock } } })
    await wrapper.setData({ orders: [{ id: 1, status: 'PREPARING' }] })
    expect(wrapper.vm.shouldShowBadge(null)).toBe(true)
  })

  it('shouldShowBadge 反向：无备餐订单不显示红点', async () => {
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    await wrapper.setData({ orders: [] })
    expect(wrapper.vm.shouldShowBadge(null)).toBe(false)
  })

  // getPreparingOrderCount
  it('getPreparingOrderCount 正向：统计数量正确', async () => {
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    await wrapper.setData({ orders: [{ status: 'PREPARING' }, { status: 'READY' }, { status: 'PREPARING' }] })
    expect(wrapper.vm.getPreparingOrderCount()).toBe(2)
  })

  it('getPreparingOrderCount 反向：无备餐返回 0', async () => {
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    await wrapper.setData({ orders: [{ status: 'READY' }] })
    expect(wrapper.vm.getPreparingOrderCount()).toBe(0)
  })

  // selectStatus
  it('selectStatus 正向：选择 READY 并重置分页', async () => {
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    await wrapper.setData({ page: 3, jumpPage: 3 })
    wrapper.vm.selectStatus('READY')
    expect(wrapper.vm.selectedStatus).toBe('READY')
    expect(wrapper.vm.page).toBe(1)
    expect(wrapper.vm.jumpPage).toBe(1)
  })

  it('selectStatus 反向：选择全部(null)并重置分页', async () => {
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    await wrapper.setData({ page: 2, jumpPage: 2 })
    wrapper.vm.selectStatus(null)
    expect(wrapper.vm.selectedStatus).toBeNull()
    expect(wrapper.vm.page).toBe(1)
  })

  // getStatusLabel
  it('getStatusLabel 正向：映射 READY -> 已出餐', () => {
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    expect(wrapper.vm.getStatusLabel('READY')).toBe('已出餐')
  })

  it('getStatusLabel 反向：未知状态原样返回', () => {
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    expect(wrapper.vm.getStatusLabel('FOO')).toBe('FOO')
  })

  // getActionButtonText
  it('getActionButtonText 正向：PREPARING -> 出餐', () => {
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    expect(wrapper.vm.getActionButtonText('PREPARING')).toBe('出餐')
  })

  it('getActionButtonText 反向：未知 -> 查看', () => {
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    expect(wrapper.vm.getActionButtonText('XXX')).toBe('查看')
  })

  // getStatusButtonClass
  it('getStatusButtonClass 正向：PREPARING -> action-available', () => {
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    expect(wrapper.vm.getStatusButtonClass('PREPARING')).toBe('action-available')
  })

  it('getStatusButtonClass 反向：未知 -> default', () => {
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    expect(wrapper.vm.getStatusButtonClass('XXX')).toBe('default')
  })

  // isActionable
  it('isActionable 正向：PREPARING 可操作', () => {
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    expect(wrapper.vm.isActionable('PREPARING')).toBe(true)
  })

  it('isActionable 反向：READY 不可操作', () => {
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    expect(wrapper.vm.isActionable('READY')).toBe(false)
  })

  // handleOrderAction
  it('handleOrderAction 正向：PREPARING 触发 serveOrder', async () => {
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    wrapper.vm.serveOrder = jest.fn()
    wrapper.vm.handleOrderAction({ id: 1, status: 'PREPARING' })
    expect(wrapper.vm.serveOrder).toHaveBeenCalledWith(1)
  })

  it('handleOrderAction 反向：非 PREPARING 不触发 serveOrder', () => {
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    wrapper.vm.serveOrder = jest.fn()
    wrapper.vm.handleOrderAction({ id: 1, status: 'READY' })
    expect(wrapper.vm.serveOrder).not.toHaveBeenCalled()
  })

  // fetchOrders
  it('fetchOrders 正向：sellerId 存在且 200 时填充订单', async () => {
    const wrapper = mount(SellerOrder, { global: { plugins: [store] } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({
      json: async () => ({ code: 200, data: [{ id: 1, status: 'PREPARING', items: [] }] })
    })

    await wrapper.vm.fetchOrders()
    // 组件内部可能会额外请求一次，这里仅断言至少调用一次
    expect(fetchWithTimeout.mock.calls.length).toBeGreaterThanOrEqual(1)
    expect(wrapper.vm.orders).toHaveLength(1)
  })

  it('fetchOrders 反向：sellerId 为空直接返回且不请求', async () => {
    const emptyStore = createStore({
      modules: {
        userStore: { namespaced: true, state: () => ({ userInfo: { userId: '' } }) },
        sellerStore: { namespaced: true, state: () => ({ sellerId: '' }) }
      }
    })
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [emptyStore] } })
    fetchWithTimeout.mockClear()

    await wrapper.vm.fetchOrders()
    expect(wrapper.vm.orders).toEqual([])
    expect(fetchWithTimeout).not.toHaveBeenCalled()
  })

  // serveOrder
  it('serveOrder 正向：成功提示并刷新列表', async () => {
    const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {})
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    wrapper.vm.fetchOrders = jest.fn().mockResolvedValue()

    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ success: true }) })

    await wrapper.vm.serveOrder(123)

    expect(alertSpy).toHaveBeenCalled()
    expect(wrapper.vm.fetchOrders).toHaveBeenCalled()
  })

  it('serveOrder 反向：失败提示不刷新列表', async () => {
    const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {})
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    wrapper.vm.fetchOrders = jest.fn()

    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 500, message: 'bad' }) })

    await wrapper.vm.serveOrder(123)

    expect(alertSpy).toHaveBeenCalled()
    expect(wrapper.vm.fetchOrders).not.toHaveBeenCalled()
  })

  // fetchAlertMessage
  it('fetchAlertMessage 正向：有提醒消息弹窗', async () => {
    const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {})
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({
      json: async () => ({ success: true, alertMessage: 'Hi' })
    })

    await wrapper.vm.fetchAlertMessage()
    expect(alertSpy).toHaveBeenCalled()
  })

  it('fetchAlertMessage 反向：无 userId 直接返回', async () => {
    const emptyStore = createStore({
      modules: {
        userStore: { namespaced: true, state: () => ({ userInfo: { userId: '' } }) },
        sellerStore: { namespaced: true, state: () => ({ sellerId: '' }) }
      }
    })
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [emptyStore] } })
    fetchWithTimeout.mockClear()

    await wrapper.vm.fetchAlertMessage()
    expect(fetchWithTimeout).not.toHaveBeenCalled()
  })

  // startAlertTimer / stopAlertTimer
  it('startAlertTimer 正向：立即触发一次并定时触发', () => {
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    wrapper.vm.fetchAlertMessage = jest.fn()

    wrapper.vm.startAlertTimer()
    expect(wrapper.vm.fetchAlertMessage).toHaveBeenCalledTimes(1)

    jest.advanceTimersByTime(5000)
    expect(wrapper.vm.fetchAlertMessage).toHaveBeenCalledTimes(2)
  })

  it('stopAlertTimer 反向：停止后不再触发', () => {
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    wrapper.vm.fetchAlertMessage = jest.fn()

    wrapper.vm.startAlertTimer()
    jest.advanceTimersByTime(5000)
    wrapper.vm.stopAlertTimer()
    const called = wrapper.vm.fetchAlertMessage.mock.calls.length

    jest.advanceTimersByTime(10000)
    expect(wrapper.vm.fetchAlertMessage.mock.calls.length).toBe(called)
  })

  // changePage
  it('changePage 正向：跳转到第一页并同步 jumpPage', async () => {
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    // 构造 25 条，确保有分页
    await wrapper.setData({ orders: Array.from({ length: 25 }, (_, i) => ({ id: i + 1, status: 'READY' })) })
    wrapper.vm.changePage(1)
    expect(wrapper.vm.page).toBe(1)
    expect(wrapper.vm.jumpPage).toBe(1)
  })

  it('changePage 反向：越界页码被夹取到 1', async () => {
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    await wrapper.setData({ orders: Array.from({ length: 25 }, (_, i) => ({ id: i + 1, status: 'READY' })) })
    wrapper.vm.changePage(999)
    expect(wrapper.vm.page).toBe(1)
    wrapper.vm.changePage(0)
    expect(wrapper.vm.page).toBe(1)
  })

  // toggleExpand
  it('toggleExpand 正向：展开指定订单', () => {
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    wrapper.vm.toggleExpand(10)
    expect(wrapper.vm.expandedOrderId).toBe(10)
  })

  it('toggleExpand 反向：再次点击同一订单则收起', () => {
    const wrapper = shallowMount(SellerOrder, { global: { plugins: [store] } })
    wrapper.vm.toggleExpand(10)
    wrapper.vm.toggleExpand(10)
    expect(wrapper.vm.expandedOrderId).toBeNull()
  })
})