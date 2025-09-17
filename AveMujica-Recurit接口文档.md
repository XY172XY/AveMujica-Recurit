# UserController

## PUT changePassword

PUT /api/me/password

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
|userId|query|integer| 否 |none|
|body|body|[ChangePasswordVO](#schemachangepasswordvo)| 否 |none|

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

## PUT modifyEmail

PUT /api/me/email

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
|id|query|integer| 否 |none|
|body|body|[ModifyEmailVO](#schemamodifyemailvo)| 否 |none|

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

POST /api/auth/verify-code

请求邮件验证码

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|email|query|string| 是 |none|
|type|query|string| 是 |none|

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

## PUT 密码重置操作

PUT /api/auth/account/password

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
|body|body|[EmailResetVO](#schemaemailresetvo)| 否 |none|

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

POST /api/auth/account

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
|body|body|[RegisterAccountVO](#schemaregisteraccountvo)| 否 |none|

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

## POST submitFlag

POST /api/submits/flag

> Body 请求参数

```json
{
  "id": 0,
  "userId": 0,
  "questionId": 0,
  "originScore": 0,
  "flag": "string",
  "options": {
    "key": "string"
  }
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[QuestionSubmitVO](#schemaquestionsubmitvo)| 否 |none|

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

## POST submitChoice

POST /api/submits/choice

> Body 请求参数

```json
{
  "id": 0,
  "userId": 0,
  "questionId": 0,
  "originScore": 0,
  "flag": "string",
  "options": {
    "key": "string"
  }
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[QuestionSubmitVO](#schemaquestionsubmitvo)| 否 |none|

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

## POST submitMaterial

POST /api/submits/material

> Body 请求参数

```yaml
id: 0
userId: 0
questionId: 0
originScore: 0
flag: string
options.key: null
file: string

```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|object| 否 |none|
|» id|body|integer| 否 |none|
|» userId|body|integer| 否 |none|
|» questionId|body|integer| 否 |none|
|» originScore|body|integer| 否 |none|
|» flag|body|string| 否 |none|
|» options.key|body|any| 否 |none|
|» file|body|string(binary)| 否 |none|

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

## GET getSubmitRecord

GET /api/submits

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

## GET getSubmit

GET /api/admin/questions/{questionId}/submits

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|questionId|path|integer| 是 |none|
|page|query|integer| 是 |none|
|size|query|integer| 是 |none|
|status|query|string| 是 |none|

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

## GET downLoad

GET /api/admin/files/{fileName}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|fileName|path|string| 是 |none|

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

## PUT correct

PUT /api/admin/submits/{submitId}/score

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|submitId|path|integer| 是 |none|
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

## POST 请完整上传题目信息

POST /api/admin/questions

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
|body|body|[QuestionAddVO](#schemaquestionaddvo)| 否 |none|

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

## PUT 题目更新接口,请上传完整题目信息

PUT /api/admin/questions/{id}

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
|id|path|integer| 是 |none|
|X-Nonce|header|string| 是 |none|
|X-Timestamp|header|string| 是 |none|
|body|body|[QuestionUpdateVO](#schemaquestionupdatevo)| 否 |none|

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

## DELETE 删除题目

DELETE /api/admin/questions/{id}

删除题目

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|
|X-Nonce|header|string| 是 |none|
|X-Timestamp|header|string| 是 |none|

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

## GET selectQuestionByDirectionAndTurn

GET /api/questions

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|direction|query|string| 否 |none|
|turn|query|integer| 否 |none|
|page|query|integer| 是 |none|
|X-Nonce|header|string| 是 |none|
|X-Timestamp|header|string| 是 |none|

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

## GET selectQuestionDetail

GET /api/questions/{id}/detail

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|
|X-Nonce|header|string| 是 |none|
|X-Timestamp|header|string| 是 |none|

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

## GET error

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

## GET getChart

GET /api/charts

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|direction|query|string| 否 |none|
|page|query|integer| 是 |none|
|size|query|integer| 是 |none|
|X-Nonce|header|string| 是 |none|
|X-Timestamp|header|string| 是 |none|

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
      "directionScores": {
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

