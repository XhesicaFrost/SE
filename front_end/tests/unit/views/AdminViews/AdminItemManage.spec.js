import { mount } from '@vue/test-utils'
import AdminItemManage from '@/views/AdminViews/AdminItemManage.vue'

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

describe('AdminItemManage', () => {
  let wrapper

  const mockItems = [
    {
      id: 1,
      name: '商品1',
      price: 25.99,
      shopId: 1,
      status: '正常',
      image: 'base64data1'
    },
    {
      id: 2,
      name: '商品2',
      price: 35.99,
      shopId: 2,
      status: '下架',
      image: 'base64data2'
    }
  ]

  const mockShops = [
    { id: 1, name: '店铺1' },
    { id: 2, name: '店铺2' }
  ]

  beforeEach(() => {
    fetchWithTimeout.mockClear()
    global.alert = jest.fn()
    // Mock console.error to suppress error logs in tests
    global.console.error = jest.fn()
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  test('正向：成功加载商品和店铺列表', async () => {
    fetchWithTimeout
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: mockItems })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: mockShops })
      })

    wrapper = mount(AdminItemManage, {
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    // 等待两个异步请求完成
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    expect(wrapper.vm.items).toEqual(mockItems)
    expect(wrapper.vm.shops).toEqual(mockShops)
    expect(wrapper.findAll('.item-card')).toHaveLength(2)
  })

  test('正向：搜索功能正常工作', async () => {
    fetchWithTimeout
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: mockItems })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: mockShops })
      })

    wrapper = mount(AdminItemManage, {
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    // 测试搜索
    await wrapper.find('.search-input').setValue('商品1')

    expect(wrapper.vm.filteredItems).toHaveLength(1)
    expect(wrapper.vm.filteredItems[0].name).toBe('商品1')
  })

  test('正向：店铺筛选功能正常工作', async () => {
    fetchWithTimeout
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: mockItems })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: mockShops })
      })

    wrapper = mount(AdminItemManage, {
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    // 设置店铺筛选
    wrapper.vm.shopFilter = '1'
    await wrapper.vm.$nextTick()

    expect(wrapper.vm.filteredItems).toHaveLength(1)
    expect(wrapper.vm.filteredItems[0].shopId).toBe(1)
  })

  test('正向：商品状态切换功能', async () => {
    fetchWithTimeout
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: mockItems })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: mockShops })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200 })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: mockItems })
      })

    wrapper = mount(AdminItemManage, {
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    await wrapper.vm.toggleItemStatus(1, '正常')

    expect(fetchWithTimeout).toHaveBeenCalledWith(
      'http://test-api/admin/item/status?itemId=1&status=disable',
      expect.objectContaining({
        method: 'POST'
      })
    )
  })

  test('反向：API错误时显示空列表', async () => {
    fetchWithTimeout.mockRejectedValue(new Error('网络错误'))

    wrapper = mount(AdminItemManage, {
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    expect(wrapper.vm.items).toEqual([])
    expect(wrapper.vm.shops).toEqual([])
    expect(wrapper.find('.empty-tip').exists()).toBe(true)
    
    // 验证错误日志被调用
    expect(global.console.error).toHaveBeenCalledWith('获取商品列表失败:', expect.any(Error))
    expect(global.console.error).toHaveBeenCalledWith('获取店铺列表失败:', expect.any(Error))
  })

  test('正向：编辑商品导航', async () => {
    const mockPush = jest.fn()
    
    fetchWithTimeout
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: mockItems })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: mockShops })
      })

    wrapper = mount(AdminItemManage, {
      global: {
        mocks: {
          $router: { push: mockPush }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    wrapper.vm.editItem(123)

    expect(mockPush).toHaveBeenCalledWith('/admin/item/edit/123')
  })

  test('正向：店铺名称显示功能', async () => {
    fetchWithTimeout
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: mockItems })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: mockShops })
      })

    wrapper = mount(AdminItemManage, {
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    expect(wrapper.vm.getShopName(1)).toBe('店铺1')
    expect(wrapper.vm.getShopName(2)).toBe('店铺2')
    expect(wrapper.vm.getShopName(999)).toBe('未知店铺')
  })
})
