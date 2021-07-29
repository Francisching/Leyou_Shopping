package com.leyou.auth.service.impl;

import com.leyou.auth.dto.Payload;
import com.leyou.auth.dto.UserDetail;
import com.leyou.auth.service.UserAuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.user.client.UserClient;
import com.leyou.user.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserAuthServiceImpl implements UserAuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void userAuth(HttpServletResponse response, String username, String password) {

        //获取到了userDTO，基于userDTO生成token
        UserDTO userDTO = this.userClient.login(username, password);



        //token中的用户实体变成了userDetail
        UserDetail userDetail = new UserDetail();
        userDetail.setUsername(userDTO.getUsername());
        userDetail.setId(userDTO.getId());

        //基于userDetail生成token
        String token = jwtUtils.createJwt(userDetail);

        Cookie cookie = new Cookie("LY_TOKEN",token);

        cookie.setDomain("leyou.com");

        cookie.setPath("/");

        //保护cookie，拒绝前端所有的脚步来访问
        cookie.setHttpOnly(true);

        response.addCookie(cookie);

    }

    @Override
    public void userLogout(HttpServletRequest request, HttpServletResponse response) {

       //删除redis中有效的key
        try {
            String token = CookieUtils.getCookieValue(request, "LY_TOKEN");

            UserDetail userDetail = jwtUtils.parseJwt(token).getUserDetail();

            String key = userDetail.getUsername();

            jwtUtils.deleteJwt(key);
        } catch (Exception e) {
        }

        Cookie cookie = new Cookie("LY_TOKEN","");
        cookie.setDomain("leyou.com");
        cookie.setPath("/");
        //0表示立即失效
        cookie.setMaxAge(0);

        response.addCookie(cookie);

    }
}
