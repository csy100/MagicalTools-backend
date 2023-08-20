package com.mt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mt.common.Result;
import com.mt.controller.UserOrderController;
import com.mt.dto.LoginFormDto;
import com.mt.entity.User;
import com.mt.entity.UserOrder;

/**
 * Author: csy100
 * Date: 2023/7/16
 */
public interface UserService extends IService<User> {
    Result sendCode(LoginFormDto loginFormDto);
    
    Result userLogin(LoginFormDto loginFormDto);
    
    Result userLogout(String token);
    
    Result changePassword(LoginFormDto loginFormDto);

    Result getCode(String price);

    void getResByXorPay(UserOrderController.PayRequest request);
}
