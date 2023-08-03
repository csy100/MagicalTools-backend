package com.mt.utils;

/**
 * Author: csy100
 * Date: 2023/7/16
 */
public class RedisConstants {
    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long LOGIN_CODE_TTL = 1L;
    public static final String LOGIN_USER_KEY = "login:token:";
    public static final Long LOGIN_USER_TTL = 15L;
    public static final String CHAT_USER_KEY = "login:chat:";
    public static final Long CHAT_USER_TTL = 1L;
}
