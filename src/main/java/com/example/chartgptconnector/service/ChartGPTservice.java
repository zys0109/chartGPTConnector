package com.example.chartgptconnector.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.OutputStream;

public interface ChartGPTservice {
    String askAi(String prompt);
    void askAiStream(String prompt,OutputStream outputStream);
    String askAiContext(String prompt,String token);
    SseEmitter askAiContextStreamSSE(String prompt);
    void askAiContextStream(String prompt,String token,OutputStream responseOutputStream);
    String askAiPictureSearch(String prompt,String token);

}
