package cn.itcast.mbp.web;

import cn.itcast.mbp.pojo.User;
import cn.itcast.mbp.service.UserService;

public class CTL {


    private UserService userService;

    public User queryUserById(Long id){

        return this.userService.getById(id);
    }
}
