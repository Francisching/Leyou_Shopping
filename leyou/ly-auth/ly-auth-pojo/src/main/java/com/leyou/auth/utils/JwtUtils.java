package com.leyou.auth.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.auth.dto.Payload;
import com.leyou.auth.dto.UserDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;


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

    public JwtUtils(String key) {
        // 生成秘钥
        secretKey = Keys.hmacShaKeyFor(key.getBytes(Charset.forName("UTF-8")));
        // JWT解析器
        this.jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
    }



    /**
     * 生成jwt，自己指定的JTI
     *
     * @param userDetails 用户信息
     * @return JWT
     */
    public String createJwt(UserDetail userDetails) {

        try {
            // 生成token
            return Jwts.builder().signWith(secretKey)
                    .setId(createJti())
                    .claim("user", mapper.writeValueAsString(userDetails))
                    .compact();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析jwt，并将用户信息转为指定的Clazz类型
     *
     * @param jwt   token
     * @return 载荷，包含JTI和用户信息
     */
    public Payload parseJwt(String jwt) {
        try {
            Jws<Claims> claimsJws = jwtParser.parseClaimsJws(jwt);
            Claims claims = claimsJws.getBody();

            Payload payload = new Payload();
            payload.setJti(claims.getId());
            payload.setUserDetail(mapper.readValue(claims.get("user", String.class), UserDetail.class));
            return payload;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String createJti() {
        return StringUtils.replace(UUID.randomUUID().toString(), "-", "");
    }
}