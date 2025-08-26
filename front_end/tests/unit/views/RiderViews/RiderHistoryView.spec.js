import { mount } from '@vue/test-utils'
import RiderHistoryView from '@/views/RiderViews/RiderHistoryView.vue'
import { createStore } from 'vuex'

// Mock dependencies
jest.mock('@/config.js', () => ({
  BASE_URL: 'http://test-api',
  fetchWithTimeout: jest.fn()
}))

jest.mock('@/components/topNav.vue', () => ({
  name: 'TopNav',
  template: '<div class="mock-top-nav"></div>',
  props: ['navInfo']
}))

jest.mock('@/components/bottomNav.vue', () => ({
  name: 'BottomNav',
  template: '<div class="mock-bottom-nav"></div>',
  props: ['navItems']
}))

const { fetchWithTimeout } = require('@/config.js')

describe('RiderHistoryView', () => {
  let wrapper
  let store

  const mockHistoryOrders = [
    {
      id: 1001,
      sellerName: '测试餐厅1',
      sellerAddress: '商家地址1',
      userAddress: '用户地址1',
      userPhone: '13800138001',
      createTime: '2023-12-01 10:00:00',
      completeTime: '2023-12-01 10:30:00',
      status: 'COMPLETED'
    },
    {
      id: 1002,
      sellerName: '测试餐厅2',
      sellerAddress: '商家地址2',
      userAddress: '用户地址2',
      userPhone: '13800138002',
      createTime: '2023-12-01 11:00:00',
      completeTime: '2023-12-01 11:30:00',
      status: 'COMPLETED'
    }
  ]

  beforeEach(() => {
    fetchWithTimeout.mockClear()
    global.console.error = jest.fn()
    global.console.log = jest.fn()

    // 创建 mock store
    store = createStore({
      modules: {
        userStore: {
          namespaced: true,
          state: {
            userInfo: { userId: 'rider123' }
          }
        }
      }
    })
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  test('正向：成功加载历史订单列表', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ 
        code: 200, 
        success: true,
        data: mockHistoryOrders,
        total: 2
      })
    })

    wrapper = mount(RiderHistoryView, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    // 等待异步操作完成
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    expect(wrapper.vm.historyOrders).toEqual(mockHistoryOrders)
    expect(wrapper.vm.totalCount).toBe(2)
    expect(wrapper.findAll('.history-item')).toHaveLength(2)
  })

  test('正向：点击订单跳转详情页', async () => {
    const mockPush = jest.fn()

    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ 
        code: 200, 
        success: true,
        data: mockHistoryOrders
      })
    })

    wrapper = mount(RiderHistoryView, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: mockPush }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    wrapper.vm.goToOrderDetail(1001)

    expect(mockPush).toHaveBeenCalledWith('/rider/order/1001')
  })

  test('正向：分页功能正常工作', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ 
        code: 200, 
        success: true,
        data: mockHistoryOrders,
        total: 25
      })
    })

    wrapper = mount(RiderHistoryView, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    expect(wrapper.vm.totalPages).toBe(3) // 25 orders / 10 per page = 3 pages

    // 测试翻页
    wrapper.vm.nextPage()
    expect(wrapper.vm.currentPage).toBe(2)

    wrapper.vm.prevPage()
    expect(wrapper.vm.currentPage).toBe(1)
  })

  test('正向：状态显示正确', () => {
    wrapper = mount(RiderHistoryView, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    expect(wrapper.vm.getStatusText('ACCEPTED')).toBe('已接单')
    expect(wrapper.vm.getStatusText('PICKED')).toBe('已取餐')
    expect(wrapper.vm.getStatusText('COMPLETED')).toBe('已完成')

    expect(wrapper.vm.getStatusClass('COMPLETED')).toBe('completed')
    expect(wrapper.vm.getStatusClass('ACCEPTED')).toBe('accepted')
  })

  test('反向：API错误时显示空状态', async () => {
    fetchWithTimeout.mockRejectedValue(new Error('网络错误'))

    wrapper = mount(RiderHistoryView, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    expect(wrapper.vm.historyOrders).toEqual([])
    expect(wrapper.vm.totalCount).toBe(0)
    expect(wrapper.find('.empty-state').exists()).toBe(true)
  })

  test('反向：用户ID不存在时跳过请求', async () => {
    // 创建没有用户信息的 store
    const emptyStore = createStore({
      modules: {
        userStore: {
          namespaced: true,
          state: {
            userInfo: {}
          }
        }
      }
    })

    wrapper = mount(RiderHistoryView, {
      global: {
        plugins: [emptyStore],
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    expect(fetchWithTimeout).not.toHaveBeenCalled()
    expect(global.console.error).toHaveBeenCalledWith('用户ID不存在')
  })

  test('反向：分页边界测试', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ 
        code: 200, 
        success: true,
        data: mockHistoryOrders,
        total: 2
      })
    })

    wrapper = mount(RiderHistoryView, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    // 测试边界情况
    wrapper.vm.prevPage() // 当前在第1页，不应该变化
    expect(wrapper.vm.currentPage).toBe(1)

    wrapper.vm.nextPage() // 只有1页，不应该变化
    expect(wrapper.vm.currentPage).toBe(1)
  })
})
