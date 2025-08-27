import { mount } from '@vue/test-utils'
import AdminShopManage from '@/views/AdminViews/AdminShopManage.vue'

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

describe('AdminShopManage', () => {
  let wrapper

  const mockShops = [
    {
      id: 1,
      name: '店铺1',
      address: '地址1',
      status: '正常',
      image: 'base64data1'
    },
    {
      id: 2,
      name: '店铺2',
      address: '地址2',
      status: '封禁中',
      image: 'base64data2'
    }
  ]

  beforeEach(() => {
    fetchWithTimeout.mockClear()
    global.alert = jest.fn()
    // Mock console.error to suppress logs
    global.console.error = jest.fn()
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  test('正向：成功加载店铺列表', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ code: 200, data: mockShops })
    })

    wrapper = mount(AdminShopManage, {
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    // 等待异步操作完成
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    expect(wrapper.vm.shops).toEqual(mockShops)
    expect(wrapper.findAll('.shop-item')).toHaveLength(2)
  })

  test('正向：分页功能正常工作', async () => {
    const manyShops = Array.from({ length: 20 }, (_, i) => ({
      id: i + 1,
      name: `店铺${i + 1}`,
      address: `地址${i + 1}`,
      status: '正常',
      image: 'base64data'
    }))

    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ code: 200, data: manyShops })
    })

    wrapper = mount(AdminShopManage, {
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    // 等待异步操作完成
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    // 验证分页
    expect(wrapper.vm.totalPages).toBe(3)
    expect(wrapper.vm.pagedShops).toHaveLength(8)

    // 测试翻页
    wrapper.vm.changePage(2)
    expect(wrapper.vm.page).toBe(2)
    expect(wrapper.vm.jumpPage).toBe(2)
  })

  test('正向：店铺状态切换功能', async () => {
    fetchWithTimeout
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: mockShops })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200 })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: mockShops })
      })

    wrapper = mount(AdminShopManage, {
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    await wrapper.vm.toggleShopStatus(1, '正常')

    expect(fetchWithTimeout).toHaveBeenCalledWith(
      'http://test-api/admin/shop/status',
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({ shopId: 1, status: 'disable' })
      })
    )
  })

  test('正向：编辑店铺导航', async () => {
    const mockPush = jest.fn()
    
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ code: 200, data: mockShops })
    })

    wrapper = mount(AdminShopManage, {
      global: {
        mocks: {
          $router: { push: mockPush }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    wrapper.vm.editShop(123)

    expect(mockPush).toHaveBeenCalledWith('/admin/shop/edit/123')
  })

  test('正向：查看店铺详情导航', async () => {
    const mockPush = jest.fn()
    
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ code: 200, data: mockShops })
    })

    wrapper = mount(AdminShopManage, {
      global: {
        mocks: {
          $router: { push: mockPush }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    wrapper.vm.viewShop(123)

    expect(mockPush).toHaveBeenCalledWith('/admin/shop/123')
  })

  test('正向：评论审核导航', async () => {
    const mockPush = jest.fn()
    
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ code: 200, data: mockShops })
    })

    wrapper = mount(AdminShopManage, {
      global: {
        mocks: {
          $router: { push: mockPush }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    wrapper.vm.reviewComments(123)

    expect(mockPush).toHaveBeenCalledWith('/admin/shop/comments/123')
  })

  test('反向：API错误时显示空列表', async () => {
    fetchWithTimeout.mockRejectedValue(new Error('网络错误'))

    wrapper = mount(AdminShopManage, {
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    expect(wrapper.vm.shops).toEqual([])
    expect(global.console.error).toHaveBeenCalledWith('获取店铺列表失败:', expect.any(Error))
  })

  test('反向：分页边界测试', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ code: 200, data: mockShops })
    })

    wrapper = mount(AdminShopManage, {
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    // 测试超出边界的页码
    wrapper.vm.changePage(-1)
    expect(wrapper.vm.page).toBe(1)

    wrapper.vm.changePage(999)
    expect(wrapper.vm.page).toBe(1) // 总页数为1
  })

  test('正向：图片URL处理', () => {
    wrapper = mount(AdminShopManage, {
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    expect(wrapper.vm.getImageUrl('')).toBe('')
    expect(wrapper.vm.getImageUrl('data:image/jpeg;base64,abc')).toBe('data:image/jpeg;base64,abc')
    expect(wrapper.vm.getImageUrl('base64datastring')).toBe('base64datastring')
  })
})
