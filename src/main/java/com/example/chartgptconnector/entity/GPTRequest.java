package com.example.chartgptconnector.entity;

public class GPTRequest {


    public String getAskStr() {
        return askStr;
    }

    public void setAskStr(String askStr) {
        this.askStr = askStr;
    }

    public String getReplyStr() {
        return replyStr;
    }

    public void setReplyStr(String replyStr) {
        this.replyStr = replyStr;
    }

    //问题
    private String askStr;
    //回答
    private String replyStr;
    //用户openId用于实现上下文关联对话
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
