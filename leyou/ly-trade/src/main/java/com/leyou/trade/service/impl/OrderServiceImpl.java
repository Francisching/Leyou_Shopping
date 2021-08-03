package com.leyou.trade.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.auth.dto.UserDetail;
import com.leyou.auth.utils.UserContext;
import com.leyou.common.exception.LyException;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SkuDTO;
import com.leyou.trade.dto.OrderDTO;
import com.leyou.trade.entity.Order;
import com.leyou.trade.entity.OrderDetail;
import com.leyou.trade.entity.OrderLogistics;
import com.leyou.trade.mapper.OrderMapper;
import com.leyou.trade.service.OrderDetailService;
import com.leyou.trade.service.OrderLogisticsService;
import com.leyou.trade.service.OrderService;
import com.leyou.user.client.UserClient;
import com.leyou.user.dto.AddressDTO;
import com.leyou.user.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.MultiFileHierarchicalConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserClient userClient;

    @Autowired
    private OrderLogisticsService orderLogisticsService;

    /**
     * 超卖，
     *      * 根据实际的情况，分析判断，
     *      * 一般的交易系统，不考虑超卖，鸿星尔克，特斯拉，比亚迪
     *      * 抢购，秒杀，亏本运营，淘宝奥迪a4,5折，1辆，
     *      * 2000万，定时上架，1000,2万，入口毫秒555,2万人，后台二次过滤，5，队列，消费
     *      ？？？，多线程超卖，加锁，
     *
     *      锁？？？单线程，锁的本身是一个执行条件，要执行业务
     *      必须先要获取锁，可以理解成锁只有一个，只能被一个线程获取
     *      那么会争抢锁，
     *      下单实际执行时加了全局锁，要执行下单业务
     *      不论是谁先要获取全局锁，
     *
     * @param orderDTO
     */
    @Override
    @Transactional
    public Long createOrder(OrderDTO orderDTO) {

        //key是skuId，value商品数量
        Map<Long, Integer> carts = orderDTO.getCarts();


        this.itemClient.minusStock(carts);

        UserDetail user = UserContext.getUser();

        //服务端必须对客户端请求的数据进行校验
        if (orderDTO == null
                || CollectionUtils.isEmpty(orderDTO.getCarts())
                || orderDTO.getAddressId() == null
                || orderDTO.getPaymentType() == null) {
            throw new LyException(400, "请正确提交订单");
        }

        //保存数据

        Order order = new Order();
        order.setStatus(1);
        order.setUserId(user.getId());
        order.setPostFee(0L);

        Integer paymentType = orderDTO.getPaymentType() > 2 || orderDTO.getPaymentType() < 1 ? 4 : orderDTO.getPaymentType();

        if (paymentType == 4) {
            throw new LyException(400, "支持方式有误");
        }

        order.setPaymentType(paymentType);





        //实际下单的商品的集合
        List<SkuDTO> skuDTOS = this.itemClient.listSkuByIds(new ArrayList<>(carts.keySet()));

        long totalPrice = 0;
        //总价：单价*数量并且累计
        for (SkuDTO skuDTO : skuDTOS) {
            totalPrice += skuDTO.getPrice() * carts.get(skuDTO.getId());
        }

        //应该支付总额
        order.setTotalFee(totalPrice);
        //实际支持总额,全场包邮，没有优惠，所以totalFee==actualFee
        order.setActualFee(totalPrice);

        //订单保存，主键回显
        this.save(order);


        //订单详情

        List<OrderDetail> orderDetails = skuDTOS.stream().map(skuDTO -> {
            OrderDetail orderDetail = new OrderDetail();

            orderDetail.setOrderId(order.getOrderId());

            orderDetail.setSkuId(skuDTO.getId());

            orderDetail.setNum(carts.get(skuDTO.getId()));

            orderDetail.setTitle(skuDTO.getTitle());
            orderDetail.setPrice(skuDTO.getPrice());
            orderDetail.setSpec(skuDTO.getSpecialSpec());
            orderDetail.setImage(StringUtils.substringBefore(skuDTO.getImages(), ","));
            return orderDetail;
        }).collect(Collectors.toList());

        //批量保存订单详情
        this.orderDetailService.saveBatch(orderDetails);

        AddressDTO addressDTO = this.userClient.queryAddressById(orderDTO.getAddressId(), user.getId());

        if (null == addressDTO) {
            throw new LyException(400, "用户收货地址选择有误");
        }

        OrderLogistics orderLogistics = new OrderLogistics();


        BeanUtils.copyProperties(addressDTO,orderLogistics);

        orderLogistics.setOrderId(order.getOrderId());

        this.orderLogisticsService.save(orderLogistics);



        //throw new LyException(666,"故意捣乱");




        return order.getOrderId();
    }
}
