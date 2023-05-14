package com.example.chartgptconnector;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.example.chartgptconnector.mapper"})
public class

ChartGpTconnectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChartGpTconnectorApplication.class, args);
    }

}
