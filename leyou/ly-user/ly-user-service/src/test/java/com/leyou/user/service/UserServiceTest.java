package com.leyou.user.service;

import com.leyou.user.dto.UserDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void checkExists() {

        Boolean exists = this.userService.checkExists("heima129", 1);

        System.out.println("exists = " + exists);
    }

    @Test
    public void sendMessage() {
    }

    @Test
    public void register() {
    }

    @Test
    public void login() {

        UserDTO userDTO = this.userService.login("heima129", "heima129");

        System.out.println("userDTO = " + userDTO);
    }
}