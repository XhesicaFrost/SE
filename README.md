### 环境
vue+vuex+router

### 项目结构
#### /front_end/src/assets
用于存放图片等资源
#### /front_end/src/components
用于存放各种vue组件
#### /front_end/src/stores
用于存放基于vuex的状态和数据管理
#### /front_end/src/views
用于存放各个页面
#### /front_end/src/styles
用于存放各种样式
#### /front_end/src/router
用于存放路由相关内容
#### /front_end/config.js
存放可能用到的全局变量和全局函数
目前已经有:
+ BASE_URL
+ FETCH_TIMEOUT
+ fetchWithTimeout

# 用到的api
## 用户注册接口

- **请求方式**：GET
- **请求地址**：`${BASE_URL}/register`
- **请求参数**（Query String）：

| 参数名    | 类型   | 说明         | 是否必填 |
| --------- | ------ | ------------ | -------- |
| username  | string | 用户名       | 是       |
| phone     | string | 用户手机号   | 是       |
| password  | string | 用户密码     | 是       |
| userKind  | string | 用户类型（user/rider/merchant） | 是 |

- **请求示例**：

```
GET http://localhost:3000/register?username=张三&phone=13812345678&userKind=user&password=123456
```

- **返回结果**（JSON）：

成功：
```json
{
  "code": 200,
  "success": true,
  "id": "用户ID",
  "message": "注册成功"
}
```
失败：
```json
{
  "code": 400,
  "success": false,
  "message": "手机号已被注册"
}
```

---

## 用户登录接口

- **请求方式**：GET
- **请求地址**：`${BASE_URL}/login`
- **请求参数**（Query String）：

| 参数名    | 类型   | 说明         | 是否必填 |
| --------- | ------ | ------------ | -------- |
| username  | string | 用户名       | 是       |
| phone     | string | 用户手机号   | 是       |
| password  | string | 用户密码     | 是       |
| userKind  | string | 用户类型（user/rider/merchant） | 是 |

- **请求示例**：

```
GET http://localhost:3000/login?username=张三&phone=13812345678&userKind=user&password=123456
```

- **返回结果**（JSON）：

成功：
```json
{
  "code": 200,
  "success": true,
  "id": "用户ID",
  "message": "登录成功"
}
```
失败：
```json
{
  "code": 401,
  "success": false,
  "message": "用户名或密码错误"
}
```
## 商家相关接口

### 1. `/userToMerchant`  
- **请求方式**：GET  
- **请求地址**：`${BASE_URL}/userToMerchant?userId=xxx`  
- **请求参数**：  
  | 参数名 | 类型   | 说明     | 是否必填 |
  | ------ | ------ | -------- | -------- |
  | userId | string | 用户ID   | 是       |
- **返回示例**：
如果未创建店铺，商家ID应该为-1
```json
{
  "code": 200,
  "success": true,
  "merchantId": "商家ID",
  "merchantName": "商家名称"
}
```

### 2. `/merchantHome`
- **请求方式**：GET  
- **请求地址**：`${BASE_URL}/merchant?userId=xxx`  
- **请求参数**：  
  | 参数名 | 类型   | 说明     | 是否必填 |
  | ------ | ------ | -------- | -------- |
  | userId | string | 用户ID   | 是       |
- **返回示例**：
```json
{
  "code": 200,
  "success": true,
  "data": {
    "todayRevenue": 1234.56,
    "todayOrderCount": 42,
    "latestComments": [
      { "username": "用户A", "content": "菜品很好吃！" }
      // ...共10条
    ]
  }
}
```
