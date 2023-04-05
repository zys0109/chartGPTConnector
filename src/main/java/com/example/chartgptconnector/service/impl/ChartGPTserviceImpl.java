package com.example.chartgptconnector.service.impl;

import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.chartgptconnector.entity.GPTResponse;
import com.example.chartgptconnector.service.ChartGPTservice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ChartGPTserviceImpl implements ChartGPTservice {

    @Value("${ChatGPT.variables.apiKey}")
    private String apiKey;
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
    @Override
    public String send(String prompt) {
        String askAiResponse =new String();
        log.info("开始执行send方法！！！");
        JSONObject requstBodyJson = new JSONObject();
        requstBodyJson.put("model",model);
        JSONObject messageJson = new JSONObject();
        messageJson.put("role",role);
        messageJson.put("content",prompt);
        JSONArray messageList = new JSONArray();
        messageList.add(messageJson);
        requstBodyJson.put("messages",messageList);
        requstBodyJson.put("temperature",temperature);
//        Map<String, Object> headMap = new HashMap<>();
//        headMap.put("Authorization", "Bearer " + apiKey);
        log.info("请求地址:"+openAiUrl);
        log.info("请求报文体:"+JSONUtil.toJsonStr(requstBodyJson));
        try {
            HttpResponse httpResponse = HttpUtil.createPost(openAiUrl).header(Header.AUTHORIZATION,"Bearer "+apiKey).header("Content-Type",contentType).body(JSONUtil.toJsonStr(requstBodyJson)).execute();
            //HttpResponse httpResponse = HttpUtil.createPost("https://api.openai.com/v1/engines/" + model + "/completions").header(Header.AUTHORIZATION, "Bearer " + apiKey).body(JSONUtil.toJsonStr(bodyJson)).execute();
            log.info("httpResponse:"+httpResponse);
            String resStr = httpResponse.body();
            log.info("响应报文体:"+resStr);
            GPTResponse gptResponse = JSONUtil.toBean(resStr,GPTResponse.class);
            log.info("本地封装接口返回值:"+gptResponse.getChoices().get(0).getMessage().getContent());
            askAiResponse = gptResponse.getChoices().get(0).getMessage().getContent();
        }catch (Exception exception){
            log.info("调用openAI接口异常:"+exception.getMessage());
            askAiResponse = "调用openAI接口发生异常，请联系管理员！！！";
        }finally {
            return askAiResponse;
        }
    }
}
