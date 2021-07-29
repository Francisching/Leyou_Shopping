package com.leyou.auth.annotations;

import com.leyou.auth.config.MvcConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用JWT验证开关，会通过mvc的拦截器拦截用户请求，并获取用户信息，存入UserContext
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
//引用，本质类似与maven的dependency，就可以在加了自定义注解的位置，实现关联扫描
@Import(MvcConfiguration.class)
@Documented
@Inherited
public @interface EnableJwtVerification {
}