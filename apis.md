# 用到的api
## 用户注册接口

- **请求方式**：GET
- **请求地址**：`${BASE_URL}/register`
- **请求参数**（Query String）：

| 参数名      | 类型   | 说明         | 是否必填 |
| ----------- | ------ | ------------ | -------- |
| userName    | string | 用户名       | 是       |
| userEmail   | string | 用户邮箱     | 是       |
| userKind    | string | 用户类型     | 是       |
| password    | string | 用户密码     | 是       |

- **请求示例**：

```
GET http://localhost:3000/register?userName=张三&userEmail=zhangsan@example.com&userKind=normal&password=123456
```

- **返回结果**（JSON）：

成功：
```json
{
  "code": 200,
  "success": true,
  "message": "注册成功"
}
```
失败：
```json
{
  "code": 400,
  "success": false,
  "message": "邮箱已被注册"
}
```

---

## 用户登录接口

- **请求方式**：GET
- **请求地址**：`${BASE_URL}/login`
- **请求参数**（Query String）：

| 参数名      | 类型   | 说明         | 是否必填 |
| ----------- | ------ | ------------ | -------- |
| userName    | string | 用户名       | 否，但是会有一个默认值，可能为空|
| userEmail   | string | 用户邮箱     | 是        |
| password    | string | 用户密码     | 是       |
| userKind    | string | 用户类型     | 是       |

- **请求示例**：

```
GET http://localhost:3000/login?userName=张三&password=123456
```

- **返回结果**（JSON）：

成功：
```json
{
  "code": 200,
  "success": true,
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