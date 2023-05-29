package com.example.chartgptconnector.utile;

import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.chartgptconnector.entity.ApiKey;
import com.example.chartgptconnector.mapper.ApiKeyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class GPTUtiles {
    private static int id = 1;
    private static ApiKey apiKey;

    private static String userInfoUrl;

    private static String filterString;

    //apiKey个数
    private static int apiKeyCount;


    private static  String contentType;

    @Value("${ChatGPT.variables.contentType}")
    public void setContentType(String contentTypeTemp) {
        contentType = contentTypeTemp;
    }

    @Value("${WeChartLoginTool.userinfoUrl}")
    public void setUserInfoUrl(String url) {
        userInfoUrl = url;
    }

    @Value("${ChatGPT.Stringfilter}")
    public void setFilterString(String filterStringTemp) {
        filterString = filterStringTemp;
    }

    @Value("${ChatGPT.apiKeyCount}")
    public void setApiKeyCount(int apiKeyCountTemp) {
        apiKeyCount = apiKeyCountTemp;
    }

    /**
     * 轮询获取apiKey
     *
     * @return api_key
     */
    public synchronized static String getApiKey(ApiKeyMapper apiKeyMapper) throws Exception {
        while (id <= apiKeyCount){
            log.info("当前apiKey的id:" + id);
            apiKey = apiKeyMapper.selectById(id);
            log.info("当前apiKey:" + apiKey.toString());
            if (apiKey.isEffective()){
                id = (id % apiKeyCount) + 1;
                break;
            }
            id = (id % apiKeyCount) + 1;
        }
        return apiKey.getApiKey().toString();
    }

    /**
     * 判断当前apiKey是否有效
     */
    public synchronized static void apikeyIsEffective(String api_key, ApiKeyMapper apiKeyMapper, int status, JSONObject jsonObject) throws Exception {
        log.info("当前请求状态码:" + status);
        if (status == 200){
            log.info("chartGPT响应成功["+api_key+"]状态不用更新");
        }else if (status == 429 && "You exceeded your current quota, please check your plan and billing details.".equals(jsonObject.getJSONObject("error").getStr("message")) ) {
            apiKey.setEffective(false);
            apiKeyMapper.update(apiKey, new UpdateWrapper<ApiKey>().eq("api_key", api_key));
            log.info("chartGPT响应失败[" + api_key + "]状态更新完成");
        }
    }

    /**
     * 校验当前接口调用是否合法
     *
     * @param token
     * @return
     */
    public static Boolean authCheck(String token) throws Exception{
        log.info("userInfoUrl:" + userInfoUrl + "?refresh=false");
        HashMap<String, String> map = new HashMap<>();
        map.put("refresh", "false");
        //HttpResponse response = HttpUtil.createGet(userInfoUrl).header(Header.AUTHORIZATION, "Bearer " + token).form(map).execute();
        JSONObject responseBody = responseBody = GPTUtiles.httpToolGET(userInfoUrl, token, "?refresh=false");
        log.info("响应报文体" + responseBody);
        if ("0".equals(responseBody.getByPath("code").toString())) {
            log.info("被鉴权token为[" + token + "],鉴权结果:成功");
            return true;
        } else {
            log.info("被鉴权token为[" + token + "],鉴权结果:失败");
            return false;
        }
    }

    /**
     * 过滤替换响应关键字
     * @param response
     * @return
     */
    public static String stringfilter(String response) {
        ArrayList<String> filterStringArray = new ArrayList();
        String filterStringTemp = "";
        int filterStringCount = 0;
        int forTemp = 0;
        char[] charArray = filterString.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == '|') {
                for (int j = forTemp; j < i; j++) {
                    filterStringTemp = filterStringTemp + charArray[j];
                }
                filterStringCount++;
                forTemp = i + 1;
                filterStringArray.add(filterStringTemp);
                filterStringTemp = "";
            }
        }
        log.info("需要被过滤的字符串数组:"+filterStringArray.toString());
        for (String str : filterStringArray) {
            if (response.contains(str)){
                response = response.replace(str,"BigMan");
            }
        }
        return response;
    }

    public static JSONObject httpToolGET(String urlString, String token,String parameter) throws Exception{
        log.info("开始调用httpToolGET！！！");
        HttpURLConnection connection = null;
        URL url = new URL(urlString+parameter);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization","Bearer " + token);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        InputStream inputStream = connection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String content = "";
        while ((line = bufferedReader.readLine()) != null){
            if (StringUtils.hasLength(line)){
                    content = content+line;
                }
            }
        log.info("content:"+content);
        return new JSONObject(content);
    }

    public static HttpURLConnection httpToolPOST(String urlString, String api_key,JSONObject requstBodyJson) throws Exception{
        log.info("开始调用httpToolPOST！！！");
        HttpURLConnection connection = null;
        URL url = new URL(urlString);
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
        return connection;
    }
}
