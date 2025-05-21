import { BASE_URL, fetchWithTimeout } from '@/config.js'



export default {
  namespaced: true,
  state: () => ({
    userInfo: {
      userId: '',      // 用户ID
      userName: '',    // 用户名
      userKind: '',    // 用户类型
      userPhone: ''    // 用户手机号
    },
    errorMessage: ''
  }),
  mutations: {
    SET_USER_INFO(state, payload) {
      state.userInfo = payload
    },
    SET_ERROR(state, message) {
      state.errorMessage = message
    }
  },
  actions: {
    /**
     * registerUser
     * 用户注册方法，向后端 /register 接口发送注册请求。
     *
     * 作用：注册新用户，提交用户名、手机号、密码、用户类型等信息，返回注册结果。
     *
     * 传入参数 userData（对象）格式：
     * {
     *   username: string,   // 用户名，必填
     *   phone: string,      // 用户手机号，必填
     *   password: string,   // 用户密码，必填
     *   userKind: string    // 用户类型（user/rider/merchant），必填
     * }
     *
     * 返回值格式：
     * {
     *   code: number,       // 状态码，如 200 表示成功
     *   success: boolean,   // 是否成功
     *   id: string          // 用户 ID
     * }
     */
    async registerUser({ commit }, userData) {
      try {
        const params = new URLSearchParams(userData).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/register?${params}`)
        const result = await response.json()
        if (result.success && result.code === 200) {
          // 保存userId、userName、userKind、userPhone
          commit('SET_USER_INFO', {
            userId: result.id || '',
            userName: userData.username || '',
            userKind: userData.userKind || '',
            userPhone: userData.phone || ''
          })
          commit('SET_ERROR', '')
          return { code: result.code, success: true, id: result.id }
        } else {
          let msg = result.message || 'Registration failed'
          if (result.code === 500) {
            msg += '，请检查网络连接'
          }
          commit('SET_ERROR', msg)
          return { code: result.code || 500, success: false }
        }
      } catch (error) {
        let msg = 'Registration failed'
        if (error.name === 'AbortError') {
          msg = '请求超时，请检查网络连接'
        } else {
          msg += '，请检查网络连接'
        }
        commit('SET_ERROR', msg)
        return { code: 500, success: false }
      }
    },

    /**
     * loginUser
     * 用户登录方法，向后端 /login 接口发送登录请求。
     *
     * 作用：用户登录，提交手机号、密码、用户类型等信息，返回登录结果。
     *
     * 传入参数 loginData（对象）格式：
     * {
     *   username: string,   // 用户名，必填
     *   phone: string,      // 用户手机号，必填
     *   password: string,   // 用户密码，必填
     *   userKind: string    // 用户类型（user/rider/merchant），必填
     * }
     *
     * 返回值格式：
     * {
     *   code: number,       // 状态码，如 200 表示成功
     *   success: boolean,   // 是否成功
     *   id: string          // 用户 ID
     * }
     */
    async loginUser({ commit }, loginData) {
      try {
        const params = new URLSearchParams(loginData).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/login?${params}`)
        const result = await response.json()
        if (result.success && result.code === 200) {
          // 保存userId、userName、userKind、userPhone
          commit('SET_USER_INFO', {
            userId: result.id || '',
            userName: loginData.username || '',
            userKind: loginData.userKind || '',
            userPhone: loginData.phone || ''
          })
          commit('SET_ERROR', '')
          return { code: result.code, success: true, id: result.id }
        } else {
          let msg = result.message || 'Login failed'
          if (result.code === 500) {
            msg += '，请检查网络连接'
          }
          commit('SET_ERROR', msg)
          return { code: result.code || 500, success: false }
        }
      } catch (error) {
        let msg = 'Login failed'
        if (error.name === 'AbortError') {
          msg = '请求超时，请检查网络连接'
        } else {
          msg += '，请检查网络连接'
        }
        commit('SET_ERROR', msg)
        return { code: 500, success: false }
      }
    }
  }
}