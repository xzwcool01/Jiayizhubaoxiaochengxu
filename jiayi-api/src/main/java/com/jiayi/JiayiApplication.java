package com.jiayi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.jiayi.mapper")
public class JiayiApplication {
    public static void main(String[] args) {
        SpringApplication.run(JiayiApplication.class, args);
    }
}
