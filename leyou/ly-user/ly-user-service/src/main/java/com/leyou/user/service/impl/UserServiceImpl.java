package com.leyou.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.auth.dto.Payload;
import com.leyou.auth.dto.UserDetail;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.exception.LyException;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.User;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        String colName;

        switch (type) {
            case 1:
                colName = "username";
                break;
            case 2:
                colName = "phone";
                break;
            default:
                throw new LyException(400, "只能校验用户名或手机号");
        }

        //select count(*) from tb_user where (username = 'ldh') [phone='123456789']
        return this.query().eq(colName, data).count() == 1;

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
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);

        this.amqpTemplate.convertAndSend(SMS_EXCHANGE_NAME, VERIFY_CODE_KEY, msg);

    }

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void register(String username, String password, String phone, String code) {

        String key = KEY_PREFIX + phone;
        //key不存在，或者存储的验证码和输入的验证码不一致
        if (!redisTemplate.hasKey(key) || !code.equals(redisTemplate.opsForValue().get(key))) {
            throw new LyException(400, "验证码输入有误");
        }

        //及时的处理失效的验证码
        redisTemplate.delete(key);

        User user = new User();
        user.setPhone(phone);
        user.setUsername(username);

        //加盐加密，问题，盐值salt，保存盐值，动态盐值，spring，

        String encodePassword = passwordEncoder.encode(password);
        user.setPassword(encodePassword);

        this.save(user);


        //怎么写？写什么

    }

    @Override
    public UserDTO login(String username, String password) {

        User user = this.query()
                .eq("username", username)
                .one();

        //验密，spring的passwordEncode中使用了动态盐加密，每次的盐值都不同，
        //所以，要推测盐，spring自己会根据密码，获取盐值，然后使用上一次的盐值，和明文，再次加密
        if (null == user || !this.passwordEncoder.matches(password, user.getPassword())) {
            throw new LyException(401, "用户名或密码错误");
        }


        return new UserDTO(user);
    }

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public UserDetail showUser(String token) {

        Payload payload = jwtUtils.parseJwt(token);
        return payload.getUserDetail();
    }
}