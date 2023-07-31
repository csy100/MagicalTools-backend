package com.mt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mt.common.Result;
import com.mt.entity.Session;

/**
 * Author: csy100
 * Date: 2023/7/26
 */
public interface SessionService extends IService<Session> {
    Result addNewSession(Session session);
    
    Result sessionInit();
    
    Result deleteSession(String sessionId);
    
    Result deleteAllSessions();
    
}
