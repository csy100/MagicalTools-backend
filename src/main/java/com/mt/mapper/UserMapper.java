package com.mt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mt.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * Author: csy100
 * Date: 2023/7/16
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
