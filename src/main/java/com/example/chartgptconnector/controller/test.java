package com.example.chartgptconnector.controller;

import cn.hutool.json.JSONUtil;
import com.example.chartgptconnector.utile.GPTUtiles;
import lombok.extern.slf4j.Slf4j;
import org.apache.juli.logging.Log;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class test {
    @ResponseBody
    @RequestMapping("test")
    public String test1(@RequestBody String token){
        log.info("开始执行test方法！！！");
        Boolean authCheck = null;
        try {
            authCheck = GPTUtiles.authCheck(JSONUtil.parse(token).getByPath("token").toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("["+JSONUtil.parse(token).getByPath("token")+"]校验结果:"+authCheck);
        return "hello world!";
    }

}
