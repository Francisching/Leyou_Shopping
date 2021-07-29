package com.leyou.auth.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.auth.dto.Payload;
import com.leyou.auth.dto.UserDetail;
import com.leyou.common.exception.LyException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class JwtUtils {

    /**
     * JWT解析器
     */
    private final JwtParser jwtParser;
    /**
     * 秘钥
     */
    private final SecretKey secretKey;

    private final static ObjectMapper mapper = new ObjectMapper();

    private StringRedisTemplate redisTemplate;

    public JwtUtils(String key, StringRedisTemplate redisTemplate) {
        // 生成秘钥
        secretKey = Keys.hmacShaKeyFor(key.getBytes(Charset.forName("UTF-8")));
        // JWT解析器
        this.jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();

        this.redisTemplate = redisTemplate;
    }


    /**
     * 生成jwt，自己指定的JTI
     *
     * @param userDetails 用户信息
     * @return JWT
     */
    public String createJwt(UserDetail userDetails) {

        try {
            String jti = createJti();

            String key = userDetails.getUsername();

            //给当前token的id，设置了过期时间30min，30min后，此token的id从redis自动删除
            redisTemplate.opsForValue().set(key, jti, 30, TimeUnit.MINUTES);

            // 生成token
            return Jwts.builder().signWith(secretKey)
                    .setId(jti)
                    .claim("user", mapper.writeValueAsString(userDetails))
                    .compact();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析jwt，并将用户信息转为指定的Clazz类型
     *
     * @param jwt token
     * @return 载荷，包含JTI和用户信息
     */
    public Payload parseJwt(String jwt) {
        try {
            Jws<Claims> claimsJws = jwtParser.parseClaimsJws(jwt);
            Claims claims = claimsJws.getBody();

            Payload payload = new Payload();
            payload.setJti(claims.getId());
            UserDetail userDetail = mapper.readValue(claims.get("user", String.class), UserDetail.class);

            String key = userDetail.getUsername();

            //没有这个key或者，取出的jti和解析到的jti不一致，此时说明登录解析应该停止
            if (!redisTemplate.hasKey(key) || !redisTemplate.opsForValue().get(key).equals(payload.getJti())) {
                throw new LyException(401,"登录已失效");
            }


            payload.setUserDetail(userDetail);


            return payload;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String createJti() {
        return StringUtils.replace(UUID.randomUUID().toString(), "-", "");
    }

    public void refreshJwt(String username) {
        this.redisTemplate.expire(username,30,TimeUnit.MINUTES);
    }

    public void deleteJwt(String key) {
        this.redisTemplate.delete(key);
    }
}