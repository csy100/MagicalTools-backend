package com.mt.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailUtil;
import com.mt.common.Result;
import com.mt.dto.LoginFormDto;
import com.mt.dto.UserDto;
import com.mt.entity.User;
import com.mt.service.UserService;
import com.mt.utils.UserHolder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
     *
     * @param loginFormDto
     * @return
     */
    @PostMapping("/code")
    public Result sendCode(@RequestBody LoginFormDto loginFormDto) {
        return userService.sendCode(loginFormDto);
    }
    
    /**
     * 登录功能
     *
     * @param loginFormDto
     * @return
     */
    @PostMapping("/login")
    public Result userLogin(@RequestBody LoginFormDto loginFormDto) {
        return userService.userLogin(loginFormDto);
    }
    
    
    /**
     * 找回密码
     *
     * @return
     */
    @PostMapping("/password")
    public Result forgotPassword(@RequestBody LoginFormDto loginFormDto) {
        return userService.changePassword(loginFormDto);
    }
    
    /**
     * 用户退出
     *
     * @param token
     * @return
     */
    @PostMapping("/logout")
    public Result userLogout(@RequestParam("token") String token) {
        return userService.userLogout(token);
    }
    
    /**
     * 查询用户信息
     *
     * @return
     */
    @GetMapping("/info")
    public Result userInfo() {
        UserDto userDto = UserHolder.getUser();
        return Result.ok(userDto);
    }
    
}
