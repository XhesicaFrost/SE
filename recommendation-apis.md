# 推荐系统API文档

## 1. 获取用户推荐商品

### 接口信息
- **请求方式**：GET
- **请求地址**：`${BASE_URL}/api/recommendations/{userId}`
- **请求参数**：路径参数

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| userId   | string | 用户ID   | 是       |

### 返回数据格式
```json
{
    "code": 200,
    "success": true,
    "data": [
        {
            "id": 1,
            "userId": 123,
            "itemId": 456,
            "score": 0.85
        }
        // ... 更多推荐商品
    ]
}
```

## 2. 生成用户推荐

### 接口信息
- **请求方式**：POST
- **请求地址**：`${BASE_URL}/api/recommendations/generate/{userId}`
- **请求参数**：路径参数 + 请求体

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| userId   | string | 用户ID   | 是       |

请求体格式：
```json
{
    "1": {  // 用户ID
        "101": 5.0,  // 商品ID: 评分
        "102": 4.0,
        "103": 3.0
    },
    "2": {
        "101": 4.0,
        "102": 5.0,
        "104": 3.0
    }
    // ... 更多用户的评分数据
}
```

### 返回数据格式
```json
{
    "code": 200,
    "success": true,
    "message": "推荐生成成功"
}
```

## 3. 店铺筛选

### 接口信息
- **请求方式**：GET
- **请求地址**：`${BASE_URL}/api/recommendations/shops/filter`
- **请求参数**：查询参数

| 参数名          | 类型    | 说明                 | 是否必填 |
| --------------- | ------- | -------------------- | -------- |
| shopType        | string  | 店铺类型             | 否       |
| latitude        | number  | 纬度                 | 否       |
| longitude       | number  | 经度                 | 否       |
| minRating       | number  | 最低评分             | 否       |
| maxRating       | number  | 最高评分             | 否       |
| minPrice        | number  | 最低价格             | 否       |
| maxPrice        | number  | 最高价格             | 否       |
| maxDeliveryTime | number  | 最大配送时间（分钟） | 否       |

### 返回数据格式
```json
{
    "code": 200,
    "success": true,
    "data": [
        {
            "id": 1,
            "name": "示例店铺1",
            "type": "中餐",
            "rating": 4.5,
            "minPrice": 20.0,
            "maxPrice": 100.0,
            "deliveryTime": 30,
            "location": {
                "latitude": 39.9042,
                "longitude": 116.4074
            }
        }
        // ... 更多店铺
    ]
}
```

## 4. 获取用户位置（新增）

### 接口信息
- **请求方式**：GET
- **请求地址**：`${BASE_URL}/api/user/location`
- **请求参数**：查询参数

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| userId   | string | 用户ID   | 是       |

### 返回数据格式
```json
{
    "code": 200,
    "success": true,
    "data": {
        "latitude": 39.9042,
        "longitude": 116.4074,
        "address": "北京市海淀区中关村大街1号"
    }
}
```

## 5. 更新用户位置（新增）

### 接口信息
- **请求方式**：POST
- **请求地址**：`${BASE_URL}/api/user/location`
- **请求参数**：请求体

| 参数名     | 类型   | 说明     | 是否必填 |
| ---------- | ------ | -------- | -------- |
| userId     | string | 用户ID   | 是       |
| latitude   | number | 纬度     | 是       |
| longitude  | number | 经度     | 是       |
| address    | string | 地址     | 否       |

### 返回数据格式
```json
{
    "code": 200,
    "success": true,
    "message": "位置更新成功"
}
```

## 6. 获取店铺类型列表（新增）

### 接口信息
- **请求方式**：GET
- **请求地址**：`${BASE_URL}/api/shop/types`
- **请求参数**：无

### 返回数据格式
```json
{
    "code": 200,
    "success": true,
    "data": [
        {
            "id": 1,
            "name": "中餐",
            "icon": "chinese-food.png"
        },
        {
            "id": 2,
            "name": "西餐",
            "icon": "western-food.png"
        }
        // ... 更多店铺类型
    ]
}
```

## 7. 获取用户历史订单（用于推荐系统）

### 接口信息
- **请求方式**：GET
- **请求地址**：`${BASE_URL}/api/user/orders/history`
- **请求参数**：查询参数

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| userId   | string | 用户ID   | 是       |
| limit    | number | 限制数量 | 否       |

### 返回数据格式
```json
{
    "code": 200,
    "success": true,
    "data": [
        {
            "orderId": 1,
            "shopId": 101,
            "items": [
                {
                    "itemId": 201,
                    "quantity": 2,
                    "price": 15.0
                }
            ],
            "totalAmount": 30.0,
            "createTime": "2024-03-20T12:00:00"
        }
        // ... 更多订单
    ]
}
```

## 8. 获取店铺推荐（新增）

### 接口信息
- **请求方式**：GET
- **请求地址**：`${BASE_URL}/api/recommendations/shops/{userId}`
- **请求参数**：路径参数

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| userId   | string | 用户ID   | 是       |

### 返回数据格式
```json
{
    "code": 200,
    "success": true,
    "data": [
        {
            "id": 1,
            "name": "示例店铺1",
            "type": "中餐",
            "rating": 4.5,
            "minPrice": 20.0,
            "maxPrice": 100.0,
            "deliveryTime": 30,
            "location": {
                "latitude": 39.9042,
                "longitude": 116.4074
            },
            "score": 0.85,
            "recommendationReason": "根据您的历史订单和偏好推荐"
        }
        // ... 更多推荐店铺
    ]
}
```

## 9. 生成店铺推荐（新增）

### 接口信息
- **请求方式**：POST
- **请求地址**：`${BASE_URL}/api/recommendations/shops/generate/{userId}`
- **请求参数**：路径参数 + 请求体

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| userId   | string | 用户ID   | 是       |

请求体格式：
```json
{
    "userPreferences": {
        "preferredTypes": ["中餐", "西餐"],
        "priceRange": {
            "min": 20,
            "max": 100
        },
        "maxDeliveryTime": 30
    },
    "location": {
        "latitude": 39.9042,
        "longitude": 116.4074
    }
}
```

### 返回数据格式
```json
{
    "code": 200,
    "success": true,
    "message": "店铺推荐生成成功"
}
```

## 10. 获取用户店铺偏好（新增）

### 接口信息
- **请求方式**：GET
- **请求地址**：`${BASE_URL}/api/user/shop-preferences`
- **请求参数**：查询参数

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| userId   | string | 用户ID   | 是       |

### 返回数据格式
```json
{
    "code": 200,
    "success": true,
    "data": {
        "preferredTypes": ["中餐", "西餐"],
        "priceRange": {
            "min": 20,
            "max": 100
        },
        "maxDeliveryTime": 30,
        "lastUpdated": "2024-03-20T12:00:00"
    }
}
```

## 11. 更新用户店铺偏好（新增）

### 接口信息
- **请求方式**：POST
- **请求地址**：`${BASE_URL}/api/user/shop-preferences`
- **请求参数**：请求体

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| userId   | string | 用户ID   | 是       |

请求体格式：
```json
{
    "preferredTypes": ["中餐", "西餐"],
    "priceRange": {
        "min": 20,
        "max": 100
    },
    "maxDeliveryTime": 30
}
```

### 返回数据格式
```json
{
    "code": 200,
    "success": true,
    "message": "店铺偏好更新成功"
}
```

## 12. 获取店铺详情（新增）

### 接口信息
- **请求方式**：GET
- **请求地址**：`${BASE_URL}/api/shops/{shopId}`
- **请求参数**：路径参数

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| shopId   | string | 店铺ID   | 是       |

### 返回数据格式
```json
{
    "code": 200,
    "success": true,
    "data": {
        "id": 1,
        "name": "示例店铺1",
        "type": "中餐",
        "rating": 4.5,
        "minPrice": 20.0,
        "maxPrice": 100.0,
        "deliveryTime": 30,
        "location": {
            "latitude": 39.9042,
            "longitude": 116.4074
        },
        "address": "北京市海淀区中关村大街1号",
        "businessHours": "10:00-22:00",
        "phone": "010-12345678",
        "description": "这是一家特色中餐厅",
        "hotItems": [
            {
                "id": 1,
                "name": "特色炒饭",
                "price": 28.0,
                "sales": 1000,
                "image": "https://example.com/image1.jpg"
            }
        ],
        "categories": [
            {
                "id": 1,
                "name": "主食",
                "items": [
                    {
                        "id": 1,
                        "name": "特色炒饭",
                        "price": 28.0,
                        "image": "https://example.com/image1.jpg",
                        "description": "使用优质大米制作",
                        "sales": 1000
                    }
                ]
            }
        ]
    }
}
```

## 13. 获取商品详情（新增）

### 接口信息
- **请求方式**：GET
- **请求地址**：`${BASE_URL}/api/items/{itemId}`
- **请求参数**：路径参数

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| itemId   | string | 商品ID   | 是       |

### 返回数据格式
```json
{
    "code": 200,
    "success": true,
    "data": {
        "id": 1,
        "name": "特色炒饭",
        "price": 28.0,
        "image": "https://example.com/image1.jpg",
        "description": "使用优质大米制作",
        "sales": 1000,
        "rating": 4.8,
        "shopId": 1,
        "shopName": "示例店铺1",
        "category": "主食",
        "reviews": [
            {
                "id": 1,
                "userId": 123,
                "username": "用户A",
                "rating": 5,
                "content": "非常好吃！",
                "createTime": "2024-03-20T12:00:00",
                "images": ["https://example.com/review1.jpg"]
            }
        ]
    }
}
```

## 14. 获取店铺热销商品（新增）

### 接口信息
- **请求方式**：GET
- **请求地址**：`${BASE_URL}/api/shops/{shopId}/hot-items`
- **请求参数**：路径参数 + 查询参数

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| shopId   | string | 店铺ID   | 是       |
| limit    | number | 限制数量 | 否       |

### 返回数据格式
```json
{
    "code": 200,
    "success": true,
    "data": [
        {
            "id": 1,
            "name": "特色炒饭",
            "price": 28.0,
            "image": "https://example.com/image1.jpg",
            "sales": 1000,
            "rating": 4.8
        }
    ]
}
```

## 15. 获取店铺商品分类（新增）

### 接口信息
- **请求方式**：GET
- **请求地址**：`${BASE_URL}/api/shops/{shopId}/categories`
- **请求参数**：路径参数

| 参数名   | 类型   | 说明     | 是否必填 |
| -------- | ------ | -------- | -------- |
| shopId   | string | 店铺ID   | 是       |

### 返回数据格式
```json
{
    "code": 200,
    "success": true,
    "data": [
        {
            "id": 1,
            "name": "主食",
            "items": [
                {
                    "id": 1,
                    "name": "特色炒饭",
                    "price": 28.0,
                    "image": "https://example.com/image1.jpg",
                    "description": "使用优质大米制作",
                    "sales": 1000
                }
            ]
        }
    ]
}
```

## 数据库表设计

### 1. 推荐记录表（recommendations）
```sql
CREATE TABLE recommendations (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    user_id INTEGER NOT NULL,
    item_id INTEGER NOT NULL,
    score DOUBLE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (item_id) REFERENCES items(id)
);
```

### 2. 店铺表（shops）
```sql
CREATE TABLE shops (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    rating DOUBLE DEFAULT 0.0,
    min_price DOUBLE NOT NULL,
    max_price DOUBLE NOT NULL,
    delivery_time INTEGER NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    address VARCHAR(200) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 3. 用户位置表（user_locations）
```sql
CREATE TABLE user_locations (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    user_id INTEGER NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    address VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 4. 店铺类型表（shop_types）
```sql
CREATE TABLE shop_types (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    icon VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 5. 店铺推荐记录表（shop_recommendations）
```sql
CREATE TABLE shop_recommendations (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    user_id INTEGER NOT NULL,
    shop_id INTEGER NOT NULL,
    score DOUBLE NOT NULL,
    recommendation_reason VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (shop_id) REFERENCES shops(id)
);
```

### 6. 用户店铺偏好表（user_shop_preferences）
```sql
CREATE TABLE user_shop_preferences (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    user_id INTEGER NOT NULL,
    preferred_types JSON,
    min_price DOUBLE,
    max_price DOUBLE,
    max_delivery_time INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 7. 商品表（items）
```sql
CREATE TABLE items (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    shop_id INTEGER NOT NULL,
    category_id INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL,
    image VARCHAR(200),
    description TEXT,
    sales INTEGER DEFAULT 0,
    rating DOUBLE DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (shop_id) REFERENCES shops(id),
    FOREIGN KEY (category_id) REFERENCES item_categories(id)
);
```

### 8. 商品分类表（item_categories）
```sql
CREATE TABLE item_categories (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    shop_id INTEGER NOT NULL,
    name VARCHAR(50) NOT NULL,
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (shop_id) REFERENCES shops(id)
);
```

### 9. 商品评价表（item_reviews）
```sql
CREATE TABLE item_reviews (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    item_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    rating INTEGER NOT NULL,
    content TEXT,
    images JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES items(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
); 