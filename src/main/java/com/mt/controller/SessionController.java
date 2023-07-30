package com.mt.controller;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.mt.common.Result;
import com.mt.entity.Session;
import com.mt.service.SessionService;
import com.mt.utils.UserHolder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Author: csy100
 * Date: 2023/7/26
 */
@Slf4j
@RestController
@RequestMapping("/session")
public class SessionController {
    
    @Resource
    private SessionService sessionService;
    
    /**
     * 初始化session数据
     *
     * @return
     */
    @GetMapping("/init")
    public Result sessionInit() {
        return sessionService.sessionInit();
    }
    
    /**
     * 添加新会话
     *
     * @param session
     * @return
     */
    @PostMapping("/add")
    public Result addNewSession(@RequestBody Session session) {
        return sessionService.addNewSession(session);
    }
    
    /**
     * 编辑会话信息
     *
     * @param session
     * @return
     */
    @PostMapping("/updateSession")
    public Result updateSession(@RequestBody Session session) {
        Session querySession = sessionService.query().eq("session_id", session.getSessionId()).one();
        if (querySession == null) {
            return Result.fail("该会话不存在");
        }
        querySession.setSessionTitle(session.getSessionTitle());
        querySession.setUpdateTime(LocalDateTime.now());
        sessionService.updateById(querySession);
        
        return Result.ok("信息更改成功");
    }
    
    /**
     * 删除会话信息
     *
     * @param sessionId
     * @return
     */
    @DeleteMapping("/deleteSession/{sessionId}")
    public Result deleteSession(@PathVariable("sessionId") String sessionId) {
        Session querySession = sessionService.query().eq("session_id", sessionId).one();
        if (querySession == null) {
            return Result.fail("该会话不存在");
        }
        
        sessionService.removeById(querySession);
        return Result.ok("会话删除成功");
    }
    
    /**
     * 删除所有会话信息
     *
     * @return
     */
    @DeleteMapping("/deleteAllSession")
    public Result deleteAllSessions() {
        Long userId = UserHolder.getUser().getId();
        QueryChainWrapper<Session> sessionQueryChainWrapper = sessionService.query().eq("user_id", userId);
        List<Session> sessionList = sessionQueryChainWrapper.list();
        for (Session session : sessionList) {
            sessionService.removeById(session);
        }
        return Result.ok("会话删除成功");
    }
    
    
}
