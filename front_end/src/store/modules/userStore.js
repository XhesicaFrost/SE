import { BASE_URL, fetchWithTimeout } from '@/config.js'

export default {
  namespaced: true,
  state: () => ({
    userInfo: {
      userId: '',      // 用户ID
      userName: '',    // 用户名
      userKind: '',    // 用户类型
      userPhone: '',   // 用户手机号
      token: ''        // 用户令牌
    },
    errorMessage: ''
  }),
  mutations: {
    SET_USER_INFO(state, payload) {
      // 使用 Object.assign 来确保响应式更新
      Object.assign(state.userInfo, payload)
    },
    SET_ERROR(state, message) {
      state.errorMessage = message
    },
    CLEAR_USER_INFO(state) {
      state.userInfo = {
        userId: '',
        userName: '',
        userKind: '',
        userPhone: '',
        token: ''
      }
    }
  },
  actions: {
    /**
     * registerUser
     * 用户注册方法，向后端 /register 接口发送注册请求。
     */
    async registerUser({ commit }, userData) {
      try {
        const params = new URLSearchParams(userData).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/register?${params}`)
        const result = await response.json()
        if (result.code === 200) {
          // 保存用户信息和 token
          commit('SET_USER_INFO', {
            userId: result.id || result.data?.id || '',
            userName: userData.username || result.data?.username || '',
            userKind: userData.role || result.data?.role || '',
            userPhone: userData.phonenumber || result.data?.phonenumber || '',
            token: result.data?.token || result.token || '' // 保存后端返回的 token
          })
          commit('SET_ERROR', '')
          return { 
            code: result.code, 
            success: true, 
            id: result.id,
            data: {
              userKind: userData.role || result.data?.role
            }
          }
        } else {
          let msg = result.message || 'Registration failed'
          if (result.code === 500) {
            msg += '，请检查网络连接'
          }
          commit('SET_ERROR', msg)
          return { code: result.code || 500, success: false, message: msg }
        }
      } catch (error) {
        console.error('Registration error:', error)
        
        let msg = 'Registration failed'
        let shouldUpdateErrorMessage = true
        
        if (error.name === 'AuthenticationError') {
          console.error('🚫 注册时身份验证失败:', error.message)
          msg = '身份验证失败，请重新登录'
          commit('CLEAR_USER_INFO')
        } else if (error.name === 'AbortError') {
          msg = '请求超时，请检查网络连接'
        } else if (error.message.includes('请求被阻止：检测到无效的请求体数据')) {
          console.warn('🚫 注册请求被阻止：检测到无效的请求体数据')
          console.warn('传入的 userData:', userData)
          shouldUpdateErrorMessage = false
          msg = '数据格式错误'
        } else if (error.message.includes('请求被阻止')) {
          console.warn('🚫 注册请求被阻止:', error.message)
          shouldUpdateErrorMessage = false
          msg = '请求格式错误'
        } else {
          msg += '，请检查网络连接'
        }
        
        if (shouldUpdateErrorMessage) {
          commit('SET_ERROR', msg)
        }
        
        return { code: error.status || 500, success: false, message: msg }
      }
    },

    /**
     * loginUser
     * 用户登录方法，向后端 /login 接口发送登录请求。
     */
    async loginUser({ commit }, loginData) {
      try {
        const params = new URLSearchParams(loginData).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/login?${params}`)
        const result = await response.json()
        console.log('Login result:', result)

        if (result.code === 200) {
          // 保存用户信息和 token
          commit('SET_USER_INFO', {
            userId: result.id || result.data?.id || '',
            userName: loginData.username || result.data?.username || '',
            userKind: loginData.role || result.data?.role || '',
            userPhone: loginData.phonenumber || result.data?.phonenumber || '',
            token: result.data?.token || result.token || '' // 保存后端返回的 token
          })
          // 检查获得的token是否有效
          console.log('🚀 登录成功，保存用户信息:', 
            {
              userId: result.id || result.data?.id || '',
              userName: loginData.username || result.data?.username || '',
              userKind: loginData.role || result.data?.role || '',
              userPhone: loginData.phonenumber || result.data?.phonenumber || '',
              token: result.data?.token || result.token || ''
            }
          )
          commit('SET_ERROR', '')
          return { 
            code: result.code, 
            success: true, 
            id: result.id,
            data: {
              userKind: loginData.role || result.data?.role
            }
          }
        } else {
          let msg = result.message || 'Login failed'
          if (result.code === 500) {
            msg += '，请检查网络连接'
          }
          commit('SET_ERROR', msg)
          return { code: result.code || 500, success: false, message: msg }
        }
      } catch (error) {
        console.error('Login error:', error)
        
        let msg = 'Login failed'
        let shouldUpdateErrorMessage = true
        
        if (error.name === 'AuthenticationError') {
          console.error('🚫 登录时身份验证失败:', error.message)
          msg = '登录凭证已过期，请重新登录'
          commit('CLEAR_USER_INFO')
        } else if (error.name === 'AbortError') {
          msg = '请求超时，请检查网络连接！'
        } else if (error.message.includes('请求被阻止：检测到无效的请求体数据')) {
          console.warn('🚫 登录请求被阻止：检测到无效的请求体数据')
          console.warn('传入的 loginData:', loginData)
          shouldUpdateErrorMessage = false
          msg = '数据格式错误'
        } else if (error.message.includes('请求被阻止')) {
          console.warn('🚫 登录请求被阻止:', error.message)
          shouldUpdateErrorMessage = false
          msg = '请求格式错误'
        } else {
          msg += '，请检查网络连接!'
        }
        
        if (shouldUpdateErrorMessage) {
          commit('SET_ERROR', msg)
        }
        
        return { code: error.status || 500, success: false, message: msg }
      }
    },
    /**
     * 退出登录
     */
    logout({ commit }) {
      console.log('🚪 用户退出登录')
      commit('CLEAR_USER_INFO')
      commit('SET_ERROR', '')
    }
  }
}