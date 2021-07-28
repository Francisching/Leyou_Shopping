package com.leyou;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.leyou.user.mapper")
@EnableFeignClients
public class LyUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyUserApplication.class);
    }
}
