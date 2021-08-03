package com.leyou.trade.service.impl;

import com.leyou.trade.constants.PayConstants;
import com.leyou.trade.entity.Order;
import com.leyou.trade.entity.enums.OrderStatus;
import com.leyou.trade.service.OrderService;
import com.leyou.trade.service.PayService;
import com.leyou.trade.utils.PayHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

import static com.leyou.trade.constants.PayConstants.ORDER_NO_KEY;
import static com.leyou.trade.constants.PayConstants.TOTAL_FEE_KEY;

@Service
@Slf4j
public class PayServiceImpl implements PayService {

    @Autowired
    private PayHelper payHelper;

    @Autowired
    private OrderService orderService;

    @Override
    public String getPayUrl(Long orderId) {


        Order order = this.orderService.getById(orderId);


        //第三个参数建议根据商品订单id查询对应的商品，选择第一个商品的名称作为订单描述
        String payUrl = this.payHelper.getPayUrl(orderId, 1L/*order.getActualFee()*/, PayConstants.ORDER_DESC);

        //建议做缓存，减少对微信的请求，提升系统效率

        return payUrl;
    }

    @Override
    public void payNotify(Map<String, String> paramMap) {

        //1，数据校验，

        //2,校验通过后，取值，取订单id，根据订单id查询对应的订单信息，校验微信收的钱和要付的钱是否一致

        //3,如果一致并且订单状态还为1则把订单状态改为2，已支付状态

        // 1.业务标示校验
        payHelper.checkResultCode(paramMap);
        // 2.签名校验
        payHelper.checkResponseSignature(paramMap);

        // 3.订单状态校验（保证幂等，防止重复通知）
        String outTradeNo = paramMap.get(ORDER_NO_KEY);
        String totalFee = paramMap.get(TOTAL_FEE_KEY);

        if (StringUtils.isBlank(outTradeNo) || StringUtils.isBlank(totalFee)) {
            // 数据有误
            throw new RuntimeException("响应数据有误，订单金额或编号为空！");
        }
        Long orderId = Long.valueOf(outTradeNo);
        //根据订单id查询订单
        Order order = this.orderService.getById(orderId);

        if (order.getStatus()!=1) {
            // 说明订单已经支付过了，属于重复通知，直接返回
            return;
        }

        // 4.订单金额校验
        Long total = Long.valueOf(totalFee);
        if (!total.equals(1L/*order.getActualFee()*/)) {
            throw new RuntimeException("订单金额有误，我要报警了！");
        }

        // 5.修改订单状态，更新状态和支付时间两个字段
        this.orderService.update()
                .set("status", OrderStatus.PAY_UP.getValue())
                .set("pay_time", new Date())
                // 条件包括订单id和订单状态必须为1，乐观锁保证幂等
                .eq("order_id", orderId)
                .eq("status", OrderStatus.INIT.getValue()).update();

        log.info("处理微信支付通知成功！{}", paramMap);

    }
}
