import { BASE_URL, fetchWithTimeout } from '@/config.js'



export default {
  namespaced: true,
  state: () => ({
    userInfo: {
      userName: '',
      userEmail: '',
      userKind:''
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
    async registerUser({ commit }, userData) {
      try {
        console.log("userState:registerUser", userData)
        const params = new URLSearchParams(userData).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/register?${params}`)
        const result = await response.json()
        if (result.success && result.code === 200) {
          commit('SET_USER_INFO', userData)
          commit('SET_ERROR', '')
          return { code: result.code, success: true }
        } else {
          commit('SET_ERROR', result.message || 'Registration failed')
          return { code: result.code || 500, success: false }
        }
      } catch (error) {
        if (error.name === 'AbortError') {
          commit('SET_ERROR', '请求超时')
        } else {
          commit('SET_ERROR', 'Registration failed')
        }
        return { code: 500, success: false }
      }
    },
    async loginUser({ commit }, loginData) {
      try {
        console.log("userState:loginUser", loginData)
        const params = new URLSearchParams(loginData).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/login?${params}`)
        const result = await response.json()
        if (result.success && result.code === 200) {
          const userInfo = {
            userName: loginData.userName || '',
            userEmail: loginData.userEmail || ''
          }
          commit('SET_USER_INFO', userInfo)
          commit('SET_ERROR', '')
          return { code: result.code, success: true }
        } else {
          commit('SET_ERROR', result.message || 'Login failed')
          return { code: result.code || 500, success: false }
        }
      } catch (error) {
        if (error.name === 'AbortError') {
          commit('SET_ERROR', '请求超时')
        } else {
          commit('SET_ERROR', 'Login failed')
        }
        return { code: 500, success: false }
      }
    }
  }
}