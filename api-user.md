api-user

1 用户-获取全部商店信息
方式： get
地址： `${BASE_URL}/shops`
参数： 无
示例： `GET /shops`
格式：
```
[
    {
        "id": 1,
        "image": "url",
        "name": "string",
        "rating": 1,
        "tags": [
            { "tag": "好吃" },
            ...
        ]
        "avgPrice": 10,
        "distance": 100.11,
        "deliverTime": 20, //单位：min
        "products": [ //只要三个示例
            {
                "image": "url"
            },
            ...
        ]
    },
    ...
]
```

2 用户获取商店信息
方式： get
地址： `${BASE_URL}/shop?shopId=${shopId}`
参数： shopId
示例： `GET /shop?shopId=xxx`
格式：

```
{
    "name": "string",
    "image": "url",
    "address": "address",
    "rating": 1,
    "monthlySales": 123,
    "deliveryTime": 20// min
}
```

3 用户获取商品信息
方式： get
地址： `${BASE_URL}/items?shopId=${shopId}`
参数： shopId
示例： `GET /items?shopId=xxx`
格式：

```
[
    {
        "id": 1,
        "image": "url",
        "name": "string",
        "description": "string string",
        "price": 100.12
    },
    ...
]
```

4 用户获取购物车信息
方式： get
地址： `${BASE_URL}/shopcart?userId=${userId}`
参数： userId
示例： `GET /shopcart?userId=xxx`
格式：

```
[
    {
        shop: {
            id: '1',
            name: '美味鲜果店',
            image: 'https://via.placeholder.com/60',
            address: '朝阳区建国路88号'
        },
        items: [
        {
            product: {
                id: '101',
                name: '进口红心火龙果',
                description: '越南进口，果肉鲜甜',
                price: 25.8,
                image: 'https://via.placeholder.com/80?text=火龙果'
            },
            quantity: 2
        },
        ...
        ]
    },
    ...
]
```

5 用户获取地址信息
方式： get
地址： `${BASE_URL}/address?userId=${userId}`
参数： userId
示例： `GET /address?userId=xxx`
格式：
```
[
    {
        id: 2,
        name: "123",
        phone: "123****1234",
        fullAddress: "string",
        current: false
    },
    ...
]
```

6 用户获取历史信息
方式： get
地址： `${BASE_URL}/history?userId=${userId}`
参数： userId
示例： `GET /history?userId=xxx`
格式：

```
[
    {
        id: 2,
        state: "arrived",
        fullAddress: "string",
        shop: {
            id: '1',
            name: '美味鲜果店',
            image: 'https://via.placeholder.com/60',
            address: '朝阳区建国路88号'
        },
        items: [
        {
            product: {
                id: '101',
                name: '进口红心火龙果',
                description: '越南进口，果肉鲜甜',
                price: 25.8,
                image: 'https://via.placeholder.com/80?text=火龙果'
            },
            quantity: 2
        },
        ...
        ]
    }
    ...
]
```

7 用户修改地址信息
方式： post
地址： `${BASE_URL}/address/edit`
格式： FormData（multipart/form-data）
示例：

```
POST /address/edit
Content-Type: multipart/form-data

id=1
name=namename（可选）
phone=12312341234（可选）
fullAddress=string（可选）
current=false（可选）
```
返回：
```
{
  "status": "success"
}
```
或
```
{
  "status": "fail"
}
```

8 用户增加地址信息
方式： post
地址： `${BASE_URL}/address/add`
格式： FormData（multipart/form-data）
示例：

```
POST /address/add
Content-Type: multipart/form-data

userId=1
name=namename
phone=12312341234
fullAddress=string
current=false
```
返回：
```
{
  "status": "success"
}
```
或
```
{
  "status": "fail"
}
```

9 用户编辑个人资料
方式： post
地址： `${BASE_URL}/personal/edit`
格式： FormData（multipart/form-data）
示例：
```
POST /personal/edit
Content-Type: multipart/form-data

id=1
name=namename（可选）
phone=12312341234（可选）
image=url（可选）
```
返回：
```
{
  "status": "success"
}
```
或
```
{
  "status": "fail"
}
```

10 用户删除地址信息
方式： delete
地址： `${BASE_URL}/address/delete`
示例：
```
DELETE /address/delete
Content-Type: application/json

{
  "id": "123"
}
```
返回：
```
{
  "success": true
}
```
或
```
{
  "success": false
}
```

11 用户更改当前地址信息
方式： post
地址： `${BASE_URL}/address/current`
示例：
```
POST /address/current
Content-Type: application/json

{
  "id": "123"
}
```
返回：
```
{
  "success": true
}
```
或
```
{
  "success": false
}
```

12 用户获取首页推荐
方式： get
地址： `${BASE_URL}/user?userId=${userId}`
参数： userId
示例： `GET /user?userId=xxx`
格式：
```
[
    shop: {
        id: '1',
        name: '美味鲜果店',
        image: 'https://via.placeholder.com/60',
        address: '朝阳区建国路88号'
    },
    ...
]
```