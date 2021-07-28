package com.leyou.auth.config;

import com.leyou.auth.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JwtConfig {

    @Value("${ly.jwt.key}")
    private String key;

    @Bean
    public JwtUtils jwtUtils(){
        return new JwtUtils(key);
    }
}