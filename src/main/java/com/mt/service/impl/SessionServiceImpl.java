package com.mt.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mt.common.Result;
import com.mt.dto.SessionDto;
import com.mt.entity.Session;
import com.mt.mapper.SessionMapper;
import com.mt.service.SessionService;
import com.mt.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: csy100
 * Date: 2023/7/26
 */
@Slf4j
@Service
public class SessionServiceImpl extends ServiceImpl<SessionMapper, Session> implements SessionService {
    
    
    /**
     * 初始化数据
     *
     * @return
     */
    @Override
    public Result sessionInit() {
        Long userId = UserHolder.getUser().getId();
        
        QueryChainWrapper<Session> sessionQueryChainWrapper = this.query().eq("user_id", userId).orderByDesc("update_time");
        List<Session> sessionList = sessionQueryChainWrapper.list();
        
        List<SessionDto> sessionDtoList = new ArrayList<>();
        for (Session session : sessionList) {
            SessionDto sessionDto = new SessionDto();
            sessionDto.setSessionId(session.getSessionId());
            sessionDto.setSessionTitle(session.getSessionTitle());
            
            sessionDtoList.add(sessionDto);
        }
        return Result.ok(sessionDtoList);
    }
    
    
    /**
     * 添加新会话
     *
     * @param session
     * @return
     */
    @Override
    public Result addNewSession(Session session) {
        Long userId = UserHolder.getUser().getId();
        
        // 查询是否存在该用户是否有该session
        Session querySession = this.query()
                .eq("user_id", userId)
                .eq("session_title", session.getSessionTitle())
                .one();
        
        if (querySession != null) {
            return Result.fail("已存有该会话");
        }
        
        // 生成UUID
        String sessionId = UUID.randomUUID().toString();
        
        // 保存会话
        Session saveSession = new Session();
        saveSession.setUserId(userId);
        saveSession.setSessionId(sessionId);
        saveSession.setSessionTitle(session.getSessionTitle());
        this.save(saveSession);
        
        // 返回会话信息
        SessionDto sessionDto = new SessionDto();
        sessionDto.setSessionId(sessionId);
        sessionDto.setSessionTitle(session.getSessionTitle());
        
        return Result.ok(sessionDto);
    }
    
    
}
