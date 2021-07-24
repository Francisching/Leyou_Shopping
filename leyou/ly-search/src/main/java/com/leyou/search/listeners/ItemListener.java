package com.leyou.search.listeners;

import com.leyou.search.service.IndexService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemListener {

    @Autowired
    private IndexService indexService;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "item.queue.up",durable = "true"),
            exchange = @Exchange(name = "jhj",type = ExchangeTypes.TOPIC,durable = "true"),
            key = "item.up"
    ))
    public void goodsUp(Long spuId){

        this.indexService.addGoods(spuId);
    }

    @RabbitListener(bindings=@QueueBinding(
            value = @Queue(name = "item.queue.down",durable = "true"),
            exchange = @Exchange(name = "jhj",type = ExchangeTypes.TOPIC,durable = "true"),
            key = "item.down"
    ))
    public void goodsDown(Long spuId){

        this.indexService.deleteGoods(spuId);
    }
}
