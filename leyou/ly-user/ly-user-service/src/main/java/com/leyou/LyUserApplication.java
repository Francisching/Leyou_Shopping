package com.leyou;

import com.leyou.auth.annotations.EnableJwtVerification;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.leyou.user.mapper")
@EnableFeignClients
@EnableJwtVerification //开启登录拦截器的支持
public class LyUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyUserApplication.class);
    }
}
