import userStore from '@/store/modules/userStore.js'

describe('userStore mutations', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  test('SET_USER_INFO: 正常设置用户信息并持久化', () => {
    const state = { userInfo: {}, errorMessage: '' }
    const payload = {
      userId: '1',
      userName: '张三',
      userKind: 'buyer',
      userPhone: '12345678901',
      token: 'token123',
      userImage: 'data:image/jpeg;base64,abcde'
    }
    userStore.mutations.SET_USER_INFO(state, payload)
    expect(state.userInfo.userId).toBe('1')
    expect(state.userInfo.userName).toBe('张三')
    expect(localStorage.getItem('userInfo')).toContain('张三')
    expect(localStorage.getItem('token')).toBe('token123')
  })

  test('SET_USER_INFO: 头像为纯base64字符串时自动加前缀', () => {
    const state = { userInfo: {}, errorMessage: '' }
    const payload = { userImage: 'abcde' }
    userStore.mutations.SET_USER_INFO(state, payload)
    expect(state.userInfo.userImage.startsWith('data:image/jpeg;base64,')).toBe(true)
  })

  test('SET_ERROR: 正常设置错误信息并持久化', () => {
    const state = { userInfo: {}, errorMessage: '' }
    userStore.mutations.SET_ERROR(state, '错误信息')
    expect(state.errorMessage).toBe('错误信息')
    expect(localStorage.getItem('errorMessage')).toBe('错误信息')
  })

  test('CLEAR_USER_INFO: 清空用户信息和本地存储', () => {
    const state = { userInfo: { userId: '1' }, errorMessage: 'err' }
    localStorage.setItem('userInfo', '{"userId":"1"}')
    localStorage.setItem('token', 'token123')
    localStorage.setItem('errorMessage', 'err')
    userStore.mutations.CLEAR_USER_INFO(state)
    expect(state.userInfo.userId).toBe('')
    expect(localStorage.getItem('userInfo')).toBeNull()
    expect(localStorage.getItem('token')).toBeNull()
    expect(localStorage.getItem('errorMessage')).toBeNull()
  })
})

describe('userStore actions', () => {
  test('updateUserInfo: 头像为纯base64字符串时自动加前缀', () => {
    const commit = jest.fn()
    const state = { userInfo: {} }
    userStore.actions.updateUserInfo({ commit, state }, { userImage: 'abcde' })
    expect(commit).toHaveBeenCalledWith('SET_USER_INFO', expect.objectContaining({
      userImage: expect.stringMatching(/^data:image\/jpeg;base64,/)
    }))
  })
})