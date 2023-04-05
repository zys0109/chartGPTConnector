package com.example.chartgptconnector.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.juli.logging.Log;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class test {
    @ResponseBody
    @RequestMapping("test")
    public String test1(){
        log.info("开始执行test方法！！！");
        return "hello world!";
    }

}
