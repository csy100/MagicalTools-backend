package com.mt.controller;

import com.mt.common.Result;
import com.mt.entity.UserOrder;
import com.mt.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Author: csy100
 * Date: 2023/8/19
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class UserOrderController {

    @Resource
    private UserService userService;

    /**
     * 获取二维码
     * @return
     */
    @GetMapping("/getQRCode")
    public Result getQRCOde(@RequestParam String price) {
        return userService.getCode(price);
    }

    public record PayRequest(String aoid, String order_id,
                             String pay_price, String pay_time,
                             String more, String detail,
                             String sign
    ) { }
    /**
     * 被动获取--接口的回调
     */
    @PostMapping("/getPayRes")
    public ResponseEntity<String> getPayRes(PayRequest request) {
        userService.getResByXorPay(request);
        return ResponseEntity.badRequest().build();
    }
}
