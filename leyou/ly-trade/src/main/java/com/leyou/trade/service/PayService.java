package com.leyou.trade.service;

import java.util.Map;

public interface PayService {
    String getPayUrl(Long orderId);

    void payNotify(Map<String, String> paramMap);
}
