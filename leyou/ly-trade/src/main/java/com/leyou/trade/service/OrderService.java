package com.leyou.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.trade.dto.OrderDTO;
import com.leyou.trade.entity.Order;

public interface OrderService extends IService<Order> {
    Long createOrder(OrderDTO orderDTO);
}
