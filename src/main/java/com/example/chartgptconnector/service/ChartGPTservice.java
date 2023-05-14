package com.example.chartgptconnector.service;

import java.io.OutputStream;

public interface ChartGPTservice {
    String askAi(String prompt);
    void askAiStream(String prompt,OutputStream outputStream);
    String askAiContext(String prompt,String openId);
    void askAiContextStream(String prompt,String openId,OutputStream outputStream);
}
