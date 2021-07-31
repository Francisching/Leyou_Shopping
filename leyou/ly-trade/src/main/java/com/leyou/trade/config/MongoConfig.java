package com.leyou.trade.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MongoConfig {
    @Value("${ly.mongo.collectionNamePrefix}")
    private String collectionNamePrefix;

    @Bean
    public CollectionNameBuilder collectionNameBuilder(){
        return new CollectionNameBuilder(collectionNamePrefix);
    }
}