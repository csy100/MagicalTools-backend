package com.mt.controller;

import com.mt.common.Result;
import com.mt.entity.UserOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Author: csy100
 * Date: 2023/8/19
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class UserOrderController {
    
    /**
     * 获取二维码
     * @return
     */
    @GetMapping("/getQRCode")
    public Result getQRCOde() {
        // TODO 获取二维码
        return Result.ok("https://xorpay.com/qr?data=123456");
    }
    
}
