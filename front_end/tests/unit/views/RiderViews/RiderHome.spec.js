import { mount } from '@vue/test-utils'
import RiderHome from '@/views/RiderViews/RiderHome.vue'
import { createStore } from 'vuex'

// Mock dependencies
jest.mock('@/config.js', () => ({
  BASE_URL: 'http://test-api',
  fetchWithTimeout: jest.fn()
}))

jest.mock('@/components/bottomNav.vue', () => ({
  name: 'BottomNav',
  template: '<div class="mock-bottom-nav"></div>',
  props: ['navItems']
}))

const { fetchWithTimeout } = require('@/config.js')

// Mock geolocation
Object.defineProperty(global.navigator, 'geolocation', {
  value: {
    getCurrentPosition: jest.fn()
  },
  writable: true
})

describe('RiderHome', () => {
  let wrapper
  let store
  let mockLogout
  let mockStartLocationTracking
  let mockStopLocationTracking

  // 等待组件挂载后的两个初始化请求完成（已接订单 + 推荐订单）
  async function waitForInitialLoads(maxTries = 20) {
    for (let i = 0; i < maxTries; i++) {
      // 两个初始化请求都已发出
      if (fetchWithTimeout.mock.calls.length >= 2) break
      await new Promise(r => setTimeout(r, 20))
    }
    // 再给一次渲染与 Promise 结算机会
    await new Promise(r => setTimeout(r, 20))
  }

  const mockAcceptedOrders = [
    {
      id: 1001,
      sellerName: '测试餐厅1',
      sellerAddress: '商家地址1',
      userAddress: '用户地址1',
      createTime: '2023-12-01 10:00:00',
      status: 'ACCEPTED'
    }
  ]

  const mockRecommendedOrders = [
    {
      id: 1002,
      sellerName: '测试餐厅2',
      sellerAddress: '商家地址2',
      userAddress: '用户地址2',
      createTime: '2023-12-01 11:00:00',
      status: 'PENDING'
    }
  ]

  beforeEach(() => {
    fetchWithTimeout.mockClear()
    global.console.error = jest.fn()
    global.console.log = jest.fn()
    global.console.warn = jest.fn()
    global.alert = jest.fn()
    global.confirm = jest.fn(() => true)

    // 创建 mock actions
    mockLogout = jest.fn()
    mockStartLocationTracking = jest.fn()
    mockStopLocationTracking = jest.fn()

    // 创建 mock store
    store = createStore({
      modules: {
        userStore: {
          namespaced: true,
          state: {
            userInfo: { userId: 'rider123' }
          },
          actions: {
            logout: mockLogout
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

    // Mock geolocation
    global.navigator.geolocation.getCurrentPosition.mockImplementation((success) => {
      success({
        coords: { latitude: 40.7128, longitude: -74.0060 }
      })
    })
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  test('正向：成功加载已接订单和推荐订单', async () => {
    fetchWithTimeout
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ 
          code: 200, 
          success: true,
          data: mockAcceptedOrders
        })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ 
          code: 200, 
          success: true,
          data: mockRecommendedOrders
        })
      })

    wrapper = mount(RiderHome, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    // 等待所有异步操作完成
    await wrapper.vm.$nextTick()
    
    // 简化等待逻辑
    for (let i = 0; i < 5; i++) {
      await new Promise(resolve => setTimeout(resolve, 50))
      if (wrapper.vm.acceptedOrders.length > 0 || wrapper.vm.recommendedOrders.length > 0) {
        break
      }
    }

    expect(wrapper.vm.acceptedOrders).toEqual(mockAcceptedOrders)
    expect(wrapper.vm.recommendedOrders).toEqual(mockRecommendedOrders)
  }, 10000)

  test('正向：成功抢单', async () => {
    fetchWithTimeout
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: [] })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: mockRecommendedOrders })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, success: true })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: [mockAcceptedOrders[0]] })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: [] })
      })

    wrapper = mount(RiderHome, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await waitForInitialLoads()

    // 直接调用抢单方法
    await wrapper.vm.grabOrder(1002)

    expect(fetchWithTimeout).toHaveBeenCalledWith(
      'http://test-api/rider/chooseorder',
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({ riderId: 'rider123', orderId: 1002 })
      })
    )
    expect(global.alert).toHaveBeenCalledWith('抢单成功！')
  }, 10000)

  test('正向：成功更新订单状态', async () => {
    // 设置mock数据，确保组件先获取到已接订单
    wrapper = mount(RiderHome, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    // 设置订单数据
    wrapper.vm.acceptedOrders = mockAcceptedOrders

    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ code: 200, success: true })
    })

    // 直接调用更新状态方法
    await wrapper.vm.updateOrderStatus(1001)

    expect(fetchWithTimeout).toHaveBeenCalledWith(
      'http://test-api/rider/updateorder',
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({ 
          riderId: 'rider123', 
          orderId: 1001, 
          status: 'PICKED' 
        })
      })
    )
  })

  test('正向：提醒消息功能', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ 
        success: true,
        alertMessage: '测试提醒消息'
      })
    })

    wrapper = mount(RiderHome, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()

    // 清空之前的 alert 调用
    global.alert.mockClear()

    // 直接调用提醒消息方法
    await wrapper.vm.fetchAlertMessage()

    expect(global.alert).toHaveBeenCalledWith('骑手提醒：\n测试提醒消息')
  })

  test('正向：退出登录功能', async () => {
    fetchWithTimeout
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: [] })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: [] })
      })

    const mockPush = jest.fn()

    wrapper = mount(RiderHome, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: mockPush }
        }
      }
    })

    await wrapper.vm.$nextTick()

    // 直接调用退出登录方法
    await wrapper.vm.goTo('logout')

    expect(mockLogout).toHaveBeenCalled()
    expect(global.alert).toHaveBeenCalledWith('已成功退出登录')
    expect(mockPush).toHaveBeenCalledWith('/login')
  })

  test('反向：API错误时显示空列表', async () => {
    fetchWithTimeout.mockRejectedValue(new Error('网络错误'))

    wrapper = mount(RiderHome, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(wrapper.vm.acceptedOrders).toEqual([])
    expect(wrapper.vm.recommendedOrders).toEqual([])
    expect(global.console.error).toHaveBeenCalled()
  }, 10000)

  test('反向：抢单失败时显示错误信息', async () => {
    // 先设置初始化时的mock调用
    fetchWithTimeout
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: [] })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: [] })
      })

    wrapper = mount(RiderHome, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await waitForInitialLoads()

    // 清空之前的mock调用，确保下一次就是抢单接口
    fetchWithTimeout.mockClear()

    // 抢单失败返回
    fetchWithTimeout.mockResolvedValueOnce({
      json: () => Promise.resolve({
        code: 400,
        message: '该订单已被其他骑手接取'
      })
    })

    await wrapper.vm.grabOrder(1002)

    expect(global.alert).toHaveBeenCalledWith('抢单失败：该订单已被其他骑手接取')
  }, 10000)

  test('反向：地理位置获取失败', async () => {
    // Mock 地理位置失败
    global.navigator.geolocation.getCurrentPosition.mockImplementation((success, error) => {
      error(new Error('位置获取失败'))
    })

    fetchWithTimeout
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: [] })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: [] })
      })

    wrapper = mount(RiderHome, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    await wrapper.vm.refreshRecommendedOrders()

    expect(global.console.error).toHaveBeenCalledWith('获取推荐订单失败', expect.any(Error))
  })

  test('反向：组件销毁时停止定时器', async () => {
    wrapper = mount(RiderHome, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    // 检查定时器是否存在
    expect(wrapper.vm.alertTimer).toBeTruthy()

    wrapper.unmount()

    // 检查定时器是否被清理
    expect(wrapper.vm.alertTimer).toBeNull()
  })

  test('正向：刷新推荐订单功能', async () => {
    fetchWithTimeout
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: [] })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: [] })
      })

    wrapper = mount(RiderHome, {
      global: {
        plugins: [store],
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    // 重新设置 mock 以返回推荐订单
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ code: 200, data: mockRecommendedOrders })
    })

    await wrapper.vm.refreshRecommendedOrders()

    expect(wrapper.vm.recommendedOrders).toEqual(mockRecommendedOrders)
  })
})
