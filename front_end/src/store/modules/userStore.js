import { BASE_URL, fetchWithTimeout } from '@/config.js'

export default {
  namespaced: true,
  state: () => ({
    userInfo: {
      userId: '',      // 用户ID
      userName: '',    // 用户名
      userKind: '',    // 用户类型
      userPhone: '',   // 用户手机号
      token: '',       // 用户令牌
      userImage: ''    // 用户头像
    },
    errorMessage: ''
  }),
  mutations: {
    SET_USER_INFO(state, payload) {
      // 使用 Object.assign 来确保响应式更新
      Object.assign(state.userInfo, payload)
      // ✅ 保留原有的手动持久化
      try {
        localStorage.setItem('userInfo', JSON.stringify(state.userInfo))
        localStorage.setItem('token', state.userInfo.token || '')
      } catch (e) {
        console.warn('⚠️ 手动持久化失败，将使用插件备份')
      }
    },
    SET_ERROR(state, message) {
      state.errorMessage = message
      // ✅ 保留原有的错误信息持久化
      try {
        localStorage.setItem('errorMessage', message)
      } catch (e) {
        console.warn('⚠️ 错误信息持久化失败')
      }
    },
    CLEAR_USER_INFO(state) {
      state.userInfo = {
        userId: '',
        userName: '',
        userKind: '',
        userPhone: '',
        token: '',
        userImage: ''
      }
      state.errorMessage = ''
      
      // ✅ 保留原有的清除逻辑
      try {
        localStorage.removeItem('userInfo')
        localStorage.removeItem('token')
        localStorage.removeItem('errorMessage')
      } catch (e) {
        console.warn('⚠️ 手动清除持久化数据失败')
      }
    },
    // ✅ 保留原有的恢复方法
    RESTORE_FROM_STORAGE(state) {
      try {
        const savedUserInfo = localStorage.getItem('userInfo')
        const savedErrorMessage = localStorage.getItem('errorMessage') || ''
        
        if (savedUserInfo) {
          const userInfo = JSON.parse(savedUserInfo)
          if (userInfo && userInfo.userId) {
            Object.assign(state.userInfo, userInfo)
            console.log('🔄 从手动存储恢复用户信息:', userInfo)
          }
        }
        
        state.errorMessage = savedErrorMessage
      } catch (error) {
        console.error('❌ 从手动存储恢复失败:', error)
        // 插件会自动处理备份恢复
      }
    }
  },
  actions: {
    // ✅ 保留所有原有的 actions 代码
    async registerUser({ commit }, userData) {
      try {
        const params = new URLSearchParams(userData).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/register?${params}`)
        const result = await response.json()
        if (result.code === 200) {
          const token = result.data?.token || result.token || ''
          localStorage.setItem('token', token)
          
          // 确保userId是数字
          const userId = parseInt(result.id || result.data?.id || '')
          if (isNaN(userId)) {
            throw new Error('无效的用户ID')
          }
          commit('SET_USER_INFO', {
            userId: userId,
            userName: userData.username || result.data?.username || '',
            userKind: userData.role || result.data?.role || '',
            userPhone: userData.phonenumber || result.data?.phonenumber || '',
            token: token, // ✅ 修改：使用提取的 token
            userImage: userData.userImage || ''
          })
          commit('SET_ERROR', '')
          return { 
            code: result.code, 
            success: true, 
            id: userId,
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
        let msg = 'Registration failed，请检查网络连接'
        commit('SET_ERROR', msg)
        return { code: error.status || 500, success: false, message: msg }
      }
    },

    async loginUser({ commit }, loginData) {
      try {
        const params = new URLSearchParams(loginData).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/login?${params}`)
        const result = await response.json()
        console.log('Login result:', result)

        if (result.code === 200) {
          const token = result.data?.token || result.token || ''
          
          // 保存token到localStorage
          localStorage.setItem('token', token)
          
          // 保存用户信息和 token，确保userId是数字
          const userId = parseInt(result.id || result.data?.id || '')
          if (isNaN(userId)) {
            throw new Error('无效的用户ID')
          }
          
          commit('SET_USER_INFO', {
            userId: userId,
            userName: loginData.username || result.data?.username || '',
            userKind: loginData.role || result.data?.role || '',
            userPhone: loginData.phonenumber || result.data?.phonenumber || '',
            token: token, // ✅ 修改：使用提取的 token
            userImage: loginData.userImage || '' // ✅ 新增：设置 userImage
          })
          // 检查获得的token是否有效
          console.log('🚀 登录成功，保存用户信息:', 
            {
              userId: userId,
              userName: loginData.username || result.data?.username || '',
              userKind: loginData.role || result.data?.role || '',
              userPhone: loginData.phonenumber || result.data?.phonenumber || '',
              userImage: loginData.userImage || '',
              token: token
            }
          )
          
          console.log('🚀 登录成功，保存用户信息')
          commit('SET_ERROR', '')
          return { 
            code: result.code, 
            success: true, 
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
        let msg = 'Login failed，请检查网络连接!'
        commit('SET_ERROR', msg)
        return { code: error.status || 500, success: false, message: msg }
      }
    },
    
    // ✅ 保留原有的恢复方法
    restoreFromStorage({ commit }) {
      commit('RESTORE_FROM_STORAGE')
    },
    
    // ✅ 保留原有的退出登录
    logout({ commit }) {
      console.log('🚪 用户退出登录')
      // 清除localStorage中的token
      // ✅ 新增：清除 localStorage 中的 token
      localStorage.removeItem('token')
      commit('CLEAR_USER_INFO')
    }
  }
}