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
  | 参数名 | 类型   | 说明     | 是否必填 |
  | ------ | ------ | -------- | -------- |
  | merchantStatus | string | 可以为“未注册/正常/审批中/封禁中”   | 是       |

```json
{
  "code": 200,
  "success": true,
  "merchantId": "商家ID",
  "merchantName": "商家名称",
  "merchantStatus":"正常"
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
### 商家注册接口

- **请求方式**：POST  
- **请求地址**：`/merchant/register`  
- **请求参数**（FormData 格式）：

| 参数名      | 类型    | 说明         | 是否必填 |
| ----------- | ------- | ------------ | -------- |
| shopName    | string  | 店铺名称     | 是       |
| shopAddress | string  | 店铺地址     | 是       |
| shopImage   | file    | 店铺图片     | 是       |

- **请求示例**：

以 FormData 方式提交：

```
POST /merchant/register
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
| merchantId | string | 商家ID     | 是       |

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
| merchantId | string | 商家ID     | 是       |

- **返回示例**：

```json
{
  "code": 200,
  "success": true,
  "data": {
    "shopName": "示例店铺",
    "shopImg": "https://xxx.com/shop.jpg",
    "shopAddress": "示例地址"
  }
}
```

### 本文件用到的网络接口格式说明

#### 1. 获取商品信息接口

- **请求方式**：GET  
- **请求地址**：`${BASE_URL}/merchant/item`  
- **请求参数**（Query String）：

| 参数名 | 类型   | 说明     | 是否必填 |
| ------ | ------ | -------- | -------- |
| id     | string | 商品ID   | 是       |

- **请求示例**：
```
GET /merchant/item?id=123
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
- **请求地址**：`${BASE_URL}/merchant/item/register`  
- **发送数据格式**：FormData（multipart/form-data）

| 参数名    | 类型   | 说明         | 是否必填 |
| --------- | ------ | ------------ | -------- |
| itemId    | string | 商品ID       | 是       |
| itemName  | string | 商品名称     | 是       |
| itemPrice | number | 商品单价     | 是       |
| itemImage | file   | 商品图片     | 否（如未更改可不传） |

- **请求示例**：
```
POST /merchant/item/register
Content-Type: multipart/form-data

itemId=123
itemName=商品A
itemPrice=10.5
itemImage=文件（可选）
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
- **请求地址**：`${BASE_URL}/merchant/item/register`  
- **发送数据格式**：FormData（`multipart/form-data`）

| 参数名      | 类型   | 说明         | 是否必填 |
| ----------- | ------ | ------------ | -------- |
| itemName    | string | 商品名称     | 是       |
| itemImage   | file   | 商品图片     | 是       |
| itemPrice   | number | 商品单价     | 是       |
| merchantId  | string | 商家ID       | 是       |

- **请求示例**：
```
POST /merchant/item/register
Content-Type: multipart/form-data

itemName=商品A
itemImage=文件
itemPrice=10.5
merchantId=xxx
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

### 本页面用到的网络接口格式说明

#### 1. 获取店铺信息接口

- **请求方式**：GET  
- **请求地址**：`${BASE_URL}/shop`  
- **请求参数**（Query String）：

| 参数名     | 类型   | 说明     | 是否必填 |
| ---------- | ------ | -------- | -------- |
| merchantId | string | 商家ID   | 是       |

- **请求示例**：
```
GET /shop?merchantId=xxx
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
- **请求地址**：`${BASE_URL}/merchant/edit`  
- **发送数据格式**：FormData（`multipart/form-data`）

| 参数名      | 类型   | 说明         | 是否必填 |
| ----------- | ------ | ------------ | -------- |
| shopName    | string | 店铺名称     | 是       |
| shopAddress | string | 店铺地址     | 是       |
| shopImage   | file   | 店铺图片     | 否（如未更改可不传） |
| merchantId  | string | 商家ID       | 是       |

- **请求示例**：
```
POST /merchant/edit
Content-Type: multipart/form-data

shopName=示例店铺
shopAddress=示例地址
shopImage=文件（可选）
merchantId=xxx
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
- **请求地址**：`${BASE_URL}/merchant/item/register`  
- **发送数据格式**：FormData（`multipart/form-data`）

| 参数名      | 类型    | 说明         | 是否必填 |
| ----------- | ------- | ------------ | -------- |
| itemName    | string  | 商品名称     | 是       |
| itemImage   | file    | 商品图片     | 是       |
| itemPrice   | number  | 商品单价     | 是       |
| merchantId  | string  | 商家ID       | 是       |

- **请求示例**：
```
POST /merchant/item/register
Content-Type: multipart/form-data

itemName=商品A
itemImage=文件
itemPrice=10.5
merchantId=xxx
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
- **请求地址**：`${BASE_URL}/merchant/item`  
- **发送数据格式**：JSON

| 参数名 | 类型   | 说明     | 是否必填 |
| ------ | ------ | -------- | -------- |
| id     | string | 商品ID   | 是       |

- **请求示例**：
```
DELETE /merchant/item
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
| merchantId | string | 商家ID   | 是       |

- **请求示例**：
```
GET /approval/shop?merchantId=xxx
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
| merchantId | string | 商家ID   | 是       |

- **请求示例**：
```
GET /approval/item?merchantId=xxx
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
- **请求地址**：`${BASE_URL}/merchant/promotion`  
- **请求参数**（Query String）：

| 参数名     | 类型   | 说明     | 是否必填 |
| ---------- | ------ | -------- | -------- |
| merchantId | string | 商家ID   | 是       |

- **请求示例**：
```
GET /merchant/promotion?merchantId=xxx
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
- **请求地址**：`${BASE_URL}/merchant/promotion`  
- **发送数据格式**：JSON

| 参数名       | 类型   | 说明         | 是否必填 |
| ------------ | ------ | ------------ | -------- |
| promotionId  | string | 促销活动ID   | 是       |

- **请求示例**：
```
DELETE /merchant/promotion
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
- **请求地址**：`${BASE_URL}/merchant/promotion/detail`  
- **请求参数**（Query String）：

| 参数名       | 类型   | 说明         | 是否必填 |
| ------------ | ------ | ------------ | -------- |
| promotionId  | string | 促销活动ID   | 是       |

- **请求示例**：
```
GET /merchant/promotion/detail?promotionId=xxx
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
- **请求地址**：`${BASE_URL}/merchant/promotion/edit`  
- **发送数据格式**：FormData（`multipart/form-data`）

| 参数名        | 类型    | 说明         | 是否必填 |
| ------------- | ------- | ------------ | -------- |
| promotionId   | string  | 促销活动ID   | 是       |
| promotionName | string  | 活动名称     | 是       |
| full          | number  | 满减门槛     | 是       |
| minus         | number  | 减免金额     | 是       |
| startTime     | string  | 开始时间     | 是       |
| endTime       | string  | 结束时间     | 是       |
| merchantId    | string  | 商家ID       | 是       |

- **请求示例**：
```
POST /merchant/promotion/edit
Content-Type: multipart/form-data

promotionId=xxx
promotionName=满100减20
full=100
minus=20
startTime=2025-05-01T00:00:00
endTime=2025-05-31T23:59:59
merchantId=yyy
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
- **请求地址**：`${BASE_URL}/merchant/promotion/register`  
- **发送数据格式**：FormData（`multipart/form-data`）

| 参数名        | 类型    | 说明         | 是否必填 |
| ------------- | ------- | ------------ | -------- |
| promotionName | string  | 活动名称     | 是       |
| full          | number  | 满减门槛     | 是       |
| minus         | number  | 减免金额     | 是       |
| startTime     | string  | 开始时间     | 是       |
| endTime       | string  | 结束时间     | 是       |
| merchantId    | string  | 商家ID       | 是       |

- **请求示例**：
```
POST /merchant/promotion/register
Content-Type: multipart/form-data

promotionName=满100减20
full=100
minus=20
startTime=2025-05-01T00:00:00
endTime=2025-05-31T23:59:59
merchantId=xxx
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
- **请求地址**：`${BASE_URL}/merchant/order`  
- **请求参数**（Query String）：

| 参数名     | 类型   | 说明     | 是否必填 |
| ---------- | ------ | -------- | -------- |
| merchantId | string | 商家ID   | 是       |

- **请求示例**：
```
GET /merchant/order?merchantId=xxx
```

- **返回数据格式**（JSON）：
```json
{
  "success": true,
  "data": [
    {
      "id": 1005,
      "totalPrice": 58.5,
      "served": false,
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
- **请求地址**：`${BASE_URL}/merchant/order/serve`  
- **发送数据格式**：JSON

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| orderId  | string | 订单ID   | 是       |

- **请求示例**：
```
POST /merchant/order/serve
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
- **请求地址**：`${BASE_URL}/merchant/data/sales`  
- **请求参数**（Query String）：

| 参数名     | 类型   | 说明         | 是否必填 |
| ---------- | ------ | ------------ | -------- |
| merchantId | string | 商家ID       | 是       |
| startDate  | string | 开始日期（yyyy-mm-dd） | 是 |
| endDate    | string | 结束日期（yyyy-mm-dd） | 是 |

- **请求示例**：
```
GET /merchant/data/sales?merchantId=xxx&startDate=2025-05-01&endDate=2025-05-07
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
- **请求地址**：`${BASE_URL}/merchant/data/comment`  
- **请求参数**（Query String）：

| 参数名     | 类型   | 说明         | 是否必填 |
| ---------- | ------ | ------------ | -------- |
| merchantId | string | 商家ID       | 是       |
| startDate  | string | 开始日期（yyyy-mm-dd） | 是 |
| endDate    | string | 结束日期（yyyy-mm-dd） | 是 |

- **请求示例**：
```
GET /merchant/data/comment?merchantId=xxx&startDate=2025-05-01&endDate=2025-05-07
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
- **请求地址**：`${BASE_URL}/merchant/data/download`  
- **请求参数**（Query String）：

| 参数名     | 类型   | 说明         | 是否必填 |
| ---------- | ------ | ------------ | -------- |
| merchantId | string | 商家ID       | 是       |
| startDate  | string | 开始日期（yyyy-mm-dd） | 是 |
| endDate    | string | 结束日期（yyyy-mm-dd） | 是 |

- **请求示例**：
```
GET /merchant/data/download?merchantId=xxx&startDate=2025-05-01&endDate=2025-05-07
```

- **返回内容**：文件下载（如 Excel、CSV 等格式的详细数据）