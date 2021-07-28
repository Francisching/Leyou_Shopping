package com.leyou.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.auth.entity.ClientInfo;

public interface ServerAuthService extends IService<ClientInfo> {
    String authServer(String clientId, String password);
}
