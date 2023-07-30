package com.mt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mt.common.Result;
import com.mt.dto.LoginFormDto;
import com.mt.entity.User;

/**
 * Author: csy100
 * Date: 2023/7/16
 */
public interface UserService extends IService<User> {
    Result sendCode(LoginFormDto loginFormDto);
    
    Result userLogin(LoginFormDto loginFormDto);
    
    Result userLogout(String token);
}
