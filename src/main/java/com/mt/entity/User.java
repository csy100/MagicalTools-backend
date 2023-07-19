package com.mt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Author: csy100
 * Date: 2023/7/15
 */
@Data
@TableName("tb_user")
public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户邮箱
     */
    private String email;
    
    /**
     * 用户密码，加密存储
     */
    private String password;
    
    /**
     * 用户获取的验证码
     */
    @TableField(exist = false)
    private String code;
    
    /**
     * 用户昵称，默认是随机字符
     */
    private String nickName;
    
    /**
     * 用户头像
     */
    private String icon = "";
    
    /**
     * 注册时间
     */
    private LocalDateTime createTime;
    
    /**
     * 用户手机号
     */
    private LocalDateTime updateTime;
}
