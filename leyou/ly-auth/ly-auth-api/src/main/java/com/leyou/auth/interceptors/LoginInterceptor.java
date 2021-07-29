package com.leyou.auth.interceptors;

import com.leyou.auth.dto.Payload;
import com.leyou.auth.dto.UserDetail;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.UserContext;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 状态bean，bean中存储了贡献信息，并且是可变化的共享信息
 * 状态bean会带来的问题是？ 数据污染
 * 单例bean被线程共享，线程不安全，综上，我们直接把userDetail作为状态bean的属性，不可以
 *
 * 状态bean，单例bean===>单线程
 * 状态bean，单例bean，多线程=====》bug
 * 状态bean,中的状态属性存入ThreadLocal，单例bean，多线程====》OK
 * ThreadLocal，特点，一次请求，一个线程，每一个线程对应的一个独立的threadLocal，
 * threadLocal只能被唯一的线程访问，thread-id-1,
 *
 *
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    public LoginInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            // 获取cookie中的jwt
            String jwt = CookieUtils.getCookieValue(request, "LY_TOKEN");
            // 验证并解析
            Payload payload = jwtUtils.parseJwt(jwt);
            // 获取用户
            UserDetail userDetails = payload.getUserDetail();
            log.info("用户{}正在访问。", userDetails.getUsername());
            // 保存用户,到threadLocal中
            UserContext.setUser(userDetails);
            return true;
        } catch (JwtException e) {
            throw new LyException(401, "JWT无效或过期!", e);
        } catch (IllegalArgumentException e){
            throw new LyException(401, "用户未登录!", e);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 业务结束后，移除用户
        UserContext.removeUser();
    }

}