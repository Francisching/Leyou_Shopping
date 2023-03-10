package com.leyou.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;


@Data
@ConfigurationProperties("ly.auth")
public class ClientProperties {
    private String clientId;
    private String secret;

    /**
     * 拦截器拦截路径
     */
    private List<String> includeFilterPaths;
    /**
     * 拦截器放行路径
     */
    private List<String> excludeFilterPaths;
}
