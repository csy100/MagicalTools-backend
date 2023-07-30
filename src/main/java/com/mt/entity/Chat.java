package com.mt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Author: csy100
 * Date: 2023/7/25
 */
@Data
@TableName("tb_chat")
public class Chat implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户id索引值
     */
    private Long userId;
    
    /**
     * 会话索引id值
     */
    private String sessionId;
    
    /**
     * 聊天信息返回类型
     */
    private Integer chatType;
    
    /**
     * 聊天内容
     */
    private String chatContent;
    
    /**
     * 注册时间
     */
    private LocalDateTime createTime;
    
    /**
     * 用户手机号
     */
    private LocalDateTime updateTime;
}
