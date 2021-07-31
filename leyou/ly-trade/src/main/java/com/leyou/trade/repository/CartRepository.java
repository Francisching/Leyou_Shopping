package com.leyou.trade.repository;

import com.leyou.trade.entity.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CartRepository extends MongoRepository<Cart,String> {
    List<Cart> findByUserId(Long id);
}
