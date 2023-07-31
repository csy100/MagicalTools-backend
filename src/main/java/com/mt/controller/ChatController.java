package com.mt.controller;

import com.mt.common.Result;
import com.mt.entity.Chat;
import com.mt.service.ChatService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    
    @GetMapping("/init/{sessionId}")
    public Result initChatMessages(@PathVariable("sessionId") String sessionId) {
        return chatService.initChatMessages(sessionId);
    }
    
    /**
     * 测试
     * @param chat
     * @return
     */
    @PostMapping("/prompt")
    public Result sendMessage(@RequestBody Chat chat) {
        return chatService.sendMessage(chat);
    }
    
    
    
//    /**
//     * 测试
//     * @param prompt
//     * @return
//     */
//    @GetMapping("/sse")
//    public SseEmitter sseEmitter(String prompt) {
//        log.info("test测试");
//        //国内需要代理 国外不需要
//        Proxy proxy = Proxys.http("127.0.0.1", 7890);
//
//        ChatGPTStream chatGPTStream = ChatGPTStream.builder()
//                .timeout(600)
//                .apiKey("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")
//                .proxy(proxy)
//                .apiHost("https://api.openai.com/")
//                .build()
//                .init();
//
//        SseEmitter sseEmitter = new SseEmitter(-1L);
//
//        SseStreamListener listener = new SseStreamListener(sseEmitter);
//        Message message = Message.of(prompt);
//
//        listener.setOnComplate(msg -> {
//            //回答完成，可以做一些事情
//        });
//        chatGPTStream.streamChatCompletion(Arrays.asList(message), listener);
//
//        return sseEmitter;
//    }
    
}
