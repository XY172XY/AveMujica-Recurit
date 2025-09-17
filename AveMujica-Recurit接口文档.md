# Authentication



# UserController

## POST 修改密码

POST AveMujica/api/user/change-password

> Body 请求参数

```json
{
  "password": "string",
  "new_password": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|userId|query|integer| 否 |不需要提供|
|body|body|[ChangePasswordVO](#schemachangepasswordvo)| 是 |旧密码和新密码|

> 返回示例

> 200 Response

```json
{
  "id": 0,
  "code": 0,
  "data": null,
  "message": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[RestBeanVoid](#schemarestbeanvoid)|

## POST 重新绑定邮箱

POST AveMujica//api/user/modify-email

> Body 请求参数

```json
{
  "email": "user@example.com",
  "code": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|query|integer| 否 |不需要提供|
|body|body|[ModifyEmailVO](#schemamodifyemailvo)| 否 |邮箱和验证码|

> 返回示例

> 200 Response

```json
{
  "id": 0,
  "code": 0,
  "data": null,
  "message": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[RestBeanVoid](#schemarestbeanvoid)|

# AuthorizeController

## POST 请求邮件验证码

POST AveMujica//api/auth/ask-code

请求邮件验证码，type为目的，注册，修改密码，重绑邮箱

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|email|query|string| 是 |none|
|type|query|string| 是 |"register" \| "modify" \| "reset"|

> 返回示例

> 200 Response

```json
{
  "id": 0,
  "code": 0,
  "data": null,
  "message": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[RestBeanVoid](#schemarestbeanvoid)|

## POST 密码重置操作

POST AveMujica//api/auth/reset-password

密码重置操作

> Body 请求参数

```json
{
  "email": "user@example.com",
  "code": "string",
  "password": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[EmailResetVO](#schemaemailresetvo)| 是 |邮箱，密码，验证码|

> 返回示例

> 200 Response

```json
{
  "id": 0,
  "code": 0,
  "data": null,
  "message": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[RestBeanVoid](#schemarestbeanvoid)|

## POST 注册用户

POST AveMujica/api/auth/register

注册用户

> Body 请求参数

```json
{
  "username": "string",
  "password": "string",
  "email": "user@example.com",
  "code": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[RegisterAccountVO](#schemaregisteraccountvo)| 是 |用户名，密码，邮箱，邮箱验证码|

> 返回示例

> 200 Response

```json
{
  "id": 0,
  "code": 0,
  "data": null,
  "message": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[RestBeanVoid](#schemarestbeanvoid)|

# SubmitController

## POST 提交题目

POST AveMujica/api/submit

> Body 请求参数

```yaml
{
"id": 0,
"userId": 0,
"questionId": 0,
"originScore": 0,
"flag": "",
"options": 
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|type|query|string| 是 |题目类型 "flag" \| "choice" \| "material"|
|body|body|object| 是 |none|
|» file|body|string(binary)| 否 |提交的附件|

> 返回示例

> 200 Response

```json
{
  "id": 0,
  "code": 0,
  "data": "",
  "message": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[RestBeanString](#schemarestbeanstring)|

## POST 获取提交记录

POST /api/submit/get

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|userId|query|integer| 是 |none|
|questionId|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "id": 0,
  "code": 0,
  "data": {
    "timeRecord": "",
    "score": 0,
    "count": 0
  },
  "message": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[RestBeanSubmitRecord](#schemarestbeansubmitrecord)|

# CorrectController

## POST 获取已经批改的提交

POST /api/admin/get-corrected-submit

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|question_id|query|integer| 是 |none|
|page|query|integer| 是 |none|
|size|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "id": 0,
  "code": 0,
  "data": [
    {
      "id": 0,
      "userId": 0,
      "questionId": 0,
      "timeRecord": "",
      "score": 0,
      "count": 0,
      "fileName": ""
    }
  ],
  "message": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[RestBeanListQuestionSubmit](#schemarestbeanlistquestionsubmit)|

## POST 获取未批改的提交

POST /api/admin/get-uncorrected-submit

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|question_id|query|integer| 是 |none|
|page|query|integer| 是 |none|
|size|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "id": 0,
  "code": 0,
  "data": [
    {
      "id": 0,
      "userId": 0,
      "questionId": 0,
      "timeRecord": "",
      "score": 0,
      "count": 0,
      "fileName": ""
    }
  ],
  "message": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[RestBeanListQuestionSubmit](#schemarestbeanlistquestionsubmit)|

## POST 获取附件下载链接

POST AveMujica/api/admin/down-load

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|fileName|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "id": 0,
  "code": 0,
  "data": "",
  "message": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[RestBeanString](#schemarestbeanstring)|

## POST 批改提交

POST /api/admin/correct

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|submitId|query|integer| 是 |none|
|score|query|integer| 是 |none|
|questionId|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "id": 0,
  "code": 0,
  "data": "",
  "message": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[RestBeanString](#schemarestbeanstring)|

# QuestionManageController

## POST 上传题目

POST AveMujica/api/question/admin/add

请完整上传题目信息

> Body 请求参数

```json
{
  "type": "string",
  "title": {
    "key": {}
  },
  "content": {
    "key": {}
  },
  "originScore": 0,
  "deadline": "string",
  "difficulty": "string",
  "questionOrder": 0,
  "turn": 0,
  "direction": "string",
  "answer": {
    "id": 0,
    "questionId": 0,
    "flagAnswer": "string",
    "choiceAnswer": {
      "key": "string"
    }
  }
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|nonce|query|string| 是 |none|
|timestamp|query|integer| 是 |none|
|body|body|[QuestionAddVO](#schemaquestionaddvo)| 否 |title,content是HashMap|

> 返回示例

> 200 Response

```json
{
  "id": 0,
  "code": 0,
  "data": "",
  "message": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[RestBeanString](#schemarestbeanstring)|

## POST 题目更新

POST /api/question/admin/update

题目更新接口,请上传完整题目信息

> Body 请求参数

```json
{
  "id": 0,
  "type": "string",
  "title": {
    "key": {}
  },
  "content": {
    "key": {}
  },
  "originScore": 0,
  "deadline": "string",
  "difficulty": "string",
  "questionOrder": 0,
  "turn": 0,
  "direction": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|nonce|query|string| 是 |none|
|timestamp|query|integer| 是 |none|
|body|body|[QuestionUpdateVO](#schemaquestionupdatevo)| 是 |title,content是HashMap|

> 返回示例

> 200 Response

```json
{
  "id": 0,
  "code": 0,
  "data": "",
  "message": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[RestBeanString](#schemarestbeanstring)|

## POST 删除题目

POST /api/question/admin/delete

删除题目

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|query|integer| 是 |none|
|nonce|query|string| 是 |防重放随机数|
|timestamp|query|integer| 是 |请求时间戳|

> 返回示例

> 200 Response

```json
{
  "id": 0,
  "code": 0,
  "data": "",
  "message": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[RestBeanString](#schemarestbeanstring)|

## POST 获取题目列表

POST /api/question/select-direction-turn

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|direction|query|string| 否 |方向|
|turn|query|integer| 否 |轮次|
|page|query|integer| 是 |页数，默认size = 10|
|nonce|query|string| 是 |防重放随机数|
|timestamp|query|integer| 是 |请求时间戳|

> 返回示例

> 200 Response

```json
{
  "id": 0,
  "code": 0,
  "data": [
    {
      "id": 0,
      "type": "",
      "title": {
        "": {}
      },
      "content": {
        "": {}
      },
      "originScore": 0,
      "currentScore": 0,
      "firstBlood": "",
      "deadline": "",
      "difficulty": "",
      "questionOrder": 0,
      "turn": 0,
      "direction": ""
    }
  ],
  "message": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[RestBeanListQuestion](#schemarestbeanlistquestion)|

## POST 获取详细题目内容

POST /api/question/select-detail

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|query|integer| 是 |题目id|
|nonce|query|string| 是 |防重放时间戳|
|timestamp|query|integer| 是 |请求时间戳|

> 返回示例

> 200 Response

```json
{
  "id": 0,
  "code": 0,
  "data": {
    "id": 0,
    "type": "",
    "title": {
      "": {}
    },
    "content": {
      "": {}
    },
    "originScore": 0,
    "currentScore": 0,
    "deadline": ""
  },
  "message": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[RestBeanQuestionDetail](#schemarestbeanquestiondetail)|

# ErrorPageController

## GET 错误处理

GET /${server.error.path:${error.path:/error}}

> 返回示例

> 200 Response

```json
{
  "id": 0,
  "code": 0,
  "data": null,
  "message": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[RestBeanVoid](#schemarestbeanvoid)|

# ChartController

## POST 获取排行榜

POST /api/chart/get

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|direction|query|string| 否 |方向|
|page|query|integer| 是 |页数|
|size|query|integer| 是 |每页数据量|
|nonce|query|string| 是 |防重放随机数|
|timestamp|query|integer| 是 |请求时间戳|

> 返回示例

> 200 Response

```json
{
  "id": 0,
  "code": 0,
  "data": [
    {
      "id": 0,
      "userId": 0,
      "username": "",
      "scores": {
        "": 0
      },
      "producedScores": {
        "": 0
      },
      "totalScore": 0
    }
  ],
  "message": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[RestBeanListScore](#schemarestbeanlistscore)|

