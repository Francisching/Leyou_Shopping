package com.leyou.auth.service;

import javax.servlet.http.HttpServletResponse;

public interface UserAuthService {
    void userAuth(HttpServletResponse response, String username, String password);

}
