package com.mt.controller;

import com.mt.common.Result;
import com.mt.entity.Chat;
import com.mt.entity.ChatRequest;
import com.mt.service.ChatService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Author: csy100
 * Date: 2023/7/25
 */
@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {
    
    @Resource
    private ChatService chatService;
    
    /**
     * 初始化会话数据
     * @param sessionId
     * @return
     */
    @GetMapping("/init/{sessionId}")
    public Result initChatMessages(@PathVariable("sessionId") String sessionId) {
        return chatService.initChatMessages(sessionId);
    }
    
    /**
     * 测试
     * @param chatRequest
     * @return
     */
    @PostMapping("/prompt")
    public SseEmitter sendMessage(@RequestBody ChatRequest chatRequest) {
        return chatService.sendMessage(chatRequest);
    }
    
}
