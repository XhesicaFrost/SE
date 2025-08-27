import { mount } from '@vue/test-utils'
import RiderOrdersView from '@/views/RiderViews/RiderOrdersView.vue'
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

describe('RiderOrdersView', () => {
  let wrapper
  let store
  let mockStartLocationTracking
  let mockStopLocationTracking

  const mockOrders = [
    {
      id: 1001,
      sellerName: '测试餐厅1',
      sellerAddress: '商家地址1',
      userAddress: '用户地址1',
      createTime: '2023-12-01 10:00:00',
      status: 'PENDING'
    },
    {
      id: 1002,
      sellerName: '测试餐厅2',
      sellerAddress: '商家地址2',
      userAddress: '用户地址2',
      createTime: '2023-12-01 11:00:00',
      status: 'PENDING'
    },
    {
      id: 1003,
      sellerName: '另一家餐厅',
      sellerAddress: '商家地址3',
      userAddress: '其他区域地址',
      createTime: '2023-12-01 12:00:00',
      status: 'PENDING'
    }
  ]

  beforeEach(() => {
    fetchWithTimeout.mockClear()
    global.console.error = jest.fn()
    global.console.log = jest.fn()
    global.alert = jest.fn()

    // 创建 mock actions
    mockStartLocationTracking = jest.fn()
    mockStopLocationTracking = jest.fn()

    // 创建 mock store
    store = createStore({
      modules: {
        userStore: {
          namespaced: true,
          state: {
            userInfo: { userId: 'rider123' }
          }
        },
        locationStore: {
          namespaced: true,
          actions: {
            startLocationTracking: mockStartLocationTracking,
            stopLocationTracking: mockStopLocationTracking
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

  test('正向：成功加载订单列表', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ 
        code: 200, 
        success: true,
        data: mockOrders
      })
    })

    wrapper = mount(RiderOrdersView, {
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

    expect(wrapper.vm.allOrders).toEqual(mockOrders)
    expect(wrapper.findAll('.order-item')).toHaveLength(3)
  })

  test('正向：搜索过滤功能正常工作', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ 
        code: 200, 
        success: true,
        data: mockOrders
      })
    })

    wrapper = mount(RiderOrdersView, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    // 测试商家名称过滤
    wrapper.vm.sellerNameFilter = '测试餐厅'
    expect(wrapper.vm.filteredOrders).toHaveLength(2)

    // 测试用户地址过滤
    wrapper.vm.sellerNameFilter = ''
    wrapper.vm.userAddressFilter = '用户地址'
    expect(wrapper.vm.filteredOrders).toHaveLength(2)

    // 测试组合过滤
    wrapper.vm.sellerNameFilter = '测试餐厅1'
    wrapper.vm.userAddressFilter = '用户地址1'
    expect(wrapper.vm.filteredOrders).toHaveLength(1)
    expect(wrapper.vm.filteredOrders[0].id).toBe(1001)
  })

  test('正向：分页功能正常工作', async () => {
    const manyOrders = Array.from({ length: 25 }, (_, i) => ({
      id: i + 1,
      sellerName: `餐厅${i + 1}`,
      sellerAddress: `地址${i + 1}`,
      userAddress: `用户地址${i + 1}`,
      createTime: '2023-12-01 10:00:00',
      status: 'PENDING'
    }))

    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ 
        code: 200, 
        success: true,
        data: manyOrders
      })
    })

    wrapper = mount(RiderOrdersView, {
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
    expect(wrapper.vm.pagedOrders).toHaveLength(10) // 第一页10个

    // 测试翻页
    wrapper.vm.changePage(2)
    expect(wrapper.vm.page).toBe(2)
    expect(wrapper.vm.jumpPage).toBe(2)
  })

  test('正向：成功抢单', async () => {
    fetchWithTimeout
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ 
          code: 200, 
          success: true,
          data: mockOrders
        })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ 
          code: 200, 
          success: true
        })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ 
          code: 200, 
          success: true,
          data: []
        })
      })

    wrapper = mount(RiderOrdersView, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    await wrapper.vm.grabOrder(1001)

    expect(fetchWithTimeout).toHaveBeenCalledWith(
      'http://test-api/rider/chooseorder',
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({ riderId: 'rider123', orderId: 1001 })
      })
    )
    expect(global.alert).toHaveBeenCalledWith('抢单成功！')
    expect(mockStartLocationTracking).toHaveBeenCalled()
  })

  test('正向：点击订单跳转详情页', async () => {
    const mockPush = jest.fn()

    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ 
        code: 200, 
        success: true,
        data: mockOrders
      })
    })

    wrapper = mount(RiderOrdersView, {
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

  test('正向：搜索和清空功能', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ 
        code: 200, 
        success: true,
        data: mockOrders
      })
    })

    wrapper = mount(RiderOrdersView, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    // 设置过滤条件
    wrapper.vm.sellerNameFilter = '测试'
    wrapper.vm.userAddressFilter = '地址'

    // 应用过滤
    await wrapper.vm.applyFilter()
    expect(wrapper.vm.page).toBe(1)
    expect(wrapper.vm.jumpPage).toBe(1)

    // 清空过滤
    await wrapper.vm.clearFilter()
    expect(wrapper.vm.sellerNameFilter).toBe('')
    expect(wrapper.vm.userAddressFilter).toBe('')
    expect(wrapper.vm.page).toBe(1)
  })

  test('反向：API错误时显示空列表', async () => {
    fetchWithTimeout.mockRejectedValue(new Error('网络错误'))

    wrapper = mount(RiderOrdersView, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    expect(wrapper.vm.allOrders).toEqual([])
    expect(wrapper.find('.empty-tip').exists()).toBe(true)
    expect(global.console.error).toHaveBeenCalledWith('获取订单列表失败:', expect.any(Error))
  })

  test('反向：抢单失败时显示错误信息', async () => {
    fetchWithTimeout
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ 
          code: 200, 
          success: true,
          data: mockOrders
        })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ 
          code: 400, 
          success: false,
          message: '该订单已被其他骑手接取'
        })
      })

    wrapper = mount(RiderOrdersView, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    await wrapper.vm.grabOrder(1001)

    expect(global.alert).toHaveBeenCalledWith('抢单失败：该订单已被其他骑手接取')
    expect(mockStartLocationTracking).not.toHaveBeenCalled()
  })

  test('反向：抢单网络错误', async () => {
    fetchWithTimeout
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ 
          code: 200, 
          success: true,
          data: mockOrders
        })
      })
      .mockRejectedValueOnce(new Error('网络错误'))

    wrapper = mount(RiderOrdersView, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    await wrapper.vm.grabOrder(1001)

    expect(global.alert).toHaveBeenCalledWith('网络错误，抢单失败')
    expect(global.console.error).toHaveBeenCalledWith('抢单网络错误:', expect.any(Error))
  })

  test('反向：分页边界测试', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ 
        code: 200, 
        success: true,
        data: mockOrders
      })
    })

    wrapper = mount(RiderOrdersView, {
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
    wrapper.vm.changePage(-1) // 小于1
    expect(wrapper.vm.page).toBe(1)

    wrapper.vm.changePage(999) // 超过总页数
    expect(wrapper.vm.page).toBe(1) // 总页数为1，所以应该是1
  })

  test('反向：空搜索结果', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ 
        code: 200, 
        success: true,
        data: mockOrders
      })
    })

    wrapper = mount(RiderOrdersView, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    // 设置不存在的搜索条件
    wrapper.vm.sellerNameFilter = '不存在的餐厅'
    
    expect(wrapper.vm.filteredOrders).toHaveLength(0)
    expect(wrapper.vm.pagedOrders).toHaveLength(0)
  })
})
