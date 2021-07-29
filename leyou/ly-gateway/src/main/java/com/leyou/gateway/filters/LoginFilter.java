package com.leyou.gateway.filters;

import com.leyou.auth.dto.Payload;
import com.leyou.auth.dto.UserDetail;
import com.leyou.auth.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoginFilter implements GlobalFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 1.获取Request对象
        ServerHttpRequest request = exchange.getRequest();
        // 2.获取cookie
        HttpCookie cookie = request.getCookies().getFirst("LY_TOKEN");
        if (cookie == null) {
            // 没有登录，放行
            return chain.filter(exchange);
        }
        // 3.校验token是否有效
        String jwt = cookie.getValue();
        try {
            // 3.1.解析并验证token
            Payload payload = jwtUtils.parseJwt(jwt);
            // 3.2.获取用户
            UserDetail userInfo = payload.getUserDetail();
            // 3.3.刷新jwt
            jwtUtils.refreshJwt(userInfo.getUsername());
            log.info("用户{}正在访问{}", userInfo.getUsername(), request.getURI().getPath());
        } catch (Exception e) {
            // 解析失败，token有误
            log.info("用户未登录");
        }
        // 5.放行
        return chain.filter(exchange);
    }
}
