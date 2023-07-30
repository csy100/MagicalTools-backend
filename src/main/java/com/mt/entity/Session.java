package com.mt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Author: csy100
 * Date: 2023/7/26
 */
@Data
@TableName("tb_session")
public class Session implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户索引
     */
    private Long userId;
    
    /**
     * 会话索引
     */
    private String sessionId;
    
    /**
     * 会话标题
     */
    private String sessionTitle;
    
    /**
     * 注册时间
     */
    private LocalDateTime createTime;
    
    /**
     * 用户手机号
     */
    private LocalDateTime updateTime;
}
