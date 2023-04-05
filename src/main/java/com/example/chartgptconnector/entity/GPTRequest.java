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
}
