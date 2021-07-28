package com.leyou.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.auth.entity.ClientInfo;
import com.leyou.auth.mapper.ClientInoMapper;
import com.leyou.auth.service.ServerAuthService;
import com.leyou.common.exception.LyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ServerAuthServiceImpl extends ServiceImpl<ClientInoMapper, ClientInfo> implements ServerAuthService {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${ly.jwt.key}")
    private String key;

    @Override
    public String authServer(String clientId, String password) {

        ClientInfo clientInfo = this.query().eq("client_id", clientId).one();

        if (null==clientInfo || !passwordEncoder.matches(password,clientInfo.getSecret())){
            throw new LyException(401,"非法请求，已报警");
        }

        return key;
    }
}
