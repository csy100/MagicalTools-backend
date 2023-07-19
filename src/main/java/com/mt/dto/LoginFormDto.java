package com.mt.dto;

import lombok.Data;

/**
 * Author: csy100
 * Date: 2023/7/15
 */
@Data
public class LoginFormDto {
    private String email;
    private String password;
    private String code;
}
