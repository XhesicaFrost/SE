### 环境
vue+vuex(就是那个Store,处理全局变量比javaScript原生舒服。)+router

### 增加新页面可能要做的
在/src/router/indej.js 进行注册
在/src/views/* 写页面
在/src/components 写一些可以复用的组件。
在/config 写一些特殊的全局变量 (amap.js是我的高德地图api) 或者/config.js 写一些配置
在/store/index.js注新的全局变量，然后在/store/modules写具体的  *Store.js

### 项目结构
#### /front_end/src/assets
用于存放图片等资源
现在有一张奶龙
#### /front_end/src/components
用于存放各种vue组件
现在有一个底部导航栏bottomNav和一个登录注册表单AuthForm
#### /front_end/src/stores
用于存放基于vuex的状态和数据管理（更好用的全局变量）
locationStore存放的是当前的地理位置。
userStore存放的是用户信息，包括userId,userName,userKind,userPhone
riderStore存放的是骑手的地理位置。
sellerStore存放的是当前的商家信息，包括sellerId（因为创建账户！=创建店铺）,sellerName,sellerStatus(可能出现审批中，被封禁，正常)
#### /front_end/src/views
用于存放各个页面
注册和登录单独放置，剩下四个文件夹分别存放对应的页面。
#### /front_end/src/styles
用于存放各种样式
#### /front_end/src/router
用于存放路由相关内容
```javaScript
 path: '/seller/shop',//网页路径，例如localhost/seller/shop就会跳转到这个
    name: 'sellerShop',//路由的名字
    component: () => import('@/views/SellerViews/SellerShop.vue'),//跳转之后载入哪个页面
    meta: { allow: ['seller'] }//内部数据，用于和下面的函数接合阻止随意跳转。
```
本函数用于鉴权，如果用户类型不正确，不能跳转。例如，seller不能打开/rider
```javaScript
router.beforeEach((to, from, next) => {
    if(debug_AuthCheck==false){//不会进行权限检查。
        next()
        return
    }
  const allow = to.meta.allow
  const userKind = store.state.userStore.userInfo.userKind
  if (allow && !allow.includes(userKind)) {
    // 没有权限，跳转到登录或其他页面
    next('/login')
  } else {
    next()
  }
})
```

#### /front_end/config.js
存放可能用到的全局变量和全局函数
目前已经有:
+ BASE_URL （总不能一个个复制网址）
+ FETCH_TIMEOUT （超时的阈值）
+ fetchWithTimeout （带有超时停止的发送请求）
+ export const debug_seller = false; // 是否启用商家调试模式
+ export const debug_rider = false; // 是否启用骑手调试模式
+ export const debug_user = false; // 是否启用用户调试模式
+ export const debug_admin = false; // 是否启用管理员调试模式
+ export const debug_AuthCheck = false; // 是否启用权限检查
+ export const debug_seller_created = true;//是否默认商家已经创立

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
| userKind  | string | 用户类型（user/rider/seller） | 是 |

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
| userKind  | string | 用户类型（user/rider/seller） | 是 |

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
### 获取用户类型接口

#### 接口信息
- **请求方式**：GET
- **请求地址**：`${BASE_URL}/user/kind`
- **发送数据格式**：Query String

#### 请求参数
| 参数名 | 类型   | 说明   | 是否必填 |
| ------ | ------ | ------ | -------- |
| userId | string | 用户ID | 是       |

#### 请求示例
GET /user/kind?userId=rider123
#### 返回数据格式
##### 成功响应
```json
{
  "success": true,
  "data": {
    "userKind": "rider"
  }
}

##### 失败响应
{
  "success": false,
  "message": "用户不存在"
}

## 商家相关接口

### 1. `/userToseller`  
- **请求方式**：GET  
- **请求地址**：`${BASE_URL}/userToseller?userId=xxx`  
- **请求参数**：  
  | 参数名 | 类型   | 说明     | 是否必填 |
  | ------ | ------ | -------- | -------- |
  | userId | string | 用户ID   | 是       |
- **返回示例**：
  | 参数名 | 类型   | 说明     | 是否必填 |
  | ------ | ------ | -------- | -------- |
  | sellerStatus | string | 可以为“未注册/正常/审批中/封禁中”   | 是       |

```json
{
  "code": 200,
  "success": true,
  "sellerId": "商家ID",
  "sellerName": "商家名称",
  "sellerStatus":"正常"
}
```

### 2. `/sellerHome`
- **请求方式**：GET  
- **请求地址**：`${BASE_URL}/seller?userId=xxx`  
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
### 商家注册接口

- **请求方式**：POST  
- **请求地址**：`/seller/register`  
- **请求参数**（FormData 格式）：

| 参数名      | 类型    | 说明         | 是否必填 |
| ----------- | ------- | ------------ | -------- |
| shopName    | string  | 店铺名称     | 是       |
| shopAddress | string  | 店铺地址     | 是       |
| userId | string | 用户id | 是 |
| shopTags | json string | 店铺tags | 是 |
| shopImage   | file    | 店铺图片     | 是       |

- **请求示例**：

以 FormData 方式提交：

```
POST /seller/register
Content-Type: multipart/form-data

shopName=xxx
shopAddress=xxx
shopImage=文件
```

- **返回结果**（JSON）：

成功：
```json
{
  "status": "success"
}
```

失败：
```json
{
  "status": "fail"
}
```

### 获取正常商品列表接口
（指的是通过审批的商品）
- **请求方式**：GET  
- **请求地址**：`${BASE_URL}/item`  
- **请求参数**（Query String）：

| 参数名     | 类型   | 说明       | 是否必填 |
| ---------- | ------ | ---------- | -------- |
| sellerId | string | 商家ID     | 是       |

- **返回示例**：

```json
{
  "code": 200,
  "success": true,
  "data": [
    { "id": 1, "image": "https://via.placeholder.com/60", "name": "商品A", "price": 10, "sales": 100 },
    { "id": 2, "image": "https://via.placeholder.com/60", "name": "商品B", "price": 20, "sales": 80 },
    { "id": 3, "image": "https://via.placeholder.com/60", "name": "商品C", "price": 15, "sales": 120 }
  ]
}
```

### 获取店铺信息接口

- **请求方式**：GET  
- **请求地址**：`${BASE_URL}/shop`  
- **请求参数**（Query String）：

| 参数名     | 类型   | 说明       | 是否必填 |
| ---------- | ------ | ---------- | -------- |
| sellerId | string | 商家ID     | 是       |

- **返回示例**：

```json
{
  "code": 200,
  "success": true,
  "data": {
    "shopName": "示例店铺",
    "shopImg": "https://xxx.com/shop.jpg",
    "shopAddress": "示例地址",
    "shopTags":"['1','2']"
  }
}
```

### 本文件用到的网络接口格式说明

#### 1. 获取商品信息接口

- **请求方式**：GET  
- **请求地址**：`${BASE_URL}/seller/item`  
- **请求参数**（Query String）：

| 参数名 | 类型   | 说明     | 是否必填 |
| ------ | ------ | -------- | -------- |
| id     | string | 商品ID   | 是       |

- **请求示例**：
```
GET /seller/item?id=123
```

- **返回数据格式**（JSON，图片为 base64 字符串）：
```json
{
  "success": true,
  "code": 200,
  "data": {
    "itemName": "商品A",
    "itemPrice": 10.5,
    "itemImage": "iVBORw0KGgoAAAANSUhEUgAA..."  // base64字符串
  }
}
```

---

#### 2. 编辑商品信息接口

- **请求方式**：POST  
- **请求地址**：`${BASE_URL}/seller/item/register`  
- **发送数据格式**：FormData（multipart/form-data）

| 参数名    | 类型   | 说明         | 是否必填 |
| --------- | ------ | ------------ | -------- |
| itemId    | string | 商品ID       | 是       |
| itemName  | string | 商品名称     | 是       |
| itemPrice | number | 商品单价     | 是       |
| itemImage | file   | 商品图片     | 否（如未更改可不传） |
| itemDescription | string | 商品描述 | 是（不填为空） |

- **请求示例**：
```
POST /seller/item/register
Content-Type: multipart/form-data

itemId=123
itemName=商品A
itemPrice=10.5
itemImage=文件（可选）
itemDescription=奶龙喜欢吃
```

- **返回数据格式**（JSON）：
```json
{
  "status": "success"
}
```
或
```json
{
  "status": "fail"
}
```

### 新增商品接口说明

#### 1. 新增商品接口

- **请求方式**：POST  
- **请求地址**：`${BASE_URL}/seller/item/register`  
- **发送数据格式**：FormData（`multipart/form-data`）

| 参数名      | 类型   | 说明         | 是否必填 |
| ----------- | ------ | ------------ | -------- |
| itemName    | string | 商品名称     | 是       |
| itemImage   | file   | 商品图片     | 是       |
| itemPrice   | number | 商品单价     | 是       |
| sellerId  | string | 商家ID       | 是       |
| itemDescription | string | 商品描述 | 是（不填为空） |
- **请求示例**：
```
POST /seller/item/register
Content-Type: multipart/form-data

itemName=商品A
itemImage=文件
itemPrice=10.5
sellerId=xxx
itemDescription=奶龙喜欢吃
```

- **返回数据格式**（JSON）：

成功：
```json
{
  "status": "success"
}
```

失败：
```json
{
  "status": "fail"
}
```
### 商品上下架相关接口说明

#### 1. 下架商品

- **请求方式**：POST  
- **请求地址**：`${BASE_URL}/seller/item/offline`  
- **发送数据格式**：JSON

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| itemId   | string | 商品ID   | 是       |
| sellerId | string | 商家ID   | 是       |

- **请求示例**：

`{ "itemId": "123", "sellerId": "seller456" }`

- **返回数据格式**（JSON）：

成功：
```json
{
  "code": 200,
  "success": true,
  "message": "下架成功"
}
```
失败:
```
{
  "code": 400,
  "success": false,
  "message": "下架失败"
}
```
### 2.上架商品

- 请求方式：POST

- 请求地址：${BASE_URL}/seller/item/online

- 发送数据格式：JSON

| 参数名	 | 类型	| 说明	| 是否必填 |
| ----      | --- | ------ | ----|
| itemId	| string |	商品ID	| 是 |
| sellerId  |	string |	商家ID |	是 |

- **请求示例**：
```json
POST /item/online
Content-Type: application/json

{
  "itemId": "123",
  "sellerId": "seller456"
}
```
- **返回数据格式**

成功
```json
{
  "code": 200,
  "success": true,
  "message": "上架成功"
}
```
失败
```json
{
  "code": 400,
  "success": false,
  "message": "上架失败"
}
```

### 本页面用到的网络接口格式说明

#### 1. 获取店铺信息接口

- **请求方式**：GET  
- **请求地址**：`${BASE_URL}/shop`  
- **请求参数**（Query String）：

| 参数名     | 类型   | 说明     | 是否必填 |
| ---------- | ------ | -------- | -------- |
| sellerId | string | 商家ID   | 是       |

- **请求示例**：
```
GET /shop?sellerId=xxx
```

- **返回数据格式**（JSON，图片为 base64 字符串）：
```json
{
  "success": true,
  "code": 200,
  "data": {
    "shopName": "示例店铺",
    "shopAddress": "示例地址",
    "shopImg": "iVBORw0KGgoAAAANSUhEUgAA..."  // base64字符串
  }
}
```

---

#### 2. 编辑店铺信息接口

- **请求方式**：POST  
- **请求地址**：`${BASE_URL}/seller/edit`  
- **发送数据格式**：FormData（`multipart/form-data`）

| 参数名      | 类型   | 说明         | 是否必填 |
| ----------- | ------ | ------------ | -------- |
| shopName    | string | 店铺名称     | 是       |
| shopAddress | string | 店铺地址     | 是       |
| shopImage   | file   | 店铺图片     | 否（如未更改可不传） |
| shopTags | json string | 店铺tags | 是 |
| sellerId  | string | 商家ID       | 是       |

- **请求示例**：
```
POST /seller/edit
Content-Type: multipart/form-data

shopName=示例店铺
shopAddress=示例地址
shopImage=文件（可选）
sellerId=xxx
shopTags=['1','2']
```

- **返回数据格式**（JSON）：

成功：
```json
{
  "status": "success"
}
```

失败：
```json
{
  "status": "fail"
}
```

### 新增商品接口说明

#### 1. 新增商品接口

- **请求方式**：POST  
- **请求地址**：`${BASE_URL}/seller/item/register`  
- **发送数据格式**：FormData（`multipart/form-data`）

| 参数名      | 类型    | 说明         | 是否必填 |
| ----------- | ------- | ------------ | -------- |
| itemName    | string  | 商品名称     | 是       |
| itemImage   | file    | 商品图片     | 是       |
| itemPrice   | number  | 商品单价     | 是       |
| sellerId  | string  | 商家ID       | 是       |

- **请求示例**：
```
POST /seller/item/register
Content-Type: multipart/form-data

itemName=商品A
itemImage=文件
itemPrice=10.5
sellerId=xxx
```

- **返回数据格式**（JSON）：

成功：
```json
{
  "status": "success"
}
```

失败：
```json
{
  "status": "fail"
}
```

### 删除商品接口说明

#### 1. 删除商品

- **请求方式**：DELETE  
- **请求地址**：`${BASE_URL}/seller/item`  
- **发送数据格式**：JSON

| 参数名 | 类型   | 说明     | 是否必填 |
| ------ | ------ | -------- | -------- |
| id     | string | 商品ID   | 是       |

- **请求示例**：
```
DELETE /seller/item
Content-Type: application/json

{
  "id": "123"
}
```

- **返回数据格式**（JSON）：

成功：
```json
{
  "success": true
}
```

失败：
```json
{
  "success": false,
  "message": "删除失败"
}
```

### 审批相关接口说明

#### 1. 获取店铺信息修改审批列表

- **请求方式**：GET  
- **请求地址**：`${BASE_URL}/approval/shop`  
- **请求参数**（Query String）：

| 参数名     | 类型   | 说明     | 是否必填 |
| ---------- | ------ | -------- | -------- |
| sellerId | string | 商家ID   | 是       |

- **请求示例**：
```
GET /approval/shop?sellerId=xxx
```

- **返回数据格式**（JSON，图片为 base64 字符串或 url）：
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "image": "iVBORw0KGgoAAAANSUhEUgAA...",  // base64字符串或图片url
      "name": "店铺A",
      "address": "地址A",
      "status": "审批中" // 或 "否决"、"通过"
    }
    // ...更多审批项
  ]
}
```

---

#### 2. 获取商品信息修改审批列表

- **请求方式**：GET  
- **请求地址**：`${BASE_URL}/approval/item`  
- **请求参数**（Query String）：

| 参数名     | 类型   | 说明     | 是否必填 |
| ---------- | ------ | -------- | -------- |
| sellerId | string | 商家ID   | 是       |

- **请求示例**：
```
GET /approval/item?sellerId=xxx
```

- **返回数据格式**（JSON，图片为 base64 字符串或 url）：
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "image": "iVBORw0KGgoAAAANSUhEUgAA...",  // base64字符串或图片url
      "name": "商品A",
      "price": 10,
      "status": "通过" // 或 "审批中"、"否决"
    }
    // ...更多审批项
  ]
}
```

### 促销活动相关接口说明

#### 1. 获取促销活动列表

- **请求方式**：GET  
- **请求地址**：`${BASE_URL}/seller/promotion`  
- **请求参数**（Query String）：

| 参数名     | 类型   | 说明     | 是否必填 |
| ---------- | ------ | -------- | -------- |
| sellerId | string | 商家ID   | 是       |

- **请求示例**：
```
GET /seller/promotion?sellerId=xxx
```

- **返回数据格式**（JSON）：
```json
{
  "success": true,
  "data": [
    {
      "promotionId": 1,
      "promotionName": "满100减20",
      "full": 100,
      "minus": 20,
      "startTime": "2025-05-01 00:00:00",
      "endTime": "2025-05-31 23:59:59"
    }
    // ...更多促销活动
  ]
}
```

---

#### 2. 删除促销活动

- **请求方式**：DELETE  
- **请求地址**：`${BASE_URL}/seller/promotion`  
- **发送数据格式**：JSON

| 参数名       | 类型   | 说明         | 是否必填 |
| ------------ | ------ | ------------ | -------- |
| promotionId  | string | 促销活动ID   | 是       |

- **请求示例**：
```
DELETE /seller/promotion
Content-Type: application/json

{
  "promotionId": "1"
}
```

- **返回数据格式**（JSON）：

成功：
```json
{
  "success": true
}
```

失败：
```json
{
  "success": false,
  "message": "删除失败"
}
```

#### 1. 获取促销活动详情

- **请求方式**：GET  
- **请求地址**：`${BASE_URL}/seller/promotion/detail`  
- **请求参数**（Query String）：

| 参数名       | 类型   | 说明         | 是否必填 |
| ------------ | ------ | ------------ | -------- |
| promotionId  | string | 促销活动ID   | 是       |

- **请求示例**：
```
GET /seller/promotion/detail?promotionId=xxx
```

- **返回数据格式**（JSON）：
```json
{
  "success": true,
  "data": {
    "promotionName": "满100减20",
    "full": 100,
    "minus": 20,
    "startTime": "2025-05-01T00:00:00",
    "endTime": "2025-05-31T23:59:59"
  }
}
```

---

#### 2. 编辑促销活动

- **请求方式**：POST  
- **请求地址**：`${BASE_URL}/seller/promotion/edit`  
- **发送数据格式**：FormData（`multipart/form-data`）

| 参数名        | 类型    | 说明         | 是否必填 |
| ------------- | ------- | ------------ | -------- |
| promotionId   | string  | 促销活动ID   | 是       |
| promotionName | string  | 活动名称     | 是       |
| full          | number  | 满减门槛     | 是       |
| minus         | number  | 减免金额     | 是       |
| startTime     | string  | 开始时间     | 是       |
| endTime       | string  | 结束时间     | 是       |
| sellerId    | string  | 商家ID       | 是       |

- **请求示例**：
```
POST /seller/promotion/edit
Content-Type: multipart/form-data

promotionId=xxx
promotionName=满100减20
full=100
minus=20
startTime=2025-05-01T00:00:00
endTime=2025-05-31T23:59:59
sellerId=yyy
```

- **返回数据格式**（JSON）：

成功：
```json
{
  "status": "success"
}
```

失败：
```json
{
  "status": "fail"
}
```

#### 1. 新建促销活动

- **请求方式**：POST  
- **请求地址**：`${BASE_URL}/seller/promotion/register`  
- **发送数据格式**：FormData（`multipart/form-data`）

| 参数名        | 类型    | 说明         | 是否必填 |
| ------------- | ------- | ------------ | -------- |
| promotionName | string  | 活动名称     | 是       |
| full          | number  | 满减门槛     | 是       |
| minus         | number  | 减免金额     | 是       |
| startTime     | string  | 开始时间     | 是       |
| endTime       | string  | 结束时间     | 是       |
| sellerId    | string  | 商家ID       | 是       |

- **请求示例**：
```
POST /seller/promotion/register
Content-Type: multipart/form-data

promotionName=满100减20
full=100
minus=20
startTime=2025-05-01T00:00:00
endTime=2025-05-31T23:59:59
sellerId=xxx
```

- **返回数据格式**（JSON）：

成功：
```json
{
  "status": "success"
}
```

失败：
```json
{
  "status": "fail"
}
```

### 商家订单页面相关接口说明

#### 1. 获取订单列表

- **请求方式**：GET  
- **请求地址**：`${BASE_URL}/seller/order`  
- **请求参数**（Query String）：

| 参数名     | 类型   | 说明     | 是否必填 |
| ---------- | ------ | -------- | -------- |
| sellerId | string | 商家ID   | 是       |

- **请求示例**：
```
GET /seller/order?sellerId=xxx
```

- **返回数据格式**（JSON）：
```json
{
  "success": true,
  "data": [
    {
      "id": 1005,
      "totalPrice": 58.5,
      "status": COMPLETED,
      "items": [
        { "name": "汉堡", "count": 2, "price": 15 },
        { "name": "薯条", "count": 1, "price": 8.5 },
        { "name": "可乐", "count": 1, "price": 20 }
      ]
    }
    // ...更多订单
  ]
}
```

---

#### 2. 设置订单为已出餐

- **请求方式**：POST  
- **请求地址**：`${BASE_URL}/seller/order/serve`  
- **发送数据格式**：JSON

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| orderId  | string | 订单ID   | 是       |

- **请求示例**：
```
POST /seller/order/serve
Content-Type: application/json

{
  "orderId": "1005"
}
```

- **返回数据格式**（JSON）：

成功：
```json
{
  "success": true
}
```

失败：
```json
{
  "success": false,
  "message": "操作失败"
}
```

### 商家数据统计页面相关接口说明

#### 1. 获取销售额与订单数数据

- **请求方式**：GET  
- **请求地址**：`${BASE_URL}/seller/data/sales`  
- **请求参数**（Query String）：

| 参数名     | 类型   | 说明         | 是否必填 |
| ---------- | ------ | ------------ | -------- |
| sellerId | string | 商家ID       | 是       |
| startDate  | string | 开始日期（yyyy-mm-dd） | 是 |
| endDate    | string | 结束日期（yyyy-mm-dd） | 是 |

- **请求示例**：
```
GET /seller/data/sales?sellerId=xxx&startDate=2025-05-01&endDate=2025-05-07
```

- **返回数据格式**（JSON）：
```json
{
  "success": true,
  "data": {
    "totalSales": 12345,
    "totalOrders": 234,
    "labels": ["2025-05-01", "2025-05-02", "2025-05-03", "2025-05-04", "2025-05-05", "2025-05-06", "2025-05-07"],
    "series": [
      { "sales": 2000, "orders": 30 },
      { "sales": 1800, "orders": 28 },
      { "sales": 2500, "orders": 40 },
      { "sales": 1600, "orders": 25 },
      { "sales": 2100, "orders": 35 },
      { "sales": 1700, "orders": 30 },
      { "sales": 1645, "orders": 46 }
    ]
  }
}
```

---

#### 2. 获取用户评价数据

- **请求方式**：GET  
- **请求地址**：`${BASE_URL}/seller/data/comment`  
- **请求参数**（Query String）：

| 参数名     | 类型   | 说明         | 是否必填 |
| ---------- | ------ | ------------ | -------- |
| sellerId | string | 商家ID       | 是       |
| startDate  | string | 开始日期（yyyy-mm-dd） | 是 |
| endDate    | string | 结束日期（yyyy-mm-dd） | 是 |

- **请求示例**：
```
GET /seller/data/comment?sellerId=xxx&startDate=2025-05-01&endDate=2025-05-07
```

- **返回数据格式**（JSON）：
```json
{
  "success": true,
  "data": {
    "totalGood": 180,
    "totalBad": 12,
    "labels": ["2025-05-01", "2025-05-02", "2025-05-03", "2025-05-04", "2025-05-05", "2025-05-06", "2025-05-07"],
    "series": [
      { "good": 30, "bad": 2 },
      { "good": 28, "bad": 1 },
      { "good": 35, "bad": 3 },
      { "good": 25, "bad": 2 },
      { "good": 32, "bad": 1 },
      { "good": 15, "bad": 2 },
      { "good": 15, "bad": 1 }
    ]
  }
}
```

---

#### 3. 下载详细数据

- **请求方式**：GET  
- **请求地址**：`${BASE_URL}/seller/data/download`  
- **请求参数**（Query String）：

| 参数名     | 类型   | 说明         | 是否必填 |
| ---------- | ------ | ------------ | -------- |
| sellerId | string | 商家ID       | 是       |
| startDate  | string | 开始日期（yyyy-mm-dd） | 是 |
| endDate    | string | 结束日期（yyyy-mm-dd） | 是 |

- **请求示例**：
```
GET /seller/data/download?sellerId=xxx&startDate=2025-05-01&endDate=2025-05-07
```

- **返回内容**：文件下载（如 Excel、CSV 等格式的详细数据）

### 骑手主页相关接口说明

#### 1. 获取已接订单

- **请求方式**：GET  
- **请求地址**：`${BASE_URL}/rider/acceptedorders`  
- **请求参数**（Query String）：

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| riderId  | string | 骑手ID   | 是       |

- **请求示例**：
```
GET /rider/acceptedorders?riderId=xxx
```

- **返回数据格式**（JSON）：
```json
{
  "success": true,
  "data": [
    {
      "id": 1001,
      "sellerName": "麦当劳",
      "sellerAddress": "美食街18号",
      "userAddress": "学生公寓3号楼201",
      "createTime": "2025-05-23 12:30",
      "status": "accepted"  // accepted: 已接单, picked: 已接餐
    }
    // ...更多订单
  ]
}
```

---

#### 2. 获取推荐订单

- **请求方式**：GET  
- **请求地址**：`${BASE_URL}/rider/recommendedorders`  
- **请求参数**（Query String）：

| 参数名    | 类型   | 说明       | 是否必填 |
| --------- | ------ | ---------- | -------- |
| riderId   | string | 骑手ID     | 是       |
| latitude  | number | 纬度       | 是       |
| longitude | number | 经度       | 是       |

- **请求示例**：
```
GET /rider/recommendedorders?riderId=xxx&latitude=39.9042&longitude=116.4074
```

- **返回数据格式**（JSON）：
```json
{
  "success": true,
  "data": [
    {
      "id": 2001,
      "sellerName": "北门餐厅",
      "sellerAddress": "北门商业街5号",
      "userAddress": "图书馆二楼",
      "createTime": "2025-05-23 14:00"
    }
    // ...更多订单（最多10条）
  ]
}
```

---

#### 3. 抢单

- **请求方式**：POST  
- **请求地址**：`${BASE_URL}/rider/chooseorder`  
- **发送数据格式**：JSON

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| riderId  | string | 骑手ID   | 是       |
| orderId  | string | 订单ID   | 是       |

- **请求示例**：
```
POST /rider/chooseorder
Content-Type: application/json

{
  "riderId": "xxx",
  "orderId": "2001"
}
```

- **返回数据格式**（JSON）：

成功：
```json
{
  "success": true
}
```

失败：
```json
{
  "success": false,
  "message": "该订单可能已被其他骑手接取"
}
```

---

#### 4. 更新订单状态

- **请求方式**：POST  
- **请求地址**：`${BASE_URL}/rider/updateorder`  
- **发送数据格式**：JSON

| 参数名   | 类型   | 说明                           | 是否必填 |
| -------- | ------ | ------------------------------ | -------- |
| riderId  | string | 骑手ID                         | 是       |
| orderId  | string | 订单ID                         | 是       |
| status   | string | 新状态（picked/completed）     | 是       |

- **请求示例**：
```
POST /rider/updateorder
Content-Type: application/json

{
  "riderId": "xxx",
  "orderId": "1001",
  "status": "picked"
}
```

- **返回数据格式**（JSON）：

成功：
```json
{
  "success": true
}
```

失败：
```json
{
  "success": false,
  "message": "状态更新失败"
}
```

---

### 骑手订单搜索页面相关接口说明

#### 1. 获取可抢订单（带筛选）

- **请求方式**：GET  
- **请求地址**：`${BASE_URL}/rider/orderfiltered`  
- **请求参数**（Query String）：

| 参数名       | 类型   | 说明       | 是否必填 |
| ------------ | ------ | ---------- | -------- |
| userId       | string | 用户ID     | 是       |
| sellerName | string | 商家名称   | 否       |
| userAddress  | string | 用户地址   | 否       |

- **请求示例**：
```
GET /rider/orderfiltered?userId=xxx&sellerName=麦当劳&userAddress=学生公寓
```

- **返回数据格式**（JSON）：
```json
{
  "success": true,
  "data": [
    {
      "id": 3001,
      "sellerName": "麦当劳",
      "sellerAddress": "美食街18号",
      "userAddress": "学生公寓3号楼201",
      "createTime": "2025-05-23 14:00"
    },
    {
      "id": 3002,
      "sellerName": "肯德基",
      "sellerAddress": "中心广场2楼",
      "userAddress": "教学楼A座办公室",
      "createTime": "2025-05-23 14:30"
    }
    // ...更多订单
  ]
}
```

---

#### 2. 抢单

- **请求方式**：POST  
- **请求地址**：`${BASE_URL}/rider/chooseorder`  
- **发送数据格式**：JSON

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| riderId  | string | 骑手ID   | 是       |
| orderId  | string | 订单ID   | 是       |

- **请求示例**：
```
POST /rider/chooseorder
Content-Type: application/json

{
  "riderId": "xxx",
  "orderId": "3001"
}
```

- **返回数据格式**（JSON）：

成功：
```json
{
  "success": true
}
```

失败：
```json
{
  "success": false,
  "message": "该订单可能已被其他骑手接取"
}
```

## 更新骑手位置

### 接口信息
- **请求方式**：POST  
- **请求地址**：`${BASE_URL}/rider/updateLocation`  
- **发送数据格式**：JSON

### 请求参数

| 参数名    | 类型   | 说明       | 是否必填 | 备注                    |
| --------- | ------ | ---------- | -------- | ----------------------- |
| userId    | string | 用户ID     | 是       | 来自 userStore.userId   |
| latitude  | number | 纬度       | 是       | WGS84坐标系，范围：-90~90  |
| longitude | number | 经度       | 是       | WGS84坐标系，范围：-180~180 |

### 请求示例

```javascript
POST /rider/updateLocation
Content-Type: application/json

{
  "userId": "rider123",
  "latitude": 39.9042,
  "longitude": 116.4074
}

## 1. 获取订单详情

### 接口信息
- **请求方式**：GET
- **请求地址**：`${BASE_URL}/rider/orderdetail`
- **发送数据格式**：Query String

### 请求参数

| 参数名   | 类型   | 说明     | 是否必填 | 备注                    |
| -------- | ------ | -------- | -------- | ----------------------- |
| riderId  | string | 骑手ID   | 是       | 来自 userStore.userId   |
| orderId  | string | 订单ID   | 是       | 来自路由参数 $route.params.id |

### 请求示例

```javascript
GET /rider/orderdetail?riderId=rider123&orderId=order456
```

### 成功响应

```javascript
{
  "success": true,
  "data": {
    "id": "order456",
    "sellerName": "麦当劳（中关村店）",
    "sellerAddress": "北京市海淀区中关村大街1号",
    "sellerLng": 116.3088,
    "sellerLat": 39.9828,
    "userAddress": "清华大学紫荆公寓1号楼",
    "userLng": 116.3267,
    "userLat": 40.0031,
    "userPhone": "138-0000-0000",
    "createTime": "2025-05-24 14:30:00",
    "status": "accepted"
  }
}
```

### 失败响应

```javascript
{
  "success": false,
  "message": "订单不存在或无权限访问"
}
```

### 返回字段说明

| 字段名         | 类型   | 说明                              | 是否必填 | 备注                    |
| -------------- | ------ | --------------------------------- | -------- | ----------------------- |
| id             | string | 订单ID                            | 是       | 唯一标识符              |
| sellerName   | string | 商家名称                          | 是       | 用于显示和标记          |
| sellerAddress| string | 商家地址                          | 是       | 用于地理编码解析        |
| sellerLng    | number | 商家经度坐标                      | 否       | 优先使用，提高精度      |
| sellerLat    | number | 商家纬度坐标                      | 否       | 优先使用，提高精度      |
| userAddress    | string | 用户地址                          | 是       | 用于地理编码解析        |
| userLng        | number | 用户经度坐标                      | 否       | 优先使用，提高精度      |
| userLat        | number | 用户纬度坐标                      | 否       | 优先使用，提高精度      |
| userPhone      | string | 用户手机号                        | 是       | 联系用户使用            |
| createTime     | string | 订单创建时间                      | 是       | 格式：YYYY-MM-DD HH:mm:ss |
| status         | string | 订单状态                          | 是       | accepted/picked/completed |

## 获取历史订单列表

### 接口信息
- **请求方式**：GET
- **请求地址**：`${BASE_URL}/rider/history`
- **发送数据格式**：Query String

### 请求参数

| 参数名   | 类型   | 说明           | 是否必填 | 备注                    |
| -------- | ------ | -------------- | -------- | ----------------------- |
| userId   | string | 用户ID         | 是       | 来自 userStore.userId   |
| page     | number | 页码           | 否       | 默认为1，从1开始计数    |
| pageSize | number | 每页条数       | 否       | 默认为10                |

### 请求示例

```javascript
GET /rider/history?userId=rider123&page=1&pageSize=10
### 返回数据格式
#### 成功相应
{
  "success": true,
  "data": {
    "orders": [
      {
        "id": "order456",
        "sellerName": "麦当劳（中关村店）",
        "sellerAddress": "北京市海淀区中关村大街1号",
        "userAddress": "清华大学紫荆公寓1号楼",
        "userPhone": "138-0000-0000",
        "createTime": "2025-05-24 14:30:00",
        "completeTime": "2025-05-24 15:45:00"
      },
      {
        "id": "order789",
        "sellerName": "肯德基（五道口店）",
        "sellerAddress": "北京市海淀区五道口购物中心",
        "userAddress": "北京大学燕园",
        "userPhone": "139-1111-2222",
        "createTime": "2025-05-23 12:15:00",
        "completeTime": "2025-05-23 13:20:00"
      }
    ],
    "total": 25,
    "page": 1,
    "pageSize": 10
  }
}
#### 失败响应
{
  "success": false,
  "message": "获取历史订单失败"
}

#### 订单对象字段说明

| 字段名         | 类型   | 说明                              | 是否必填 | 备注                    |
| -------------- | ------ | --------------------------------- | -------- | ----------------------- |
| id             | string | 订单ID                            | 是       | 唯一标识符              |
| sellerName   | string | 商家名称                          | 是       | 用于显示                |
| sellerAddress| string | 商家地址                          | 是       | 完整地址信息            |
| userAddress    | string | 用户地址                          | 是       | 送达地址                |
| userPhone      | string | 用户手机号                        | 是       | 联系方式                |
| createTime     | string | 订单创建时间                      | 是       | 格式：YYYY-MM-DD HH:mm:ss |
| completeTime   | string | 订单完成时间                      | 是       | 格式：YYYY-MM-DD HH:mm:ss |