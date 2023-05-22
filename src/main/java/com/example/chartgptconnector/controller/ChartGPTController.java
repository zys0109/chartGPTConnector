package com.example.chartgptconnector.controller;

import cn.hutool.json.JSONObject;
import com.example.chartgptconnector.entity.GPTRequest;
import com.example.chartgptconnector.service.ChartGPTservice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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

    @PostMapping("/askAiStream")
    public SseEmitter askAiStream(@RequestBody GPTRequest gptRequest){
        log.info("askAiStream接口请求参数:" + gptRequest.getAskStr());
        return null;
    }

    @PostMapping("/askAiContext")
    public JSONObject askAiContext(@RequestBody GPTRequest gptRequest) {
        log.info("askAiContext接口请求参数:" + gptRequest.getAskStr());
        String replyStr = chartGPTservice.askAiContext(gptRequest.getAskStr(),gptRequest.getToken());
        gptRequest.setReplyStr(replyStr);
        return new JSONObject().put("replyStr",replyStr);
    }

    @GetMapping ("/askAiContextStream")
    public SseEmitter askAiContextStream(String askStr){
        log.info("askAiContextStream接口请求参数:" + askStr);
        return chartGPTservice.askAiContextStream(askStr);
    }
}
