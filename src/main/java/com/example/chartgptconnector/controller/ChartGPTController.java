package com.example.chartgptconnector.controller;

import cn.hutool.json.JSONObject;
import com.example.chartgptconnector.entity.GPTRequest;
import com.example.chartgptconnector.service.ChartGPTservice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/dajurenGPT")
public class ChartGPTController {
    @Resource
    private ChartGPTservice chartGPTservice;

    @PostMapping("/askAi")
    public JSONObject askAi(@RequestBody GPTRequest gptRequest) {
        log.info("askAi接口请求参数:" + gptRequest.getAskStr());
        String replyStr = chartGPTservice.askAi(gptRequest.getAskStr());
        gptRequest.setReplyStr(replyStr);
        return new JSONObject().put("replyStr",replyStr);

    }

    @GetMapping ("/askAiStream")
    public void askAiStream(String askStr, HttpServletResponse response){
        log.info("askAiStream接口请求参数:" + askStr);
        response.setContentType("text/event-stream");
        // 禁用缓存
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        try {
            chartGPTservice.askAiStream(askStr, response.getOutputStream());
        } catch (IOException e) {
            log.info("askAiStream接口调用异常:"+e.toString());
        }
    }

    @PostMapping("/askAiContext")
    public JSONObject askAiContext(@RequestBody GPTRequest gptRequest) {
        log.info("askAiContext接口请求参数:" + gptRequest.getAskStr());
        String replyStr = chartGPTservice.askAiContext(gptRequest.getAskStr(),gptRequest.getOpenId());
        gptRequest.setReplyStr(replyStr);
        return new JSONObject().put("replyStr",replyStr);
    }

    @GetMapping ("/askAiContextStream")
    public void askAiContextStream(String askStr,String openId, HttpServletResponse response){
        log.info("askAiContextStream接口请求参数:" + askStr);
        response.setContentType("text/event-stream");
        // 禁用缓存
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        try {
            chartGPTservice.askAiContextStream(askStr, openId,response.getOutputStream());
        } catch (IOException e) {
            log.info("askAiContextStream接口调用异常:"+e.toString());
        }
    }
}
