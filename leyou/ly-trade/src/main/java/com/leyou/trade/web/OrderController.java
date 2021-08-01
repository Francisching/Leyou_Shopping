package com.leyou.trade.web;

import com.leyou.trade.dto.OrderDTO;
import com.leyou.trade.entity.Order;
import com.leyou.trade.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;


    /**
     * 下单业务,返回订单id
     * @param orderDTO
     * @return
     */
    @PostMapping
    public ResponseEntity<Long> createOrder(
            @RequestBody OrderDTO orderDTO){

        return ResponseEntity.status(HttpStatus.CREATED).body(this.orderService.createOrder(orderDTO));
    }

    /**
     * 根据订单id获取订单基本信息
     * @param orderId
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable("id")Long orderId){

        return ResponseEntity.ok(this.orderService.getById(orderId));
    }
}
