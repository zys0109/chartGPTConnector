package com.example.chartgptconnector.controller;

import cn.hutool.json.JSONUtil;
import com.example.chartgptconnector.service.ChartGPTservice;
import com.example.chartgptconnector.service.impl.ChartGPTserviceImpl;
import com.example.chartgptconnector.utile.GPTUtiles;
import lombok.extern.slf4j.Slf4j;
import org.apache.juli.logging.Log;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Slf4j
@Controller
public class test {

    @Resource
    private ChartGPTservice chartGPTservice;
    @ResponseBody
    @RequestMapping("test")
    public String test1(){
        log.info("开始执行test方法！！！");
        /**Boolean authCheck = null;
        try {
            authCheck = GPTUtiles.authCheck(JSONUtil.parse(token).getByPath("token").toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("["+JSONUtil.parse(token).getByPath("token")+"]校验结果:"+authCheck);*/

        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2ODU0MTQwMjN9.bZH0BX8byt5cMGHnOP429zO0C-G3o7BRmXbLKu9k63A";
        chartGPTservice.askAiPictureSearch("test",token);
        return "hello world!";
    }

}
