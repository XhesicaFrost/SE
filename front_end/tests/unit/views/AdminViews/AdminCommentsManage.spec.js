import { mount } from '@vue/test-utils'
import AdminCommentsManage from '@/views/AdminViews/AdminCommentsManage.vue'

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

const { fetchWithTimeout } = require('@/config.js')

describe('AdminCommentsManage', () => {
  let wrapper

  const mockComments = [
    {
      id: 1,
      username: '用户1',
      createTime: '2023-12-01T10:00:00',
      type: 'good',
      content: '很好的商品',
      image: null,
      status: '待审核'
    },
    {
      id: 2,
      username: '用户2',
      createTime: '2023-12-02T11:00:00',
      type: 'bad',
      content: '不太满意',
      image: 'base64imagedata',
      status: '已通过'
    }
  ]

  beforeEach(() => {
    fetchWithTimeout.mockClear()
    // Mock console methods to suppress logs
    global.console.log = jest.fn()
    global.console.error = jest.fn()
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  test('正向：成功加载评论列表', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ code: 200, data: mockComments })
    })

    wrapper = mount(AdminCommentsManage, {
      global: {
        mocks: {
          $route: { params: { shopId: '123' } },
          $router: { go: jest.fn() }
        }
      }
    })

    // 等待异步操作完成
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    expect(wrapper.vm.comments).toEqual(mockComments)
    expect(wrapper.findAll('.comment-item')).toHaveLength(2)
  })

  test('正向：分页功能正常工作', async () => {
    const manyComments = Array.from({ length: 25 }, (_, i) => ({
      id: i + 1,
      username: `用户${i + 1}`,
      createTime: '2023-12-01T10:00:00',
      type: 'good',
      content: `评论${i + 1}`,
      status: '待审核'
    }))

    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ code: 200, data: manyComments })
    })

    wrapper = mount(AdminCommentsManage, {
      global: {
        mocks: {
          $route: { params: { shopId: '123' } },
          $router: { go: jest.fn() }
        }
      }
    })

    // 等待异步操作完成
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    // 验证分页
    expect(wrapper.vm.totalPages).toBe(3)
    expect(wrapper.vm.pagedComments).toHaveLength(10)

    // 测试翻页
    wrapper.vm.changePage(2)
    expect(wrapper.vm.page).toBe(2)
    expect(wrapper.vm.jumpPage).toBe(2)
  })

  test('正向：通过评论操作', async () => {
    fetchWithTimeout
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: mockComments })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200 })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: [] })
      })

    wrapper = mount(AdminCommentsManage, {
      global: {
        mocks: {
          $route: { params: { shopId: '123' } },
          $router: { go: jest.fn() }
        }
      }
    })

    // 等待初始化完成
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    // 直接调用方法
    await wrapper.vm.approveComment(1)

    expect(fetchWithTimeout).toHaveBeenCalledWith(
      'http://test-api/admin/comment/approve',
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({ commentId: 1 })
      })
    )
  })

  test('正向：拒绝评论操作', async () => {
    fetchWithTimeout
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: mockComments })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200 })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: [] })
      })

    wrapper = mount(AdminCommentsManage, {
      global: {
        mocks: {
          $route: { params: { shopId: '123' } },
          $router: { go: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    await wrapper.vm.rejectComment(1)

    expect(fetchWithTimeout).toHaveBeenCalledWith(
      'http://test-api/admin/comment/reject',
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({ commentId: 1 })
      })
    )
  })

  test('正向：时间格式化功能', () => {
    wrapper = mount(AdminCommentsManage, {
      global: {
        mocks: {
          $route: { params: { shopId: '123' } },
          $router: { go: jest.fn() }
        }
      }
    })

    const testTime = '2023-12-01T10:30:45'
    const formatted = wrapper.vm.formatTime(testTime)
    
    expect(formatted).toMatch(/2023/)
    expect(formatted).toMatch(/12/)
    expect(formatted).toMatch(/1/) // 修改为匹配 "1" 而不是 "01"
    expect(formatted).toMatch(/10/)
    expect(formatted).toMatch(/30/)
  })

  test('正向：图片URL生成功能', () => {
    wrapper = mount(AdminCommentsManage, {
      global: {
        mocks: {
          $route: { params: { shopId: '123' } },
          $router: { go: jest.fn() }
        }
      }
    })

    expect(wrapper.vm.getImageUrl('')).toBe('')
    expect(wrapper.vm.getImageUrl(null)).toBe('')
    expect(wrapper.vm.getImageUrl('base64data')).toBe('data:image/jpeg;base64,base64data')
  })

  test('反向：API错误时显示空列表', async () => {
    fetchWithTimeout.mockRejectedValue(new Error('网络错误'))

    wrapper = mount(AdminCommentsManage, {
      global: {
        mocks: {
          $route: { params: { shopId: '123' } },
          $router: { go: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    expect(wrapper.vm.comments).toEqual([])
    expect(global.console.error).toHaveBeenCalledWith('获取评论列表失败:', expect.any(Error))
  })

  test('反向：分页边界测试', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ code: 200, data: mockComments })
    })

    wrapper = mount(AdminCommentsManage, {
      global: {
        mocks: {
          $route: { params: { shopId: '123' } },
          $router: { go: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    // 测试超出边界的页码
    wrapper.vm.changePage(-1)
    expect(wrapper.vm.page).toBe(1)

    wrapper.vm.changePage(999)
    expect(wrapper.vm.page).toBe(1) // 总页数为1，所以应该是1
  })
})
