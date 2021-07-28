package com.leyou.auth.web;

import com.leyou.auth.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;


    /**
     * 用户授权
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<Void> userAuth(
            HttpServletResponse response, @RequestParam("username")String username,
            @RequestParam("password")String password){


        this.userAuthService.userAuth(response,username,password);

        return ResponseEntity.ok().build();
    }
}
