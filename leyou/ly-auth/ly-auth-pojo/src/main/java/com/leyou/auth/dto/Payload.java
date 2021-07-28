package com.leyou.auth.dto;

import lombok.Data;


@Data
public class Payload {
    private String jti;
    private UserDetail userDetail;
}
