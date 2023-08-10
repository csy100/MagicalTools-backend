package com.mt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mt.common.Result;
import com.mt.entity.Chat;
import com.mt.entity.ChatRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Author: csy100
 * Date: 2023/7/28
 */
public interface ChatService extends IService<Chat> {
    SseEmitter sendMessage(ChatRequest chatRequest);
    
    Result initChatMessages(String sessionId);
    
}
