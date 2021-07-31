package com.leyou.trade.service.impl;

import com.leyou.auth.dto.UserDetail;
import com.leyou.auth.utils.UserContext;
import com.leyou.trade.entity.Cart;
import com.leyou.trade.repository.CartRepository;
import com.leyou.trade.service.CartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Override
    public void addCart(Cart cart) {

        UserDetail user = UserContext.getUser();

        String id = idUtils(cart.getSkuId(), user.getId());

        //加过改数量，num(已经存储的数量加本次添加的数量)，没有加过，直接添加
        Optional<Cart> result = cartRepository.findById(id);

        //加过
        if (result.isPresent()) {
            Cart storeCart = result.get();

            storeCart.setNum(storeCart.getNum() + cart.getNum());

            this.cartRepository.save(storeCart);
        }else{
            //处理id部分，用户UserId
            cart.setId(id);
            cart.setUserId(user.getId());

            this.cartRepository.save(cart);
        }


    }

    @Override
    public List<Cart> listCarts() {

        UserDetail user = UserContext.getUser();

        List<Cart> carts = this.cartRepository.findByUserId(user.getId());
        return carts;
    }

    @Override
    public String idUtils(Long skuId, Long userId) {

        //u31s123456
        return String.format("u%ds%d",userId,skuId);
    }
}
