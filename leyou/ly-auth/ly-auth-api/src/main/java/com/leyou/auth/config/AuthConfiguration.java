package com.leyou.auth.config;

import com.leyou.auth.ClientProperties;
import com.leyou.auth.client.AuthClient;
import com.leyou.auth.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "ly.auth", name = {"clientId", "secret"})
@EnableConfigurationProperties(ClientProperties.class)
public class AuthConfiguration {

    private AuthClient authClient;
    private ClientProperties properties;

    public AuthConfiguration(AuthClient authClient, ClientProperties properties) {
        this.authClient = authClient;
        this.properties = properties;
    }

    @Bean
    @Primary
    public JwtUtils jwtUtils(){
        try {
            // 查询秘钥
            String key = authClient.authServer(properties.getClientId(), properties.getSecret());
            // 创建JwtUtils
            JwtUtils jwtUtils = new JwtUtils(key);
            log.info("秘钥加载成功。");
            return jwtUtils;
        } catch (Exception e) {
            log.error("初始化JwtUtils失败，{}", e.getMessage());
            throw e;
        }
    }
}