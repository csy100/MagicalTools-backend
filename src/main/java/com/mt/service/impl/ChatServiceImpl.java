package com.mt.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mt.common.Result;
import com.mt.entity.Chat;
import com.mt.entity.Session;
import com.mt.mapper.ChatMapper;
import com.mt.service.ChatService;
import com.mt.service.SessionService;
import com.mt.utils.UserHolder;
import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.ChatGPTStream;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.Message;
import com.plexpt.chatgpt.listener.ConsoleStreamListener;
import com.plexpt.chatgpt.listener.SseStreamListener;
import com.plexpt.chatgpt.util.Proxys;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.net.Proxy;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Author: csy100
 * Date: 2023/7/28
 */
@Slf4j
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat> implements ChatService {
    
    @Resource
    private SessionService sessionService;
    
    /**
     * 初始化会话数据
     * @param sessionId
     * @return
     */
    @Override
    public Result initChatMessages(String sessionId) {
        
        Long userId = UserHolder.getUser().getId();
        
        // 对该会话进行更新
        Session session = sessionService.query()
                .eq("user_id", userId)
                .eq("session_id", sessionId).one();
        
        session.setUpdateTime(LocalDateTime.now());
        sessionService.updateById(session);
        
        
        QueryChainWrapper<Chat> queryChainWrapper = this.query()
                .eq("user_id", userId)
                .eq("session_id", sessionId)
                .orderByAsc("update_time")
                .orderByAsc("chat_type");
        
        List<Chat> list = queryChainWrapper.list();
        
        return Result.ok(list);
    }
    
    
    /**
     * 发送消息
     * @param chat
     * @return
     */
    @Override
    public SseEmitter sendMessage(Chat chat) {
        //国内需要代理 国外不需要
        Proxy proxy = Proxys.http("127.0.0.1", 7890);
        
        ChatGPTStream chatGPTStream = ChatGPTStream.builder()
                .timeout(600)
                .apiKey("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")
                .proxy(proxy)
                .apiHost("https://api.openai.com/")
                .build()
                .init();
        
        SseEmitter sseEmitter = new SseEmitter(-1L);
        
        SseStreamListener listener = new SseStreamListener(sseEmitter);
        Message message = Message.of(chat.getChatContent());
        
        Long userId = UserHolder.getUser().getId();
        listener.setOnComplate(msg -> {
            // 保存记录
            saveChat(userId, chat, msg);
        });
        chatGPTStream.streamChatCompletion(Arrays.asList(message), listener);
        
        return sseEmitter;
    }
    
    private void saveChat(Long userId, Chat chat, String msg) {
        // 保存prompt
        Chat userChat = new Chat();
        userChat.setUserId(userId);
        userChat.setSessionId(chat.getSessionId());
        userChat.setChatType(0);
        userChat.setChatContent(chat.getChatContent());
        this.save(userChat);
        
        Chat ansChat = new Chat();
        ansChat.setUserId(userId);
        ansChat.setSessionId(chat.getSessionId());
        ansChat.setChatType(1);
        ansChat.setChatContent(msg);
        this.save(ansChat);
    }
}
