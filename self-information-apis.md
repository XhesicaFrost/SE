# 个人信息相关API文档

## 1. 用户浏览历史

### 接口信息
- **请求方式**：POST
- **请求地址**：`${BASE_URL}/api/browse-history`
- **请求头**：
  ```
  Content-Type: application/json
  ```
- **请求体**：
  ```json
  {
      "userId": "1",
      "targetType": "SHOP",
      "targetId": "2",
      "name": "测试店铺",
      "image": "http://example.com/image.jpg",
      "description": "这是一个测试店铺"
  }
  ```

### 返回数据格式
```json
{
    "code": 200,
    "success": true,
    "message": "浏览记录添加成功"
}
```

## 2. 获取用户浏览历史

### 接口信息
- **请求方式**：GET
- **请求地址**：`${BASE_URL}/api/browse-history/user/{userId}`
- **请求参数**：路径参数 + 查询参数

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| userId   | string | 用户ID   | 是       |
| page     | number | 页码     | 否       |
| size     | number | 每页大小 | 否       |

### 返回数据格式
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
}
```

## 3. 获取用户特定类型的浏览历史

### 接口信息
- **请求方式**：GET
- **请求地址**：`${BASE_URL}/api/browse-history/user/{userId}/type/{targetType}`
- **请求参数**：路径参数 + 查询参数

| 参数名     | 类型   | 说明     | 是否必填 |
| ---------- | ------ | -------- | -------- |
| userId     | string | 用户ID   | 是       |
| targetType | string | 目标类型（SHOP/DISH） | 是 |
| page       | number | 页码     | 否       |
| size       | number | 每页大小 | 否       |

### 返回数据格式
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
}
```

## 4. 删除浏览历史记录

### 接口信息
- **请求方式**：DELETE
- **请求地址**：`${BASE_URL}/api/browse-history/user/{userId}/type/{targetType}/target/{targetId}`
- **请求参数**：路径参数

| 参数名     | 类型   | 说明     | 是否必填 |
| ---------- | ------ | -------- | -------- |
| userId     | string | 用户ID   | 是       |
| targetType | string | 目标类型（SHOP/DISH） | 是 |
| targetId   | string | 目标ID   | 是       |

### 返回数据格式
```json
{
    "code": 200,
    "success": true,
    "message": "浏览记录删除成功"
}
```

## 5. 获取用户偏好

### 接口信息
- **请求方式**：GET
- **请求地址**：`${BASE_URL}/api/preferences/{userId}`
- **请求参数**：路径参数

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| userId   | string | 用户ID   | 是       |

### 返回数据格式
```json
{
    "code": 200,
    "success": true,
    "data": {
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
}
```

## 6. 更新用户偏好

### 接口信息
- **请求方式**：PUT
- **请求地址**：`${BASE_URL}/api/preferences/{userId}`
- **请求参数**：路径参数 + 请求体

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| userId   | string | 用户ID   | 是       |

请求体格式：
```json
{
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
```

### 返回数据格式
```json
{
    "code": 200,
    "success": true,
    "message": "偏好设置更新成功"
}
```

## 7. 添加收藏

### 接口信息
- **请求方式**：POST
- **请求地址**：`${BASE_URL}/api/favorites/user/{userId}/{targetType}/{targetId}`
- **请求参数**：路径参数 + 请求体

| 参数名     | 类型   | 说明     | 是否必填 |
| ---------- | ------ | -------- | -------- |
| userId     | string | 用户ID   | 是       |
| targetType | string | 收藏类型（SHOP/DISH） | 是 |
| targetId   | string | 目标ID   | 是       |

请求体格式：
```json
{
    "name": "测试店铺",
    "image": "http://example.com/image.jpg",
    "description": "这是一个测试店铺",
    "rating": 4.5
}
```

### 返回数据格式
```json
{
    "code": 200,
    "success": true,
    "message": "收藏成功"
}
```

## 8. 获取用户的所有收藏

### 接口信息
- **请求方式**：GET
- **请求地址**：`${BASE_URL}/api/favorites/user/{userId}`
- **请求参数**：路径参数 + 查询参数

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| userId   | string | 用户ID   | 是       |
| page     | number | 页码     | 否       |
| size     | number | 每页大小 | 否       |

### 返回数据格式
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

## 9. 获取用户特定类型的收藏

### 接口信息
- **请求方式**：GET
- **请求地址**：`${BASE_URL}/api/favorites/user/{userId}/{targetType}`
- **请求参数**：路径参数 + 查询参数

| 参数名     | 类型   | 说明     | 是否必填 |
| ---------- | ------ | -------- | -------- |
| userId     | string | 用户ID   | 是       |
| targetType | string | 收藏类型（SHOP/DISH） | 是 |
| page       | number | 页码     | 否       |
| size       | number | 每页大小 | 否       |

### 返回数据格式
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

## ~~10. 个性化推荐（废除，具体recommendation-apis.md）~~

### 10.1 获取个性化推荐
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

### 10.2 获取热门推荐
- **接口**: `GET /api/recommendations/popular`
- **描述**: 获取当前热门店铺和商品
- **权限**: 公开访问
- **参数**:
  - `type`: 推荐类型（SHOP/DISH）
  - `limit`: 推荐数量
- **响应**: 同10.1

### 10.3 获取相似推荐
- **接口**: `GET /api/recommendations/similar/{targetType}/{targetId}`
- **描述**: 获取与指定店铺或商品相似的其他店铺或商品
- **权限**: 公开访问
- **参数**:
  - `targetType`: 目标类型（SHOP/DISH）
  - `targetId`: 目标ID
  - `limit`: 推荐数量
- **响应**: 同10.1

## 11. 推荐反馈

### 11.1 提交推荐反馈
- **接口**: `POST /api/recommendations/feedback`
- **描述**: 提交对推荐结果的反馈
- **权限**: 需要用户登录
- **参数**:
  - `userId`: 用户ID
  - `recommendationId`: 推荐ID
  - `type`: 反馈类型（LIKE/DISLIKE）
  - `comment`: 反馈评论（可选）
- **响应**: 200 OK

### 11.2 获取推荐反馈
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

## 12. 获取用户订单列表

### 接口信息
- **请求方式**：GET
- **请求地址**：`${BASE_URL}/api/orders/user/{userId}`
- **请求参数**：路径参数 + 查询参数

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| userId   | string | 用户ID   | 是       |
| page     | number | 页码     | 否       |
| size     | number | 每页大小 | 否       |

### 返回数据格式
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

## 13. 获取订单详情

### 接口信息
- **请求方式**：GET
- **请求地址**：`${BASE_URL}/api/orders/{orderId}`
- **请求参数**：路径参数

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| orderId  | string | 订单ID   | 是       |

### 返回数据格式
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

## 14. 更新订单状态

### 接口信息
- **请求方式**：PUT
- **请求地址**：`${BASE_URL}/api/orders/{orderId}/status`
- **请求参数**：路径参数 + 查询参数

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| orderId  | string | 订单ID   | 是       |
| status   | string | 订单状态 | 是       |

### 返回数据格式
```json
{
    "code": 200,
    "success": true,
    "message": "订单状态更新成功"
}
```

## 15. 更新骑手位置

### 接口信息
- **请求方式**：PUT
- **请求地址**：`${BASE_URL}/api/orders/{orderId}/rider-location`
- **请求参数**：路径参数 + 查询参数

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| orderId  | string | 订单ID   | 是       |
| location | string | 骑手位置 | 是       |

### 返回数据格式
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