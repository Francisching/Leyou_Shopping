package com.leyou.trade.service;

import com.leyou.trade.entity.Cart;

import java.util.List;

public interface CartService {
    void addCart(Cart cart);

    List<Cart> listCarts();

    String idUtils(Long skuId,Long userId);

    void batchAddCarts(List<Cart> carts);
}
