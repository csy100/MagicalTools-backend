package com.mt.controller;

import com.mt.common.Result;
import com.mt.entity.PayRequest;
import com.mt.service.UserOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Author: csy100
 * Date: 2023/8/19
 */
@RestController
@RequestMapping("/order/")
@Slf4j
public class UserOrderController {

    @Resource
    private UserOrderService userOrderService;

    /**
     * 获取二维码
     * @return
     */
    @GetMapping("/getQRCode")
    public Result getQRCOde(@RequestParam String price) {
        return userOrderService.getCode(price);
    }

    /**
     * 被动获取--接口的回调
     */
    @PostMapping("/getPayRes")
    public ResponseEntity<String> getPayRes(@ModelAttribute PayRequest request) {
        return userOrderService.getResByXorPay(request);
    }
}
