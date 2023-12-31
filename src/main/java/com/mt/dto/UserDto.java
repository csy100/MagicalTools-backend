package com.mt.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Author: csy100
 * Date: 2023/7/16
 */
@Data
public class UserDto {
    private Long id;
    private String email;
    private String nickName;
    private String icon;
    private LocalDateTime createTime;
}
