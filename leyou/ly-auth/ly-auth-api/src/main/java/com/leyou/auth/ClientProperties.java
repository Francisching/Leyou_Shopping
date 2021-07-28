package com.leyou.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties("ly.auth")
public class ClientProperties {
    private String clientId;
    private String secret;
}
