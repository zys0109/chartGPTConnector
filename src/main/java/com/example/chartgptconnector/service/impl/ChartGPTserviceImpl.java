package com.example.chartgptconnector.service.impl;

import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.chartgptconnector.entity.GPTResponse;
import com.example.chartgptconnector.mapper.ApiKeyMapper;
import com.example.chartgptconnector.service.ChartGPTservice;
import com.example.chartgptconnector.utile.GPTUtiles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class ChartGPTserviceImpl implements ChartGPTservice {

    private String api_key;
    @Value("${ChatGPT.variables.model}")
    private String model;
    @Value("${ChatGPT.variables.temperature}")
    private Double temperature;
    @Value("${ChatGPT.variables.role}")
    private String role;
    @Value("${ChatGPT.chartGPTUrl}")
    private String openAiUrl;
    @Value("${ChatGPT.variables.contentType}")
    private String contentType;
    @Value("${ChatGPT.variables.contextLength}")
    private int contextLength;
    @Resource
    private ApiKeyMapper apiKeyMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 不支持上下文关联
     *
     * @param prompt
     * @return
     */
    @Override
    public String askAi(String prompt) {
        String askAiResponse = new String();
        log.info("开始执行askAi方法！！！");
        JSONObject requstBodyJson = new JSONObject();
        requstBodyJson.put("model", model);
        JSONObject messageJson = new JSONObject();
        messageJson.put("role", role);
        messageJson.put("content", prompt);
        JSONArray messageList = new JSONArray();
        messageList.add(messageJson);
        requstBodyJson.put("messages", messageList);
        requstBodyJson.put("temperature", temperature);
        log.info("请求地址:" + openAiUrl);
        log.info("请求报文体:" + JSONUtil.toJsonStr(requstBodyJson));
        try {
            api_key = GPTUtiles.getApiKey(apiKeyMapper);
            log.info("当前api_key:" + api_key);
            HttpResponse httpResponse = HttpUtil.createPost(openAiUrl).header(Header.AUTHORIZATION, "Bearer " + api_key).header("Content-Type", contentType).body(JSONUtil.toJsonStr(requstBodyJson)).execute();
            log.info("httpResponse:" + httpResponse);
            String resStr = httpResponse.body();
            log.info("响应报文体:" + resStr);
            GPTResponse gptResponse = JSONUtil.toBean(resStr, GPTResponse.class);
            GPTUtiles.apikeyIsEffective(api_key, apiKeyMapper, httpResponse.getStatus());
            log.info("本地封装接口返回值:" + gptResponse.getChoices().get(0).getMessage().getContent());
            askAiResponse = gptResponse.getChoices().get(0).getMessage().getContent();
        } catch (Exception exception) {
            log.info("调用openAI接口异常:" + exception.toString());
            askAiResponse = "调用接口发生异常，请联系管理员！！！";
        } finally {
            return askAiResponse;
        }
    }

    /**
     * 不支持上下文关联的流式返回前端
     *
     * @param prompt
     * @param outputStream
     */
    @Override
    public void askAiStream(String prompt, OutputStream outputStream) {
        log.info("开始执行askAiStream方法！！！");
        try {
            String anser = askAi(prompt);
            char[] srt = anser.toCharArray();
            log.info("开始流式输出文本！！！");
            for (int i = 0; i < srt.length; i++) {
                char temp = srt[i];
                outputStream.write(String.valueOf(temp).getBytes(Charset.defaultCharset()));
                outputStream.flush();
                Thread.sleep(40);
            }
            outputStream.close();
            log.info("流式文本输出完成！！！");
        } catch (Exception e) {
            log.info("接口调用失败联系管理员");
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 支持上下文关联
     *
     * @param prompt
     * @param token
     * @return
     */
    public String askAiContext(String prompt, String token) {
        String askAiResponse = new String();
        try {
            if (!GPTUtiles.authCheck(token)) {
                askAiResponse = "核心接口鉴权失败，请重新登录。";
            } else {
                log.info("开始执行askAiContext方法！！！");
                JSONObject requstBodyJson = new JSONObject();
                requstBodyJson.put("model", model);
                JSONObject messageJson = new JSONObject();
                messageJson.put("role", role);
                messageJson.put("content", prompt);
                JSONArray messageList = new JSONArray();
                String messageListTempStr = stringRedisTemplate.opsForValue().get(token);
                if (messageListTempStr != null && !"".equals(messageListTempStr)) {
                    JSONArray messageListTemp = new JSONArray(messageListTempStr);
                    log.info("messageListTemp:" + messageListTemp);
                    log.info("当前上下文长度:" + messageListTemp.size());
                    if (messageListTemp.size() >= contextLength) {
                        messageListTemp.clear();
                    } else {
                        for (int i = 0; i < messageListTemp.size(); i++) {
                            messageList.put(messageListTemp.get(i));
                        }
                        log.info("未插入新message的messageList" + messageList);
                    }
                }
                messageList.add(messageJson);
                log.info("插入新message的messageList" + messageList);
                requstBodyJson.put("messages", messageList);
                requstBodyJson.put("temperature", temperature);
                log.info("请求地址:" + openAiUrl);
                log.info("请求报文体:" + JSONUtil.toJsonStr(requstBodyJson));
                api_key = GPTUtiles.getApiKey(apiKeyMapper);
                log.info("当前api_key:" + api_key);
                HttpResponse httpResponse = HttpUtil.createPost(openAiUrl).header(Header.AUTHORIZATION, "Bearer " + api_key).header("Content-Type", contentType).body(JSONUtil.toJsonStr(requstBodyJson)).execute();
                log.info("httpResponse:" + httpResponse);
                String resStr = httpResponse.body();
                log.info("响应报文体:" + resStr);
                GPTResponse gptResponse = JSONUtil.toBean(resStr, GPTResponse.class);
                GPTUtiles.apikeyIsEffective(api_key, apiKeyMapper, httpResponse.getStatus());
                log.info("本地封装接口返回值:" + gptResponse.getChoices().get(0).getMessage().getContent());
                messageList.add(gptResponse.getChoices().get(0).getMessage());
                stringRedisTemplate.opsForValue().set(token, messageList.toString(), 1, TimeUnit.DAYS);
                askAiResponse = gptResponse.getChoices().get(0).getMessage().getContent();
            }
        } catch (Exception exception) {
            log.info("调用openAI接口异常:" + exception.toString());
            askAiResponse = "调用接口发生异常，请联系管理员！！！";
        } finally {
            return askAiResponse;
        }
    }

    /**
     * 支持上下文关联流式返回前端
     *
     * @param prompt
     * @param token
     * @param outputStream
     */
    @Override
    public void askAiContextStream(String prompt, String token, OutputStream outputStream) {
        log.info("开始执行askAiContextStream方法！！！");
        try {
            String anser = askAiContext(prompt, token);
            char[] srt = anser.toCharArray();
            log.info("开始流式输出文本！！！");
            for (int i = 0; i < srt.length; i++) {
                char temp = srt[i];
                outputStream.write(String.valueOf(temp).getBytes(Charset.defaultCharset()));
                outputStream.flush();
                Thread.sleep(40);
            }
            outputStream.close();
            log.info("流式文本输出完成！！！");
        } catch (Exception e) {
            log.info("接口调用失败联系管理员");
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
