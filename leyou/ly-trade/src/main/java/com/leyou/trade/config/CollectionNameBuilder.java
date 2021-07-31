package com.leyou.trade.config;

import com.leyou.auth.dto.UserDetail;
import com.leyou.auth.utils.UserContext;


public class CollectionNameBuilder {

    private final String namePrefix;

    public CollectionNameBuilder(String namePrefix) {
        this.namePrefix = namePrefix + "_";
    }

    public String build(){
        UserDetail user = UserContext.getUser();
        if (user == null) {
            return "";
        }
        // 用一个固定collection名前缀，拼接上用户id计算出的数字，作为collection名
        return namePrefix + user.getId().hashCode() % 100;
    }
}