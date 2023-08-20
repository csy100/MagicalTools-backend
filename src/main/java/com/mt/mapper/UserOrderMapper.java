package com.mt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mt.entity.Chat;
import com.mt.entity.UserOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * Author: csy100
 * Date: 2023/7/28
 */
@Mapper
public interface UserOrderMapper extends BaseMapper<UserOrder> {
}
