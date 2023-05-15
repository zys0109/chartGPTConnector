package com.example.chartgptconnector.utile;

import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.chartgptconnector.entity.ApiKey;
import com.example.chartgptconnector.mapper.ApiKeyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Slf4j
@Component
public class GPTUtiles {
    private static int id = 1;
    private static ApiKey apiKey;

    private static String userInfoUrl;

    @Value("${WeChartLoginTool.userinfoUrl}")
    public void setReplyAddress(String url) {
        userInfoUrl = url;
    }
    /**
     * 轮询获取apiKey
     * @return api_key
     */
    public synchronized static String getApiKey(ApiKeyMapper apiKeyMapper) throws Exception{
        for (int i = 1;i<=7;i++ ){
            log.info("当前apiKey的id:"+id);
            apiKey = apiKeyMapper.selectById(id);
            log.info("当前apiKey:"+apiKey.toString());
            if (!apiKey.isEffective() && id<=7){
                id++;
            }else {
                id++;
                break;
            }
        }
        if (id==8){
            id=1;
        }
        return apiKey.getApiKey().toString();
    }

    /**
     * 判断当前apiKey是否有效
     */
    public synchronized static void apikeyIsEffective(String api_key,ApiKeyMapper apiKeyMapper,int status) throws Exception{
        log.info("当前请求状态码:"+status);
        if(status == 429){
            apiKey.setEffective(false);
            apiKeyMapper.update(apiKey,new UpdateWrapper<ApiKey>().eq("api_key",api_key));
            log.info("chartGPT响应失败["+api_key+"]状态更新完成");
        }
    }

    /**
     * 校验当前接口调用是否合法
     * @param token
     * @return
     */
    public static Boolean authCheck(String token) throws Exception{
        log.info("userInfoUrl:"+userInfoUrl+"?refresh=false");
        HashMap<String, Object> map = new HashMap<>();
        map.put("refresh","false");
        HttpResponse response = HttpUtil.createGet(userInfoUrl).header(Header.AUTHORIZATION, "Bearer " + token).form(map).execute();
        log.info("响应报文体"+response.body());
        if ("0".equals(JSONUtil.parse(response.body()).getByPath("code").toString())){
            log.info("被鉴权token为["+token+"],鉴权结果:成功");
            return true;
        }else {
            log.info("被鉴权token为["+token+"],鉴权结果:失败");
            return false;
        }
    }
}
