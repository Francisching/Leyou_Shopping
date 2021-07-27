package com.leyou.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.User;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.leyou.common.constants.MQConstants.ExchangeConstants.SMS_EXCHANGE_NAME;
import static com.leyou.common.constants.MQConstants.RoutingKeyConstants.VERIFY_CODE_KEY;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Boolean checkExists(String data, Integer type) {
        return null;
    }

    private static final String KEY_PREFIX = "ly:user:verify:code:";

    @Override
    public void sendMessage(String phone) {

        Map<String, String> msg = new HashMap<>();
        msg.put("phone", phone);


        // 2.使用Apache的工具类生成6位数字验证码
        String code = RandomStringUtils.randomNumeric(6);

        msg.put("code", code);

        //TODO 1,验证码随机，2，验证码要保存，3，必须设置有效期，

        String key = KEY_PREFIX + phone;
        //redis中存储code，验证码5min
        redisTemplate.opsForValue().set(key,code,5, TimeUnit.MINUTES);

        this.amqpTemplate.convertAndSend(SMS_EXCHANGE_NAME, VERIFY_CODE_KEY, msg);

    }

    @Override
    public void register(String username, String password, String phone, String code) {

    }

    @Override
    public UserDTO login(String username, String password) {
        return null;
    }
}