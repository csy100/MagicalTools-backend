package com.mt;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import com.mt.dto.UserDto;
import com.mt.entity.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class MagicalToolsBackendApplicationTests {
    
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    
    @Test
    void contextLoads() {
    
    }
    
}
