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
import com.example.chartgptconnector.utile.SseEmitterUTF8;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    private SseEmitter emitter;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private String streamContext;




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
            GPTUtiles.apikeyIsEffective(api_key, apiKeyMapper, httpResponse.getStatus(),JSONUtil.parseObj(resStr));
            log.info("本地封装接口过滤关键字前返回值:" + gptResponse.getChoices().get(0).getMessage().getContent());
            askAiResponse = GPTUtiles.stringfilter(gptResponse.getChoices().get(0).getMessage().getContent());
            log.info("本地封装接口过滤关键字后返回值:" + askAiResponse);
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
                GPTUtiles.apikeyIsEffective(api_key, apiKeyMapper, httpResponse.getStatus(),JSONUtil.parseObj(resStr));
                log.info("本地封装接口过滤关键字前返回值:" + gptResponse.getChoices().get(0).getMessage().getContent());
                messageList.add(gptResponse.getChoices().get(0).getMessage());
                stringRedisTemplate.opsForValue().set(token, messageList.toString(), 1, TimeUnit.DAYS);
                askAiResponse = GPTUtiles.stringfilter(gptResponse.getChoices().get(0).getMessage().getContent());
                log.info("本地封装接口过滤关键字后返回值:" + askAiResponse);
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
     */

    public void run() {
        log.info("开始执行askAiContextStream方法！！！");
        log.info("开始执行askAi方法！！！");
        JSONObject requstBodyJson = new JSONObject();
        requstBodyJson.put("model", model);
        JSONObject messageJson = new JSONObject();
        messageJson.put("role", role);
        messageJson.put("content", streamContext);
        JSONArray messageList = new JSONArray();
        messageList.add(messageJson);
        requstBodyJson.put("messages", messageList);
        requstBodyJson.put("temperature", temperature);
        requstBodyJson.put("stream",true);
        log.info("请求地址:" + openAiUrl);
        log.info("请求报文体:" + JSONUtil.toJsonStr(requstBodyJson));

        try {
            api_key = GPTUtiles.getApiKey(apiKeyMapper);
            log.info("当前api_key:" + api_key);
            //HttpResponse httpResponse = HttpUtil.createPost(openAiUrl).header(Header.AUTHORIZATION, "Bearer " + api_key).header("Content-Type", contentType).body(JSONUtil.toJsonStr(requstBodyJson)).execute();
            //log.info("httpResponse:" + httpResponse);
            HttpURLConnection connection = null;
            InputStream is = null;
            BufferedReader br = null;
            StringBuffer result = new StringBuffer();
            URL url = new URL(openAiUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",contentType);
            connection.setRequestProperty("Authorization","Bearer " + api_key);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requstBodyJson.toString().getBytes());
            outputStream.flush();
            outputStream.close();

            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            Pattern contentPattern = Pattern.compile("\"content\":\"(.*?)\"}");
            log.info("开始输出返回值到控制台");
            while ((line = bufferedReader.readLine()) != null){
                if (StringUtils.hasLength(line)){
                    Matcher matcher = contentPattern.matcher(line);
                    if (matcher.find()){
                        String content = matcher.group(1);
                        emitter.send(SseEmitter.event().name("d").data("{"+content+"}"));
                    }
                }
                //Thread.sleep(100);
            }
            log.info("输出返回值到控制台结束");
            emitter.complete();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            emitter.complete();
        }
    }

    /**
     * 对话，异步的，在新的线程的
     */
    public SseEmitter doConverse() {
        executorService.execute(this::run);
        return emitter;
    }

    @Override
    public SseEmitter askAiContextStream(String prompt) {
        this.streamContext = prompt;
        emitter = new SseEmitterUTF8(0L);
        doConverse();
        return emitter;
    }

}
