import { mount } from '@vue/test-utils'
import AdminOrderManage from '@/views/AdminViews/AdminOrderManage.vue'

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

describe('AdminOrderManage', () => {
  let wrapper

  const mockOrders = [
    {
      id: 1001,
      shopName: '测试店铺1',
      createTime: new Date('2023-12-01T10:00:00').getTime(),
      totalAmount: 89.99,
      status: 'pending',
      items: [
        { name: '商品A', quantity: 2 },
        { name: '商品B', quantity: 1 }
      ]
    },
    {
      id: 1002,
      shopName: '测试店铺2',
      createTime: new Date('2023-12-02T15:30:00').getTime(),
      totalAmount: 45.50,
      status: 'completed',
      items: [
        { name: '商品C', quantity: 1 }
      ]
    }
  ]

  beforeEach(() => {
    fetchWithTimeout.mockClear()
    // Mock console.error to suppress logs
    global.console.error = jest.fn()
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  test('正向：成功加载订单列表', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ success: true, data: mockOrders })
    })

    wrapper = mount(AdminOrderManage, {
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    // 等待异步操作完成
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    expect(wrapper.vm.orders).toEqual(mockOrders)
    expect(wrapper.findAll('.order-card')).toHaveLength(2)
  })

  test('正向：状态筛选功能正常工作', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ success: true, data: mockOrders })
    })

    wrapper = mount(AdminOrderManage, {
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    // 测试状态筛选
    await wrapper.find('.filter-select').setValue('pending')

    expect(wrapper.vm.filteredOrders).toHaveLength(1)
    expect(wrapper.vm.filteredOrders[0].status).toBe('pending')
  })

  test('正向：日期筛选功能正常工作', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ success: true, data: mockOrders })
    })

    wrapper = mount(AdminOrderManage, {
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    // 设置日期范围
    wrapper.vm.startDate = '2023-12-01'
    wrapper.vm.endDate = '2023-12-01'
    await wrapper.vm.$nextTick()

    expect(wrapper.vm.filteredOrders).toHaveLength(1)
    expect(wrapper.vm.filteredOrders[0].id).toBe(1001)
  })

  test('正向：状态文本显示正确', () => {
    wrapper = mount(AdminOrderManage, {
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    expect(wrapper.vm.getStatusText('pending')).toBe('待处理')
    expect(wrapper.vm.getStatusText('preparing')).toBe('准备中')
    expect(wrapper.vm.getStatusText('completed')).toBe('已完成')
    expect(wrapper.vm.getStatusText('cancelled')).toBe('已取消')
  })

  test('正向：时间格式化正确', () => {
    wrapper = mount(AdminOrderManage, {
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    const timestamp = new Date('2023-12-01T10:30:45').getTime()
    const formatted = wrapper.vm.formatDate(timestamp)

    expect(formatted).toBe('2023-12-01 10:30')
  })

  test('正向：商品摘要显示正确', () => {
    wrapper = mount(AdminOrderManage, {
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    const items = [
      { name: '商品A', quantity: 2 },
      { name: '商品B', quantity: 1 }
    ]

    const summary = wrapper.vm.getItemSummary(items)
    expect(summary).toBe('商品A × 2，商品B × 1')
  })

  test('正向：点击订单卡片跳转详情页', async () => {
    const mockPush = jest.fn()
    
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ success: true, data: mockOrders })
    })

    wrapper = mount(AdminOrderManage, {
      global: {
        mocks: {
          $router: { push: mockPush }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    await wrapper.find('.order-card').trigger('click')

    expect(mockPush).toHaveBeenCalledWith('/admin/order/1001')
  })

  test('反向：API错误时显示空列表', async () => {
    fetchWithTimeout.mockRejectedValue(new Error('网络错误'))

    wrapper = mount(AdminOrderManage, {
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    expect(wrapper.vm.orders).toEqual([])
    expect(wrapper.find('.empty-tip').exists()).toBe(true)
    expect(global.console.error).toHaveBeenCalledWith('获取订单列表失败:', expect.any(Error))
  })

  test('正向：状态样式类正确', () => {
    wrapper = mount(AdminOrderManage, {
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    expect(wrapper.vm.getStatusClass('pending')).toBe('status-pending')
    expect(wrapper.vm.getStatusClass('preparing')).toBe('status-preparing')
    expect(wrapper.vm.getStatusClass('completed')).toBe('status-completed')
    expect(wrapper.vm.getStatusClass('cancelled')).toBe('status-cancelled')
    expect(wrapper.vm.getStatusClass('unknown')).toBe('')
  })

  test('正向：综合筛选功能', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ success: true, data: mockOrders })
    })

    wrapper = mount(AdminOrderManage, {
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    // 同时设置状态和日期筛选
    wrapper.vm.statusFilter = 'pending'
    wrapper.vm.startDate = '2023-12-01'
    wrapper.vm.endDate = '2023-12-01'
    await wrapper.vm.$nextTick()

    expect(wrapper.vm.filteredOrders).toHaveLength(1)
    expect(wrapper.vm.filteredOrders[0].id).toBe(1001)
    expect(wrapper.vm.filteredOrders[0].status).toBe('pending')
  })
})
