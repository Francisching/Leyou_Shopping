package com.leyou.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.User;

public interface UserService extends IService<User> {
    Boolean checkExists(String data, Integer type);

    void sendMessage(String phone);

    void register(String username, String password, String phone, String code);

    UserDTO login(String username, String password);
}