package com.example.chartgptconnector.utile;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.chartgptconnector.entity.ApiKey;
import com.example.chartgptconnector.mapper.ApiKeyMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GPTUtiles {
    private static int id = 1;

    private static ApiKey apiKey;

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
}
