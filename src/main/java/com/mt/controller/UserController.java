package com.mt.controller;

import com.mt.common.Result;
import com.mt.dto.LoginFormDto;
import com.mt.entity.ChatTest;
import com.mt.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author: csy100
 * Date: 2023/7/15
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Resource
    private UserService userService;
    
    /**
     * 发送验证码
     * @param loginFormDto
     * @return
     */
    @PostMapping("/code")
    public Result sendCode(@RequestBody LoginFormDto loginFormDto) {
        return userService.sendCode(loginFormDto);
    }
    
    /**
     * 登录功能
     * @param loginFormDto
     * @return
     */
    @PostMapping("/login")
    public Result userLogin(@RequestBody LoginFormDto loginFormDto) {
        return userService.userLogin(loginFormDto);
    }
    
}
