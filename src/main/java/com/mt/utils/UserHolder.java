package com.mt.utils;

import com.mt.dto.UserDto;

/**
 * Author: csy100
 * Date: 2023/7/18
 */
public class UserHolder {
    
    private static final ThreadLocal<UserDto> tl = new ThreadLocal<>();
    
    public static void saveUser(UserDto user) {
        tl.set(user);
    }
    
    public static UserDto getUser(){
        return tl.get();
    }
    
    public static void removeUser(){
        tl.remove();
    }
}
