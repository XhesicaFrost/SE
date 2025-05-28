<template>
  <div class="rider-order-detail">
    <!-- 订单详情 -->
    <div class="order-detail-section">
      <h3 class="section-title">订单详情</h3>
      <div class="detail-card">
        <div class="detail-item">
          <label>商家名称：</label>
          <span>{{ orderDetail.sellerName }}</span>
        </div>
        <div class="detail-item">
          <label>商家地址：</label>
          <span>{{ orderDetail.sellerAddress }}</span>
        </div>
        <div class="detail-item">
          <label>用户地址：</label>
          <span>{{ orderDetail.userAddress }}</span>
        </div>
        <div class="detail-item">
          <label>用户手机：</label>
          <span>{{ orderDetail.userPhone }}</span>
        </div>
        <div class="detail-item">
          <label>订单时间：</label>
          <span>{{ orderDetail.createTime }}</span>
        </div>
        <div class="detail-item">
          <label>订单状态：</label>
          <span class="status-badge" :class="getStatusClass(orderDetail.status)">
            {{ getStatusText(orderDetail.status) }}
          </span>
        </div>
      </div>
    </div>

    <!-- 导航地图 -->
    <div class="navigation-section">
      <h3 class="section-title">配送导航</h3>
      <div class="nav-controls">
        <button @click="startNavigation" class="nav-btn" :disabled="!orderDetail.id">
          {{ isNavigating ? '导航中' : '开始导航' }}
        </button>
        <button @click="refreshRoute" class="refresh-btn" :disabled="!isNavigating">
          刷新路线
        </button>
      </div>
      <div id="nav-map" class="nav-map"></div>
      <div class="route-info" v-if="routeInfo.distance">
        <div class="info-item">
          <span class="label">预计时间：</span>
          <span class="value">{{ routeInfo.time }}</span>
        </div>
        <div class="info-item">
          <span class="label">距离：</span>
          <span class="value">{{ routeInfo.distance }}</span>
        </div>
        <div class="info-item">
          <span class="label">当前目标：</span>
          <span class="value">{{ getCurrentTarget() }}</span>
        </div>
      </div>
    </div>

    <!-- 状态更新按钮 -->
    <div class="action-section">
      <button 
        class="status-update-btn"
        :class="{ 'pickup-btn': orderDetail.status === 'accepted', 'complete-btn': orderDetail.status === 'picked' }"
        @click="updateOrderStatus"
        :disabled="!orderDetail.id"
      >
        {{ orderDetail.status === 'accepted' ? '确认接餐' : '确认送达' }}
      </button>
    </div>

    <!-- 底部导航栏 -->
    <BottomNav :navItems="navItems" />
  </div>
</template>

<script>
import BottomNav from '@/components/bottomNav.vue'
import { BASE_URL, fetchWithTimeout } from '@/config.js'
import { AMAP_CONFIG } from '@/config/amap.js'
import { mapState, mapActions } from 'vuex'
import AMapLoader from '@amap/amap-jsapi-loader'

export default {
  name: 'RiderOrderDetailView',
  components: { BottomNav },
  data() {
    return {
      orderDetail: {
        id: null,
        sellerName: '',
        sellerAddress: '',
        sellerLng: null,
        sellerLat: null,
        userAddress: '',
        userLng: null,
        userLat: null,
        userPhone: '',
        createTime: '',
        status: 'accepted'
      },
      navMap: null,
      AMap: null,
      driving: null,
      geolocation: null,
      isNavigating: false,
      routeInfo: {
        time: '',
        distance: '',
        currentPosition: null
      },
      navItems: [
        { label: '订单搜索', action: () => { this.$router.push('/rider/orders') }, isActive: true },
        { label: '历史订单', action: () => { this.$router.push('/rider/history') } },
        { label: '个人中心', action: () => { /* 暂时不跳转 */ } }
      ],
      locationUpdateTimer: null,
    }
  },
  computed: {
    ...mapState('userStore', ['userId']),
    orderId() {
      return this.$route.params.id
    }
  },
  methods: {
    ...mapActions('locationStore', ['startLocationTracking', 'stopLocationTracking']),

    // 获取订单详情 - 添加测试数据
    async fetchOrderDetail() {
      try {
        const params = new URLSearchParams({ 
          riderId: this.userId, 
          orderId: this.orderId 
        }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/rider/orderdetail?${params}`)
        const result = await response.json()
        if (result.success && result.data) {
          this.orderDetail = result.data
        } else {
          // 如果获取失败，使用测试数据
          console.warn('使用测试数据进行地图功能检测')
          this.orderDetail = {
            id: this.orderId || 'test-001',
            sellerName: '清华大学食堂',
            sellerAddress: '清华大学',
            userAddress: '北京大学',
            userPhone: '138-0000-0000',
            createTime: new Date().toLocaleString(),
            status: 'accepted'
          }
        }
      } catch (e) {
        console.error('获取订单详情失败，使用测试数据', e)
        // 网络错误时也使用测试数据
        this.orderDetail = {
          id: this.orderId || 'test-001',
          sellerName: '清华大学食堂',
          sellerAddress: '清华大学',
          userAddress: '北京大学',
          userPhone: '138-0000-0000',
          createTime: new Date().toLocaleString(),
          status: 'accepted'
        }
        alert('网络错误，已加载测试数据进行地图功能检测')
      }
    },

    // 初始化高德地图
    async initAMap() {
      try {
        console.log('开始初始化高德地图...')
        
        window._AMapSecurityConfig = {
          securityJsCode: AMAP_CONFIG.securityJsCode,
        };

        this.AMap = await AMapLoader.load({
          key: AMAP_CONFIG.key,
          version: AMAP_CONFIG.version,
          plugins: [
            'AMap.Geolocation',
            'AMap.Driving',
            'AMap.Marker',
            'AMap.InfoWindow',
            'AMap.Geocoder'  // 地理编码插件
          ]
        })

        console.log('高德地图SDK加载成功')

        // 创建地图实例 - 设置北京中心点
        this.navMap = new this.AMap.Map('nav-map', {
          zoom: 11,
          center: [116.3974, 39.9093], // 北京市中心
          mapStyle: 'amap://styles/normal'
        })

        console.log('地图实例创建成功')

        // 初始化驾车路径规划
        this.driving = new this.AMap.Driving({
          map: this.navMap,
          hideMarkers: false,
          showTraffic: true,
          autoFitView: true
        })

        console.log('路径规划初始化成功')

        // 初始化定位
        this.AMap.plugin('AMap.Geolocation', () => {
          this.geolocation = new this.AMap.Geolocation({
            enableHighAccuracy: true,
            timeout: 10000, // 增加超时时间
            convert: true,
            showButton: false,
            showMarker: true,
            showCircle: true,
            panToLocation: true
          })
          console.log('定位服务初始化成功')
        })

      } catch (e) {
        console.error('高德地图加载失败', e)
        alert('地图加载失败: ' + e.message + '\n请检查网络连接和API Key配置')
      }
    },

    // 开始导航
    async startNavigation() {
      if (!this.AMap || !this.orderDetail.id) {
        console.warn('地图未初始化或订单ID为空')
        return
      }

      console.log('开始导航...')
      this.isNavigating = true
      
      try {
        // 获取当前位置
        this.geolocation.getCurrentPosition((status, result) => {
          console.log('定位结果:', status, result)
          
          if (status === 'complete') {
            this.routeInfo.currentPosition = result.position
            console.log('当前位置获取成功:', result.position)
            this.planRoute(result.position)
            
            // 修复后的实时追踪
            this.startRealTimeTracking()
          } else {
            console.error('定位失败', result)
            alert('无法获取当前位置: ' + (result.message || '请检查定位权限'))
            this.isNavigating = false
          }
        })
      } catch (e) {
        console.error('导航启动失败', e)
        alert('导航启动失败: ' + e.message)
        this.isNavigating = false
      }
    },

    // 规划路线 - 使用文字地址
    planRoute(currentPosition) {
      // 根据订单状态决定目标地点
      let targetAddress, targetName
      
      if (this.orderDetail.status === 'accepted') {
        // 去商家取餐 - 清华大学
        targetAddress = this.orderDetail.sellerAddress  // "清华大学"
        targetName = this.orderDetail.sellerName
      } else {
        // 送到用户 - 北京大学
        targetAddress = this.orderDetail.userAddress      // "北京大学"
        targetName = '用户地址'
      }

      console.log('开始路线规划:')
      console.log('当前位置:', currentPosition)
      console.log('目标地址:', targetAddress)
      console.log('目标名称:', targetName)

      // 使用高德地图地理编码将地址转换为坐标
      this.AMap.plugin('AMap.Geocoder', () => {
        const geocoder = new this.AMap.Geocoder({
          city: '北京', // 设置为北京市，提高地址解析准确度
          radius: 1000 // 搜索半径
        })
        
        geocoder.getLocation(targetAddress, (status, result) => {
          console.log('地址解析结果:', status, result)
          
          if (status === 'complete' && result.geocodes.length > 0) {
            const targetCoords = result.geocodes[0].location
            console.log('目标坐标:', targetCoords)
            
            // 清除之前的标记
            this.navMap.clearMap()
            
            // 使用转换后的坐标进行路径规划
            this.driving.search(
              [currentPosition.lng, currentPosition.lat], // 起点坐标
              [targetCoords.lng, targetCoords.lat],       // 终点坐标
              (status, result) => {
                console.log('路径规划结果:', status, result)
                
                if (status === 'complete') {
                  const route = result.routes[0]
                  this.routeInfo = {
                    time: Math.round(route.time / 60) + ' 分钟',
                    distance: (route.distance / 1000).toFixed(1) + ' 公里',
                    currentPosition: currentPosition
                  }

                  console.log('路线信息:', this.routeInfo)

                  // 添加自定义标记
                  this.addCustomMarkers(currentPosition, targetCoords.lng, targetCoords.lat, targetName)
                } else {
                  console.error('路线规划失败', result)
                  alert('路线规划失败: ' + (result.info || '未知错误'))
                }
              }
            )
          } else {
            console.error('地址解析失败', targetAddress, result)
            alert(`无法解析地址：${targetAddress}，解析结果：${result.info || '未知错误'}`)
          }
        })
      })
    },

    // 添加自定义标记
    addCustomMarkers(currentPosition, targetLng, targetLat, targetName) {
      // 当前位置标记
      new this.AMap.Marker({
        position: [currentPosition.lng, currentPosition.lat],
        title: '我的位置',
        icon: new this.AMap.Icon({
          size: new this.AMap.Size(25, 34),
          image: '//a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-default.png'
        })
      }).setMap(this.navMap)

      // 目标位置标记
      new this.AMap.Marker({
        position: [targetLng, targetLat],
        title: targetName,
        icon: new this.AMap.Icon({
          size: new this.AMap.Size(25, 34),
          image: '//a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-red.png'
        })
      }).setMap(this.navMap)
    },

    // 实时位置追踪
    startRealTimeTracking() {
      console.log('开始实时位置追踪')
      
      try {
        // 启用连续定位
        this.geolocation.watchPosition()
        
        // 使用正确的事件监听方式
        this.geolocation.on('complete', (data) => {
          console.log('位置更新:', data.position)
          this.routeInfo.currentPosition = data.position
        })
        
        this.geolocation.on('error', (error) => {
          console.error('定位错误:', error)
        })
      } catch (e) {
        console.error('实时追踪启动失败:', e)
        
        // 备选方案：定时获取位置
        this.locationUpdateTimer = setInterval(() => {
          if (this.isNavigating && this.geolocation) {
            this.geolocation.getCurrentPosition((status, result) => {
              if (status === 'complete') {
                console.log('定时位置更新:', result.position)
                this.routeInfo.currentPosition = result.position
              }
            })
          }
        }, 30000) // 每30秒更新一次
      }
    },

    // 刷新路线
    refreshRoute() {
      if (this.routeInfo.currentPosition) {
        this.planRoute(this.routeInfo.currentPosition)
      }
    },

    // 获取当前目标
    getCurrentTarget() {
      if (this.orderDetail.status === 'accepted') {
        return `前往 ${this.orderDetail.sellerName} 取餐`
      } else if (this.orderDetail.status === 'picked') {
        return '前往用户地址送餐'
      }
      return '订单已完成'
    },

    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        accepted: '已接单',
        picked: '已取餐',
        completed: '已完成'
      }
      return statusMap[status] || status
    },

    // 获取状态样式类
    getStatusClass(status) {
      return {
        'status-accepted': status === 'accepted',
        'status-picked': status === 'picked',
        'status-completed': status === 'completed'
      }
    },

    // 更新订单状态
    async updateOrderStatus() {
      if (!this.orderDetail.id) return

      const nextStatus = this.orderDetail.status === 'accepted' ? 'picked' : 'completed'
      console.log('更新订单状态:', this.orderDetail.status, '->', nextStatus)
      
      try {
        const response = await fetchWithTimeout(`${BASE_URL}/rider/updateorder`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ 
            riderId: this.userId,
            orderId: this.orderDetail.id,
            status: nextStatus
          })
        })
        const result = await response.json()
        if (result.success) {
          this.orderDetail.status = nextStatus
          console.log('状态更新成功:', nextStatus)
          
          if (nextStatus === 'completed') {
            // 订单完成，停止导航
            this.isNavigating = false
            alert('订单完成！3秒后返回主页')
            setTimeout(() => {
              this.$router.push('/rider')
            }, 3000)
          } else {
            alert('状态更新成功！现在请送餐到北京大学')
            // 重新规划路线到用户地址（北京大学）
            if (this.isNavigating && this.routeInfo.currentPosition) {
              console.log('重新规划路线到用户地址')
              this.planRoute(this.routeInfo.currentPosition)
            }
          }
        } else {
          console.error('状态更新失败:', result)
          alert('状态更新失败：' + (result.message || '未知错误'))
        }
      } catch (e) {
        console.error('网络错误:', e)
        alert('网络错误，状态更新失败')
      }
    }

    // ...其他方法保持不变
  },

  async mounted() {
    console.log('组件挂载，订单ID:', this.orderId)
    if (this.orderId) {
      await this.fetchOrderDetail()
      await this.initAMap()
    }
  },

  beforeUnmount() {
    // 清理地图资源
    if (this.navMap) {
      this.navMap.destroy()
    }
    
    // 清理定时器
    if (this.locationUpdateTimer) {
      clearInterval(this.locationUpdateTimer)
    }
  }
}
</script>

<style scoped>
.rider-order-detail {
  padding: 16px;
  background-color: #f9f9f9;
  min-height: 100vh;
}

.order-detail-section, .navigation-section, .action-section {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 12px;
}

.detail-card {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 16px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.detail-item:last-child {
  border-bottom: none;
}

.label {
  font-weight: 500;
  color: #333;
}

.value {
  color: #666;
}

.status-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
}

.status-accepted {
  background-color: #e1f5fe;
  color: #01579b;
}

.status-picked {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.status-completed {
  background-color: #f3e5f5;
  color: #6a1b9a;
}

.navigation-section {
  position: relative;
}

.nav-controls {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.nav-btn, .refresh-btn {
  flex: 1;
  padding: 10px;
  margin-right: 8px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.3s;
}

.nav-btn:disabled, .refresh-btn:disabled {
  background: #ddd;
  cursor: not-allowed;
}

.nav-btn {
  background-color: #007bff;
  color: #fff;
}

.refresh-btn {
  background-color: #28a745;
  color: #fff;
}

.nav-map {
  width: 100%;
  height: 300px;
  border-radius: 8px;
  margin-bottom: 12px;
}

.route-info {
  display: flex;
  justify-content: space-between;
  padding: 12px;
  background: #f1f1f1;
  border-radius: 8px;
}

.info-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.info-item .label {
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.info-item .value {
  color: #666;
}

.action-section {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}

.status-update-btn {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 4px;
  font-size: 18px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.3s;
}

.pickup-btn {
  background-color: #007bff;
  color: #fff;
}

.complete-btn {
  background-color: #28a745;
  color: #fff;
}
</style>