# 评价相关API

## 1. 创建评价

### 请求信息
- 请求路径：`/api/reviews`
- 请求方法：POST
- 请求参数：
  ```json
  {
    "userId": 1,          // 用户ID
    "itemId": 1,          // 商品ID
    "orderId": 1,         // 订单ID
    "rating": 5,          // 评分（1-5）
    "comment": "商品很好",  // 评论内容
    "images": "url1,url2" // 图片URL，多个用逗号分隔
  }
  ```

### 响应信息
```json
{
  "code": 200,
  "success": true,
  "message": "评价成功",
  "data": {
    "id": 1,
    "userId": 1,
    "itemId": 1,
    "orderId": 1,
    "rating": 5,
    "comment": "商品很好",
    "images": "url1,url2",
    "createdAt": "2024-03-20T10:00:00",
    "updatedAt": "2024-03-20T10:00:00"
  }
}
```

## 2. 获取商品评价列表

### 请求信息
- 请求路径：`/api/reviews/item/{itemId}`
- 请求方法：GET
- 路径参数：
  - itemId：商品ID
- 查询参数：
  - page：页码（从0开始，默认0）
  - size：每页大小（默认10）

### 响应信息
```json
{
  "code": 200,
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "userId": 1,
        "itemId": 1,
        "orderId": 1,
        "rating": 5,
        "comment": "商品很好",
        "images": "url1,url2",
        "createdAt": "2024-03-20T10:00:00",
        "updatedAt": "2024-03-20T10:00:00"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "size": 10,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "numberOfElements": 1,
    "first": true,
    "empty": false
  }
}
```

## 3. 获取用户评价列表

### 请求信息
- 请求路径：`/api/reviews/user/{userId}`
- 请求方法：GET
- 路径参数：
  - userId：用户ID
- 查询参数：
  - page：页码（从0开始，默认0）
  - size：每页大小（默认10）

### 响应信息
```json
{
  "code": 200,
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "userId": 1,
        "itemId": 1,
        "orderId": 1,
        "rating": 5,
        "comment": "商品很好",
        "images": "url1,url2",
        "createdAt": "2024-03-20T10:00:00",
        "updatedAt": "2024-03-20T10:00:00"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "size": 10,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "numberOfElements": 1,
    "first": true,
    "empty": false
  }
}
```

## 4. 删除评价

### 请求信息
- 请求路径：`/api/reviews/{reviewId}`
- 请求方法：DELETE
- 路径参数：
  - reviewId：评价ID
- 查询参数：
  - userId：用户ID（用于验证权限）

### 响应信息
```json
{
  "code": 200,
  "success": true,
  "message": "评价删除成功"
}
```

### 错误响应
```json
{
  "code": 500,
  "success": false,
  "message": "删除评价失败：评价不存在"
}
```
或
```json
{
  "code": 500,
  "success": false,
  "message": "删除评价失败：无权删除此评价"
}
```

## 5. 管理员删除评价

### 请求信息
- 请求路径：`/api/reviews/admin/{reviewId}`
- 请求方法：DELETE
- 路径参数：
  - reviewId：评价ID
- 查询参数：
  - adminId：管理员ID（用于验证权限）

### 响应信息
```json
{
  "code": 200,
  "success": true,
  "message": "评价删除成功"
}
```

### 错误响应
```json
{
  "code": 500,
  "success": false,
  "message": "删除评价失败：管理员不存在"
}
```
或
```json
{
  "code": 500,
  "success": false,
  "message": "删除评价失败：无权限执行此操作"
}
```
或
```json
{
  "code": 500,
  "success": false,
  "message": "删除评价失败：评价不存在"
}
``` 