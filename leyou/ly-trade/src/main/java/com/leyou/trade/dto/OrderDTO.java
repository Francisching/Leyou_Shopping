package com.leyou.trade.dto;

import lombok.Data;

import java.util.Map;

@Data
public class OrderDTO {

    private Long addressId;
    private Map<Long, Integer> carts;
    private Integer paymentType;
}
