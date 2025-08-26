import riderStore from '@/store/modules/riderStore.js'

// Mock 全局函数
global.setInterval = jest.fn()
global.clearInterval = jest.fn()

// 正确 Mock navigator.geolocation
const mockGeolocation = {
  getCurrentPosition: jest.fn()
}
Object.defineProperty(global.navigator, 'geolocation', {
  value: mockGeolocation,
  writable: true
})

// Mock fetchWithTimeout
jest.mock('@/config.js', () => ({
  BASE_URL: 'http://test-api',
  fetchWithTimeout: jest.fn()
}))

describe('riderStore actions', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  test('startLocationTracking: 正常启动追踪并定时上报', async () => {
    const commit = jest.fn()
    const rootState = { userStore: { userId: 123 } }
    const state = { isLocationTracking: false, locationTimer: null }

    // Mock setInterval 返回定时器ID
    const timerId = 456
    global.setInterval.mockReturnValue(timerId)

    // Mock getCurrentPosition 成功
    mockGeolocation.getCurrentPosition.mockImplementation((success) => {
      success({
        coords: { latitude: 40.7128, longitude: -74.0060 }
      })
    })

    // 调用 action
    await riderStore.actions.startLocationTracking({ commit, rootState, state })

    // 验证状态更新
    expect(commit).toHaveBeenCalledWith('SET_LOCATION_TRACKING', true)
    expect(commit).toHaveBeenCalledWith('SET_LOCATION_TIMER', timerId)
    expect(global.setInterval).toHaveBeenCalledWith(expect.any(Function), 10000)
  })

  test('startLocationTracking: 已在追踪时不重复启动', () => {
    const commit = jest.fn()
    const rootState = { userStore: { userId: 123 } }
    const state = { isLocationTracking: true, locationTimer: 123 }

    riderStore.actions.startLocationTracking({ commit, rootState, state })

    expect(commit).not.toHaveBeenCalled()
    expect(global.setInterval).not.toHaveBeenCalled()
  })

  test('stopLocationTracking: 正常停止追踪并清理定时器', () => {
    const commit = jest.fn()
    const state = { locationTimer: 111, isLocationTracking: true }

    riderStore.actions.stopLocationTracking({ commit, state })

    expect(global.clearInterval).toHaveBeenCalledWith(111)
    expect(commit).toHaveBeenCalledWith('SET_LOCATION_TIMER', null)
    expect(commit).toHaveBeenCalledWith('SET_LOCATION_TRACKING', false)
  })

  test('stopLocationTracking: 无定时器时也能正常执行', () => {
    const commit = jest.fn()
    const state = { locationTimer: null, isLocationTracking: false }

    riderStore.actions.stopLocationTracking({ commit, state })

    expect(global.clearInterval).not.toHaveBeenCalled()
    expect(commit).toHaveBeenCalledWith('SET_LOCATION_TIMER', null)
    expect(commit).toHaveBeenCalledWith('SET_LOCATION_TRACKING', false)
  })
})

describe('riderStore mutations', () => {
  test('SET_LOCATION_TIMER: 设置定时器ID', () => {
    const state = { locationTimer: null }
    const timerId = 123

    riderStore.mutations.SET_LOCATION_TIMER(state, timerId)

    expect(state.locationTimer).toBe(timerId)
  })

  test('SET_LOCATION_TRACKING: 设置追踪状态', () => {
    const state = { isLocationTracking: false }

    riderStore.mutations.SET_LOCATION_TRACKING(state, true)

    expect(state.isLocationTracking).toBe(true)
  })
})