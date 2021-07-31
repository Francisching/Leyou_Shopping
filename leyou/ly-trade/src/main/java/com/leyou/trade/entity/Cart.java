package com.leyou.trade.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("#{@collectionNameBuilder.build()}")
public class Cart {
    @Id
    //自定义生成id，包含sku的信息，用户信息，联合主键，mongo不支持
    private String id;
    private Long skuId;
    //记录用户的身份
    private Long userId;
    private String image;
    private Integer num;
    private Long price;
    private String spec;
    private String title;
}
