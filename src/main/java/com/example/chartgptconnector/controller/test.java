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
        /**Boolean authCheck = null;
        try {
            authCheck = GPTUtiles.authCheck(JSONUtil.parse(token).getByPath("token").toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("["+JSONUtil.parse(token).getByPath("token")+"]校验结果:"+authCheck);*/
        String respones = "ChartGPT是一种基于GPT（Generative Pre-trained Transformer）模型的自然语言生成技术，它可以将用户输入的数据转化为图表形式。ChartGPT的核心是使用深度学习算法来预测和生成图表，它可以自动地从文本数据中提取关键信息，并将其转换为可视化的图表。ChartGPT可以帮助用户更加直观地理解和分析数据，提高数据分析的效率和准确性。";
        log.info("原始响应:"+respones);
        respones = GPTUtiles.stringfilter(respones);
        log.info("过滤后响应:"+respones);
        return "hello world!";
    }

}
