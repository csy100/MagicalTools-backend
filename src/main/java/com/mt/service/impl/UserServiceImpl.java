package com.mt.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mt.common.Result;
import com.mt.controller.UserOrderController;
import com.mt.dto.LoginFormDto;
import com.mt.dto.UserDto;
import com.mt.entity.User;
import com.mt.mapper.UserMapper;
import com.mt.service.UserService;
import com.mt.utils.UserHolder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.mt.utils.RedisConstants.*;
import static com.mt.utils.SystemConstants.USER_NICK_NAME_PREFIX;

/**
 * Author: csy100
 * Date: 2023/7/16
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    
    /**
     * 发送验证码
     *
     * @param loginFormDto
     * @return
     */
    @Override
    public Result sendCode(LoginFormDto loginFormDto) {
        if (StrUtil.isBlank(loginFormDto.getEmail()) || StrUtil.isBlank(loginFormDto.getPassword())) {
            return Result.fail("邮箱和密码不能为空！");
        }
        
        String code = RandomUtil.randomNumbers(6);
        MailUtil.send(loginFormDto.getEmail(), "验证码", "您的验证码为:<h2> " + code + "</h2>", true);
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + loginFormDto.getEmail(), code,
                LOGIN_CODE_TTL, TimeUnit.MINUTES);
        
        return Result.ok();
    }
    
    
    /**
     * 登录功能
     *
     * @param loginFormDto
     * @return
     */
    @Override
    public Result userLogin(LoginFormDto loginFormDto) {
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + loginFormDto.getEmail());
        if (cacheCode == null || !cacheCode.equals(loginFormDto.getCode())) {
            return Result.fail("验证码错误，请稍后重试！");
        }
        
        User user = this.query().eq("email", loginFormDto.getEmail()).one();
        if (user == null) {
            user = createUserWithEmail(loginFormDto);
        }
        
        String loginPassword = DigestUtils.md5DigestAsHex(loginFormDto.getPassword().getBytes());
        if (!user.getPassword().equals(loginPassword)) {
            return Result.fail("密码错误");
        }
        // 生成token
        String token = UUID.randomUUID().toString(true);
        
        UserDto userDto = BeanUtil.copyProperties(user, UserDto.class);
        
        // 将userDTO数据储存到Map集合下,并在赋值的时候将每一个字段的值转为字符串形式
        Map<String, Object> userMap = BeanUtil.beanToMap(userDto, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((filedName, filedValue) -> filedValue.toString()));
        
        stringRedisTemplate.opsForHash().putAll(LOGIN_USER_KEY + token, userMap);
        stringRedisTemplate.expire(LOGIN_USER_KEY + token, LOGIN_USER_TTL, TimeUnit.DAYS);
        
        return Result.ok(token);
    }
    
    /**
     * 退出功能
     * @param token
     * @return
     */
    @Override
    public Result userLogout(String token) {
        if (StrUtil.isBlank(token)) {
            return Result.fail("登录信息已过期！");
        }
        stringRedisTemplate.delete(LOGIN_USER_KEY + token);
        return Result.ok("已安全退出！");
    }
    
    /**
     * 找回密码
     * @param loginFormDto
     * @return
     */
    @Override
    public Result changePassword(LoginFormDto loginFormDto) {
        User user = this.query().eq("email", loginFormDto.getEmail()).one();
        if (StrUtil.isBlank(loginFormDto.getEmail())) {
            return Result.fail("请先填写邮箱和输入新的密码");
        }
        if (user == null) {
            return Result.fail("您未注册该账号，请先进行登录注册");
        }
        
        String code = RandomUtil.randomNumbers(6);
        String content = "您的新密码为:<h2> " + loginFormDto.getPassword() + "</h2>" +
                "您的验证码为:<h2> " + code + "</h2>(60秒内有效)";
        MailUtil.send(loginFormDto.getEmail(), "重置密码", content, true);
        
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + loginFormDto.getEmail(), code,
                LOGIN_CODE_TTL, TimeUnit.MINUTES);
        
        String newPassword = DigestUtils.md5DigestAsHex(loginFormDto.getPassword().getBytes());
        user.setPassword(newPassword);
        this.updateById(user);
        return Result.ok("请注意查收邮箱");
    }
    
    private User createUserWithEmail(LoginFormDto loginFormDto) {
        User user = new User();
        user.setEmail(loginFormDto.getEmail());
        // 生成随机 nickName
        user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomNumbers(10));
        
        // 加密密码
        String password = loginFormDto.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        user.setPassword(password);
        
        // 保存用户到数据库中
        this.save(user);
        return user;
    }
}
