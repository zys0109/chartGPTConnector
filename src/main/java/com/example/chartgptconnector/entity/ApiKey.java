package com.example.chartgptconnector.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("api_key")
public class ApiKey {
    private int id;
    private String apiKey;
    private String account;
    private boolean isEffective;
}
