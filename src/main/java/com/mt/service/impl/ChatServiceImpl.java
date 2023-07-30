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
import com.plexpt.chatgpt.util.Proxys;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.Proxy;
import java.time.LocalDateTime;
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
    
    
    @Override
    public Result sendMessage(Chat chat) {
        //国内需要代理
        Proxy proxy = Proxys.http("127.0.0.1", 7890);
        ChatGPT chatGPT = ChatGPT.builder()
                .apiKey("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")
                .proxy(proxy)
                .apiHost("https://api.openai.com/") //反向代理地址
                .build()
                .init();
        String res = chatGPT.chat(chat.getChatContent());
        
        // 用户记录保存
        Chat userChat = new Chat();
        userChat.setUserId(UserHolder.getUser().getId());
        userChat.setSessionId(chat.getSessionId());
        userChat.setChatType(0);
        userChat.setChatContent(chat.getChatContent());
        this.save(userChat);
        
        // chatgpt对话保存
        Chat resChat = new Chat();
        resChat.setUserId(UserHolder.getUser().getId());
        resChat.setSessionId(chat.getSessionId());
        resChat.setChatType(1);
        resChat.setChatContent(res);
        this.save(resChat);
        
        return Result.ok(resChat);
    }
}
