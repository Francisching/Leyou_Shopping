package com.leyou.trade.web;

import com.leyou.trade.entity.Cart;
import com.leyou.trade.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;


    /**
     * 加入购物车
     * @param cart
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addCart(
            @RequestBody Cart cart){

        this.cartService.addCart(cart);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 前端从mongo中获取当前加入购物车的商品数据
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<Cart>> listCarts(){
        return ResponseEntity.ok(this.cartService.listCarts());
    }
}
