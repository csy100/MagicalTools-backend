package com.mt.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.mt.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.mt.utils.RedisConstants.LOGIN_USER_KEY;
import static com.mt.utils.RedisConstants.LOGIN_USER_TTL;

/**
 * Author: csy100
 * Date: 2023/7/20
 */
public class RefreshTokenInterceptor implements HandlerInterceptor {
    
    private StringRedisTemplate stringRedisTemplate;
    
    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("authorization");
        // 如果token为空的话则直接返回
        if (StrUtil.isBlank(token)) {
            return true;
        }
        
        String key  = LOGIN_USER_KEY + token;
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(key);
        
        if (userMap.isEmpty()) {
            return true;
        }
        
        UserDto userDto = BeanUtil.fillBeanWithMap(userMap, new UserDto(), false);
        // 存在，保存用户信息到 ThreadLocal
        UserHolder.saveUser(userDto);
        // 刷新token有效期
        stringRedisTemplate.expire(key, LOGIN_USER_TTL, TimeUnit.DAYS);
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
