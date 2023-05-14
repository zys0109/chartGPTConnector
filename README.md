# chartGPTConnector
该工程是使用springBoot构建的一个调用openAI接口的工程
作者使用的模型是：gpt-3.5-turbo
其他模型调用可参考openAI官方文档：https://platform.openai.com/docs/api-reference/authentication

# 1. GPT接口



## 1.1 接口说明

接口url：/dajurenGPT/askAi

请求方式: post

附加说明：不支持上下文关联

请求参数:

| 名称   | 类型   | 描述                   |
| ------ | ------ | ---------------------- |
| askStr | String | 需要传送给AI的对话文本 |

返回数据:

~~~json
https://localhost:8080/chartGPT/askAi

{"replyStr":"This is a test response from an AI language model. Is there anything specific you would like me to do or answer?"}
~~~





## 1.2 接口说明

接口url：/dajurenGPT/askAiStream

请求方式: get

附加说明：不支持上下文关联的流式返回前端

请求参数:

| 名称   | 类型   | 描述                   |
| ------ | ------ | ---------------------- |
| askStr | String | 需要传送给AI的对话文本 |

返回数据:

~~~json
https://localhost:8080/dajurenGPT/askAiStream?askStr=问题文本

以流的方式返回前端，每秒输出25个字符。
~~~



## 1.3 接口说明

接口url：/dajurenGPT/askAiContext

请求方式: post

附加说明：支持上下文关联，支持十轮对话内的上下文关联，超过十轮对话则重置。

请求参数:

| 名称   | 类型   | 描述                             |
| ------ | ------ | -------------------------------- |
| askStr | String | 需要传送给AI的对话文本           |
| openId | String | 当前用户的openId，用以上下文关联 |

返回数据:

~~~json
https://localhost:8080/dajurenGPT/askAiContext

{"replyStr": "我不确定您的问题是什么，请问您需要我回答什么问题吗？"}
~~~



## 1.4 接口说明

接口url：/dajurenGPT/askAiContextStream

请求方式: get

附加说明：支持上下文关联流式返回前端，支持十轮对话内的上下文关联，超过十轮对话则重置。

请求参数:

| 名称   | 类型   | 描述                             |
| ------ | ------ | -------------------------------- |
| askStr | String | 需要传送给AI的对话文本           |
| openId | String | 当前用户的openId，用以上下文关联 |

返回数据:

~~~json
https://localhost:8080/dajurenGPT/askAiContextStream?askStr=我是谁&openId=oiSYe5aJJqRXOPNXzAbe_f_7lMlk

以流的方式返回前端，每秒输出25个字符。
~~~

