# 推荐系统API文档

## 1. 用户浏览历史

### 1.1 添加浏览记录
- **接口**: `POST /api/browse-history`
- **描述**: 记录用户浏览店铺或商品的历史
- **权限**: 需要用户登录
- **参数**:
  - `userId`: 用户ID
  - `targetType`: 目标类型（SHOP/DISH）
  - `targetId`: 目标ID
- **响应**: 200 OK

### 1.2 获取用户浏览历史
- **接口**: `GET /api/browse-history/user/{userId}`
- **描述**: 获取用户的所有浏览历史记录
- **权限**: 需要用户登录，只能查看自己的记录
- **参数**:
  - `userId`: 用户ID
  - `page`: 页码（从0开始）
  - `size`: 每页大小
- **响应**: 
  ```json
  {
    "content": [
      {
        "id": 1,
        "userId": 1,
        "targetType": "SHOP",
        "targetId": 1,
        "browseTime": "2024-03-20T10:00:00",
        "name": "店铺名称",
        "image": "店铺图片URL",
        "description": "店铺描述"
      }
    ],
    "totalElements": 100,
    "totalPages": 10,
    "size": 10,
    "number": 0
  }
  ```

### 1.3 获取用户特定类型的浏览历史
- **接口**: `GET /api/browse-history/user/{userId}/type/{targetType}`
- **描述**: 获取用户特定类型（店铺/商品）的浏览历史记录
- **权限**: 需要用户登录，只能查看自己的记录
- **参数**:
  - `userId`: 用户ID
  - `targetType`: 目标类型（SHOP/DISH）
  - `page`: 页码（从0开始）
  - `size`: 每页大小
- **响应**: 同1.2

### 1.4 删除浏览历史记录
- **接口**: `DELETE /api/browse-history/user/{userId}/type/{targetType}/target/{targetId}`
- **描述**: 删除用户特定的浏览历史记录
- **权限**: 需要用户登录，只能删除自己的记录
- **参数**:
  - `userId`: 用户ID
  - `targetType`: 目标类型（SHOP/DISH）
  - `targetId`: 目标ID
- **响应**: 200 OK

## 2. 个性化推荐

### 2.1 获取个性化推荐
- **接口**: `GET /api/recommendations`
- **描述**: 获取基于用户历史行为的个性化推荐
- **权限**: 需要用户登录
- **参数**:
  - `userId`: 用户ID
  - `type`: 推荐类型（SHOP/DISH）
  - `limit`: 推荐数量
- **响应**:
  ```json
  {
    "recommendations": [
      {
        "id": 1,
        "type": "SHOP",
        "name": "推荐店铺名称",
        "image": "店铺图片URL",
        "description": "店铺描述",
        "score": 0.95
      }
    ]
  }
  ```

### 2.2 获取热门推荐
- **接口**: `GET /api/recommendations/popular`
- **描述**: 获取当前热门店铺和商品
- **权限**: 公开访问
- **参数**:
  - `type`: 推荐类型（SHOP/DISH）
  - `limit`: 推荐数量
- **响应**: 同2.1

### 2.3 获取相似推荐
- **接口**: `GET /api/recommendations/similar/{targetType}/{targetId}`
- **描述**: 获取与指定店铺或商品相似的其他店铺或商品
- **权限**: 公开访问
- **参数**:
  - `targetType`: 目标类型（SHOP/DISH）
  - `targetId`: 目标ID
  - `limit`: 推荐数量
- **响应**: 同2.1

## 3. 用户偏好

### 3.1 获取用户偏好
- **接口**: `GET /api/preferences/{userId}`
- **描述**: 获取用户的偏好设置
- **权限**: 需要用户登录，只能查看自己的偏好
- **参数**:
  - `userId`: 用户ID
- **响应**:
  ```json
  {
    "preferences": {
      "categories": ["中餐", "西餐"],
      "priceRange": {
        "min": 0,
        "max": 100
      },
      "location": {
        "latitude": 39.9042,
        "longitude": 116.4074,
        "radius": 5000
      }
    }
  }
  ```

### 3.2 更新用户偏好
- **接口**: `PUT /api/preferences/{userId}`
- **描述**: 更新用户的偏好设置
- **权限**: 需要用户登录，只能更新自己的偏好
- **参数**:
  - `userId`: 用户ID
  - **请求体**: 同3.1响应
- **响应**: 200 OK

## 4. 推荐反馈

### 4.1 提交推荐反馈
- **接口**: `POST /api/recommendations/feedback`
- **描述**: 提交对推荐结果的反馈
- **权限**: 需要用户登录
- **参数**:
  - `userId`: 用户ID
  - `recommendationId`: 推荐ID
  - `type`: 反馈类型（LIKE/DISLIKE）
  - `comment`: 反馈评论（可选）
- **响应**: 200 OK

### 4.2 获取推荐反馈
- **接口**: `GET /api/recommendations/feedback/{userId}`
- **描述**: 获取用户的推荐反馈历史
- **权限**: 需要用户登录，只能查看自己的反馈
- **参数**:
  - `userId`: 用户ID
  - `page`: 页码
  - `size`: 每页大小
- **响应**:
  ```json
  {
    "content": [
      {
        "id": 1,
        "userId": 1,
        "recommendationId": 1,
        "type": "LIKE",
        "comment": "反馈评论",
        "createTime": "2024-03-20T10:00:00"
      }
    ],
    "totalElements": 100,
    "totalPages": 10,
    "size": 10,
    "number": 0
  }
  ```

## 收藏相关API

### 添加收藏
```http
POST /api/favorites/{targetType}/{targetId}?userId={userId}
```

请求参数：
- `targetType`: 收藏类型（SHOP或ITEM）
- `targetId`: 店铺ID或商品ID
- `userId`: 用户ID

响应示例：
```json
{
    "code": 200,
    "success": true,
    "message": "收藏成功"
}
```

### 获取用户的所有收藏
```http
GET /api/favorites/user/{userId}?page=0&size=10
```

请求参数：
- `userId`: 用户ID
- `page`: 页码（从0开始）
- `size`: 每页数量

响应示例：
```json
{
    "code": 200,
    "success": true,
    "data": {
        "content": [
            {
                "id": 1,
                "userId": 1,
                "targetType": "SHOP",
                "targetId": 1,
                "createdAt": "2024-03-20T10:00:00",
                "name": "店铺名称",
                "image": "图片URL",
                "description": "描述",
                "rating": 4.5
            }
        ],
        "totalElements": 100,
        "totalPages": 10,
        "size": 10,
        "number": 0
    }
}
```

### 获取用户特定类型的收藏
```http
GET /api/favorites/user/{userId}/{targetType}?page=0&size=10
```

请求参数：
- `userId`: 用户ID
- `targetType`: 收藏类型（SHOP或ITEM）
- `page`: 页码
- `size`: 每页数量

响应示例：
```json
{
    "code": 200,
    "success": true,
    "data": {
        "content": [
            {
                "id": 1,
                "userId": 1,
                "targetType": "SHOP",
                "targetId": 1,
                "createdAt": "2024-03-20T10:00:00",
                "name": "店铺名称",
                "image": "图片URL",
                "description": "描述",
                "rating": 4.5
            }
        ],
        "totalElements": 100,
        "totalPages": 10,
        "size": 10,
        "number": 0
    }
}
```

### 取消收藏
```http
DELETE /api/favorites/{targetType}/{targetId}?userId={userId}
```

请求参数：
- `targetType`: 收藏类型（SHOP或ITEM）
- `targetId`: 店铺ID或商品ID
- `userId`: 用户ID

响应示例：
```json
{
    "code": 200,
    "success": true,
    "message": "取消收藏成功"
}
```

## 订单相关API

### 获取用户订单列表
```http
GET /api/orders/user/{userId}?page=0&size=10
```

请求参数：
- `userId`: 用户ID
- `page`: 页码（从0开始）
- `size`: 每页数量

响应示例：
```json
{
    "code": 200,
    "success": true,
    "data": {
        "content": [
            {
                "id": 1,
                "userId": 1,
                "shopId": 1,
                "shopName": "店铺名称",
                "shopImage": "店铺图片URL",
                "totalAmount": 99.9,
                "status": "DELIVERING",
                "deliveryAddress": "配送地址",
                "deliveryPhone": "13800138000",
                "deliveryName": "张三",
                "riderId": 1,
                "riderName": "李四",
                "riderPhone": "13900139000",
                "riderLocation": "116.4074,39.9042",
                "createdAt": "2024-03-20T10:00:00",
                "updatedAt": "2024-03-20T10:30:00",
                "orderItems": [
                    {
                        "id": 1,
                        "itemId": 1,
                        "itemName": "商品名称",
                        "itemImage": "商品图片URL",
                        "quantity": 2,
                        "unitPrice": 29.9,
                        "totalPrice": 59.8
                    }
                ]
            }
        ],
        "totalElements": 100,
        "totalPages": 10,
        "size": 10,
        "number": 0
    }
}
```

### 获取订单详情
```http
GET /api/orders/{orderId}
```

请求参数：
- `orderId`: 订单ID

响应示例：
```json
{
    "code": 200,
    "success": true,
    "data": {
        "id": 1,
        "userId": 1,
        "shopId": 1,
        "shopName": "店铺名称",
        "shopImage": "店铺图片URL",
        "totalAmount": 99.9,
        "status": "DELIVERING",
        "deliveryAddress": "配送地址",
        "deliveryPhone": "13800138000",
        "deliveryName": "张三",
        "riderId": 1,
        "riderName": "李四",
        "riderPhone": "13900139000",
        "riderLocation": "116.4074,39.9042",
        "createdAt": "2024-03-20T10:00:00",
        "updatedAt": "2024-03-20T10:30:00",
        "orderItems": [
            {
                "id": 1,
                "itemId": 1,
                "itemName": "商品名称",
                "itemImage": "商品图片URL",
                "quantity": 2,
                "unitPrice": 29.9,
                "totalPrice": 59.8
            }
        ]
    }
}
```

### 更新订单状态
```http
PUT /api/orders/{orderId}/status?status={status}
```

请求参数：
- `orderId`: 订单ID
- `status`: 订单状态（PENDING_PAYMENT/PAID/PREPARING/READY/DELIVERING/COMPLETED/CANCELLED）

响应示例：
```json
{
    "code": 200,
    "success": true,
    "message": "订单状态更新成功"
}
```

### 更新骑手位置
```http
PUT /api/orders/{orderId}/rider-location?location={location}
```

请求参数：
- `orderId`: 订单ID
- `location`: 骑手位置（经纬度，格式：经度,纬度）

响应示例：
```json
{
    "code": 200,
    "success": true,
    "message": "骑手位置更新成功"
}
```

订单状态说明：
- `PENDING_PAYMENT`: 待支付
- `PAID`: 已支付
- `PREPARING`: 商家备餐中
- `READY`: 商家已出餐
- `DELIVERING`: 配送中
- `COMPLETED`: 已完成
- `CANCELLED`: 已取消 