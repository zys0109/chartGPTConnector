package com.example.chartgptconnector.entity;

import cn.hutool.json.JSONObject;

//返回choice对象
public class GPTChoice {
    private GPTMessage message;
    private String finishReason;
    private Integer index;

    public GPTMessage getMessage() {
        return message;
    }

    public void setMessage(GPTMessage message) {
        this.message = message;
    }

    public String getFinishReason() {
        return finishReason;
    }

    public void setFinishReason(String finishReason) {
        this.finishReason = finishReason;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
