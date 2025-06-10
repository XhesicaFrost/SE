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
      console.log('📝 设置用户信息到 state:', {
        ...payload,
        userImage: payload.userImage ? `[Base64 数据，长度: ${payload.userImage.length}]` : '无头像'
      })
      
      // ✅ 新增：处理 base64 头像数据
      const processedPayload = { ...payload }
      
      // 确保头像数据格式正确
      if (processedPayload.userImage && !processedPayload.userImage.startsWith('data:image/')) {
        if (processedPayload.userImage.match(/^[A-Za-z0-9+/]+=*$/)) {
          processedPayload.userImage = `data:image/jpeg;base64,${processedPayload.userImage}`
        }
      }
      
      // ✅ 同时设置 image 字段以保持兼容性
      if (processedPayload.userImage) {
        processedPayload.image = processedPayload.userImage
      }
      
      // 使用 Object.assign 来确保响应式更新
      Object.assign(state.userInfo, processedPayload)
      
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
            // ✅ 确保头像数据格式正确
            if (userInfo.userImage && !userInfo.userImage.startsWith('data:image/')) {
              if (userInfo.userImage.match(/^[A-Za-z0-9+/]+=*$/)) {
                userInfo.userImage = `data:image/jpeg;base64,${userInfo.userImage}`
              }
            }
            
            // ✅ 保持兼容性
            if (userInfo.userImage) {
              userInfo.image = userInfo.userImage
            }
            
            Object.assign(state.userInfo, userInfo)
            console.log('🔄 从手动存储恢复用户信息:', {
              ...userInfo,
              userImage: userInfo.userImage ? `[Base64 数据，长度: ${userInfo.userImage.length}]` : '无头像'
            })
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

          // ✅ 新增：处理 base64 头像
          let userImage = ''
          if (result.data?.userImage) {
            // 检查是否是 base64 编码
            if (result.data.userImage.startsWith('data:image/')) {
              // 已经是完整的 data URL，直接使用
              userImage = result.data.userImage
              console.log('✅ 注册时获取到完整的 base64 头像')
            } else {
              // 如果只是 base64 字符串，添加前缀
              userImage = `data:image/jpeg;base64,${result.data.userImage}`
              console.log('✅ 注册时获取到 base64 字符串，已添加前缀')
            }
          }

          commit('SET_USER_INFO', {
            userId: userId,
            userName: userData.username || result.data?.username || '',
            userKind: userData.role || result.data?.role || '',
            userPhone: userData.phonenumber || result.data?.phonenumber || '',
            token: token,
            userImage: userImage // ✅ 修改：使用处理后的头像
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
        console.log('🔍 登录响应:', result)

        if (result.code === 200) {
          const token = result.data?.token || result.token || ''
          
          // 保存token到localStorage
          localStorage.setItem('token', token)
          
          // 保存用户信息和 token，确保userId是数字
          const userId = parseInt(result.id || result.data?.id || '')
          if (isNaN(userId)) {
            throw new Error('无效的用户ID')
          }

          // ✅ 新增：处理 base64 头像
          let userImage = ''
          if (result.data?.userImage) {
            // 检查是否是 base64 编码
            if (result.data.userImage.startsWith('data:image/')) {
              // 已经是完整的 data URL，直接使用
              userImage = result.data.userImage
              console.log('✅ 登录时获取到完整的 base64 头像')
            } else {
              // 如果只是 base64 字符串，添加前缀
              userImage = `data:image/jpeg;base64,${result.data.userImage}`
              console.log('✅ 登录时获取到 base64 字符串，已添加前缀')
            }
            console.log('🖼️ 头像数据长度:', userImage.length)
          } else {
            console.log('⚠️ 服务器未返回头像数据')
          }
          
          const userInfo = {
            userId: userId,
            userName: loginData.username || result.data?.username || '',
            userKind: loginData.role || result.data?.role || '',
            userPhone: loginData.phonenumber || result.data?.phonenumber || '',
            token: token,
            userImage: userImage // ✅ 修改：使用处理后的头像
          }

          commit('SET_USER_INFO', userInfo)
          
          console.log('🚀 登录成功，保存用户信息:', {
            ...userInfo,
            userImage: userImage ? `[Base64 数据，长度: ${userImage.length}]` : '无头像'
          })
          
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
      localStorage.removeItem('token')
      commit('CLEAR_USER_INFO')
    },
    
    // ✅ 修改：更新用户信息 action，支持 base64 头像处理
    updateUserInfo({ commit, state }, updatedInfo) {
      console.log('🔄 更新用户信息:', updatedInfo)
      console.log('🔍 当前用户信息:', state.userInfo)
      
      // ✅ 新增：处理头像数据
      const processedInfo = { ...updatedInfo }
      
      // 如果有头像数据，确保格式正确
      if (processedInfo.userImage && !processedInfo.userImage.startsWith('data:image/')) {
        // 如果是纯 base64 字符串，添加前缀
        if (processedInfo.userImage.match(/^[A-Za-z0-9+/]+=*$/)) {
          processedInfo.userImage = `data:image/jpeg;base64,${processedInfo.userImage}`
        }
      }
      
      // 同时设置 image 字段以保持兼容性
      if (processedInfo.userImage) {
        processedInfo.image = processedInfo.userImage
      }
      
      commit('SET_USER_INFO', processedInfo)
      console.log('✅ 用户信息更新成功')
    }
  }
}