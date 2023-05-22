package com.example.chartgptconnector.entity;

import cn.hutool.json.JSONObject;

public class SSEGPTChoice {
    private JSONObject delta;
    private int index;
    private String finish_reason;

    public JSONObject getDelta() {
        return delta;
    }

    public void setDelta(JSONObject delta) {
        this.delta = delta;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getFinish_reason() {
        return finish_reason;
    }

    public void setFinish_reason(String finish_reason) {
        this.finish_reason = finish_reason;
    }
}
