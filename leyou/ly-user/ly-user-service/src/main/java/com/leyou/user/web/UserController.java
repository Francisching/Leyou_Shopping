package com.leyou.user.web;

import com.leyou.auth.dto.UserDetail;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/info")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 查询数据（用户名以及手机号）是否已存在
     *
     * @param data
     * @param type
     * @return
     */
    @GetMapping("/exists/{data}/{type}")
    public ResponseEntity<Boolean> checkExists(
            @PathVariable("data") String data,
            @PathVariable("type") Integer type) {

        return ResponseEntity.ok(this.userService.checkExists(data, type));
    }

    /**
     * 发短信
     *
     * @param phone
     * @return
     */
    @PostMapping("/code")
    public ResponseEntity<Void> sendMessage(@RequestParam("phone") String phone) {

        this.userService.sendMessage(phone);
        return ResponseEntity.ok().build();
    }

    /**
     * 用户注册
     *
     * @param username
     * @param password
     * @param phone
     * @param code
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> register(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("phone") String phone,
            @RequestParam("code") String code) {

        this.userService.register(username, password, phone, code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 用户登录接口
     *
     * @param username
     * @param password
     * @return
     */
    @GetMapping
    public ResponseEntity<UserDTO> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {

        return ResponseEntity.ok(this.userService.login(username, password));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDetail> showUser(@CookieValue("LY_TOKEN") String token) {


        return ResponseEntity.ok(this.userService.showUser(token));
    }
}
