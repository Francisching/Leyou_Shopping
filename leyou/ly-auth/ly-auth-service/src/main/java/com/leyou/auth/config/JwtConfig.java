package com.leyou.auth.config;

import com.leyou.auth.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;


@Configuration
public class JwtConfig {

    @Value("${ly.jwt.key}")
    private String key;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Bean
    public JwtUtils jwtUtils(){
        return new JwtUtils(key,redisTemplate);
    }
}