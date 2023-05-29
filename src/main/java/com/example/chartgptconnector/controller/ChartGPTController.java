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

    @GetMapping ("/askAiContextStreamSSE")
    public SseEmitter askAiContextStreamSSE(String askStr){
        log.info("askAiContextStream接口请求参数:" + askStr);
        return chartGPTservice.askAiContextStreamSSE(askStr);
    }

    @GetMapping ("/askAiContextStream")
    public void askAiContextStream(String askStr,String token,HttpServletResponse response){
        log.info("askAiContextStream接口请求参数:" + askStr);
       /** response.setHeader("Access-Control-Allow-Credentials","true");
        response.setHeader("Transfer-Encoding","chunked");
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Access-Control-Allow-Origin","*");
        response.setHeader("Access-Control-Allow-Headers","Content-Type");
        response.setHeader("Connection","keep-alive");
        */

        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        // 禁用缓存
        response.setHeader("Cache-Control", "no-cache");
        try {
            chartGPTservice.askAiContextStream(askStr,token,response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/askAiPictureSearch")
    public JSONObject askAiPictureSearch(@RequestBody GPTRequest gptRequest) {
        log.info("askAiPictureSearch接口请求参数:" + gptRequest.getAskStr());
        String replyStr = chartGPTservice.askAiPictureSearch(gptRequest.getAskStr(),gptRequest.getToken());
        gptRequest.setReplyStr(replyStr);
        return new JSONObject().put("replyStr",replyStr);
    }
}
