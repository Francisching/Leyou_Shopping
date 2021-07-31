package com.leyou;

import com.leyou.auth.annotations.EnableJwtVerification;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableJwtVerification
@EnableFeignClients
public class LyTradeApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyTradeApplication.class);
    }
}
