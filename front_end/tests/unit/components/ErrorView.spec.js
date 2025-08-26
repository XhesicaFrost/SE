import { mount } from '@vue/test-utils'
import ErrorView from '@/ErrorView.vue'

// Mock 静态资源
jest.mock('@/assets/logo.jpg', () => 'mocked-logo.jpg')

describe('ErrorView', () => {
  beforeEach(() => {
    jest.useFakeTimers()
  })
  afterEach(() => {
    jest.useRealTimers()
    jest.clearAllMocks()
  })

  test('正向：倒计时结束后调用路由回退', async () => {
    const back = jest.fn()
    const wrapper = mount(ErrorView, {
      global: {
        mocks: { $router: { back } }
      }
    })

    // 初始为 3s
    expect(wrapper.find('.countdown').text()).toContain('3s')
    // 1 秒 -> 2s
    jest.advanceTimersByTime(1000)
    await wrapper.vm.$nextTick()
    expect(wrapper.find('.countdown').text()).toContain('2s')

    // 再过 2 秒 -> 触发 back
    jest.advanceTimersByTime(2000)
    await wrapper.vm.$nextTick()

    expect(back).toHaveBeenCalledTimes(1)
  })

  test('反向：卸载后不再回退（定时器已清理）', async () => {
    const back = jest.fn()
    const wrapper = mount(ErrorView, {
      global: {
        mocks: { $router: { back } }
      }
    })

    // 先走 1 秒，尚未回退
    jest.advanceTimersByTime(1000)
    await wrapper.vm.$nextTick()
    expect(back).not.toHaveBeenCalled()

    // 卸载组件应清理定时器
    wrapper.unmount()

    // 继续推进时间也不应再触发回退
    jest.advanceTimersByTime(5000)
    await Promise.resolve()
    expect(back).not.toHaveBeenCalled()
  })
})
