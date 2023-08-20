package com.mt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mt.common.Result;
import com.mt.controller.UserOrderController;
import com.mt.dto.LoginFormDto;
import com.mt.entity.PayRequest;
import com.mt.entity.User;
import com.mt.entity.UserOrder;
import org.springframework.http.ResponseEntity;

/**
 * Author: csy100
 * Date: 2023/7/16
 */
public interface UserOrderService extends IService<UserOrder> {

    Result getCode(String price);

    ResponseEntity<String> getResByXorPay(PayRequest request);
}
