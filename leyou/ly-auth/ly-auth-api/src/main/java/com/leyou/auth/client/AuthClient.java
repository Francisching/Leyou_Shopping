package com.leyou.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("auth-service")
public interface AuthClient {
    @GetMapping("/server")
    String authServer(@RequestParam("clientId") String clientId,
                      @RequestParam("password") String password);
}
