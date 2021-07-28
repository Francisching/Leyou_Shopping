package com.leyou.user.client;

import com.leyou.user.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface UserClient {

    /**
     * 用户登录接口
     *
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/info")
    UserDTO login(
            @RequestParam("username") String username,
            @RequestParam("password") String password);
}
