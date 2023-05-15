package com.example.chartgptconnector.service;

import java.io.OutputStream;

public interface ChartGPTservice {
    String askAi(String prompt);
    void askAiStream(String prompt,OutputStream outputStream);
    String askAiContext(String prompt,String token);
    void askAiContextStream(String prompt,String token,OutputStream outputStream);
}
