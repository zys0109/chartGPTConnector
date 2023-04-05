package com.example.chartgptconnector.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.chartgptconnector.entity.GPTRequest;
import com.example.chartgptconnector.service.ChartGPTservice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.Resources;

@Slf4j
@RestController
@RequestMapping("/chartGPT")
public class ChartGPTController {
    @Resource
    private ChartGPTservice chartGPTservice;

    @PostMapping("/askAi")
    public JSONObject askAi(@RequestBody GPTRequest gptRequest) {
        log.info("askAi接口请求参数:" + gptRequest.getAskStr());
        String replyStr = chartGPTservice.send(gptRequest.getAskStr());
        gptRequest.setReplyStr(replyStr);
        return new JSONObject().put("replyStr",replyStr);

    }
}
