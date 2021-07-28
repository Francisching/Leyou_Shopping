package com.leyou.auth.utils;

import com.leyou.auth.dto.Payload;
import com.leyou.auth.dto.UserDetail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    public void testJwt(){

        Payload payload = jwtUtils.parseJwt("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjMTIzZDIxMWVkZTA0OWEzYjVmMGU3Njg4MTUwOTgxMSIsInVzZXIiOiJ7XCJpZFwiOjMwLFwidXNlcm5hbWVcIjpcImhlaW1hMTI5XCJ9In0.w7R_O-imD_RwBxBBoyV0x4A6eeYRzpJWZIwbXPMu8Og");

        UserDetail userDetail = payload.getUserDetail();

        System.out.println("userDetail = " + userDetail);
        String jti = payload.getJti();

        System.out.println("jti = " + jti);

    }

}