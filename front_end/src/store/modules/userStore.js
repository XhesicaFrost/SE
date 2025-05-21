export default {
  state: () => ({
    userInfo: null,
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
        // 这里可以添加实际的注册API调用
        commit('SET_USER_INFO', userData)
        commit('SET_ERROR', '')
        return true
      } catch (error) {
        commit('SET_ERROR', 'Registration failed')
        return false
      }
    }
  }
}