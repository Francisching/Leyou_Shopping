package com.leyou.trade.web;

import com.leyou.trade.dto.PayResultDTO;
import com.leyou.trade.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {
    @Autowired
    private PayService payService;

    @GetMapping("/url/{orderId}")
    public ResponseEntity<String> getPayUrl(@PathVariable("orderId")Long orderId){

        return ResponseEntity.ok(this.payService.getPayUrl(orderId));
    }


    /**
     * 支付回调，接收微信的是xml，返回给微信还是xml，所以要引入jackson-dataFormat-xml
     * @param paramMap
     * @return
     */
    @PostMapping(value = "/wx/notify",produces = "application/xml")
    public ResponseEntity<PayResultDTO> payNotify(
            @RequestBody Map<String,String> paramMap){

        this.payService.payNotify(paramMap);

        PayResultDTO payResultDTO = new PayResultDTO();


        return ResponseEntity.ok(payResultDTO);
    }
}
