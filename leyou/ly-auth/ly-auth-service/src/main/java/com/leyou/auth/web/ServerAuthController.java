package com.leyou.auth.web;

import com.leyou.auth.service.ServerAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/server")
public class ServerAuthController {

    @Autowired
    private ServerAuthService serverAuthService;

    @GetMapping
    public ResponseEntity<String> authServer(@RequestParam("clientId")String clientId,
                             @RequestParam("password")String password){

        return ResponseEntity.ok(this.serverAuthService.authServer(clientId,password));
    }
}
