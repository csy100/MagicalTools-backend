package com.mt.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mt.common.Result;
import com.mt.entity.Chat;
import com.mt.entity.ChatRequest;
import com.mt.entity.Session;
import com.mt.mapper.ChatMapper;
import com.mt.service.ChatService;
import com.mt.service.SessionService;
import com.mt.utils.GPTEventSourceListener;
import com.mt.utils.UserHolder;
import com.plexpt.chatgpt.ChatGPTStream;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.Message;
import com.plexpt.chatgpt.listener.SseStreamListener;
import com.plexpt.chatgpt.util.Proxys;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.net.Proxy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.mt.utils.RedisConstants.CHAT_USER_KEY;
import static com.mt.utils.RedisConstants.CHAT_USER_TTL;

/**
 * Author: csy100
 * Date: 2023/7/28
 */
@Slf4j
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat> implements ChatService {
    
    @Resource
    private SessionService sessionService;
    
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    
    /**
     * 初始化会话数据
     *
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
        
//        String chatJson = stringRedisTemplate.opsForValue().get(CHAT_USER_KEY + userId + sessionId);
//        if (StrUtil.isNotBlank(chatJson)) {
//            List<Chat> list = JSONUtil.toList(chatJson, Chat.class);
//            return Result.ok(list);
//        }
        
        QueryChainWrapper<Chat> queryChainWrapper = this.query()
                .eq("user_id", userId)
                .eq("session_id", sessionId)
                .orderByAsc("update_time")
                .orderByAsc("chat_type");
        
        List<Chat> list = queryChainWrapper.list();
        
//        stringRedisTemplate.opsForValue().set(CHAT_USER_KEY + userId + sessionId,
//                JSONUtil.toJsonStr(list), CHAT_USER_TTL, TimeUnit.DAYS);
        
        return Result.ok(list);
    }
    
    
    /**
     * 发送消息
     * @return
     */
    @Override
    public SseEmitter sendMessage(ChatRequest chatRequest) {
        Chat chat = chatRequest.getChat();
        ChatRequest.Setting setting = chatRequest.getSetting();
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
        
        GPTEventSourceListener listener = new GPTEventSourceListener(sseEmitter);
        if (setting == null) {
            setting = new ChatRequest.Setting("", "gpt-3.5-turbo",
                            0.0, 0, 1024, 0.0,0.0 );
        }
        // 添加预设
        Message system = Message.ofSystem(setting.getRolePlay());
        // 添加系统消息
        List<Message> AllMessage = new ArrayList<>();
        AllMessage.add(system);
        getComments(AllMessage, setting.getN() * 2, chat.getSessionId());
        // 添加用户发送的消息
        AllMessage.add(new Message("user", chat.getChatContent()));
        ChatCompletion chatCompletion;
        Long userId = UserHolder.getUser().getId();
        listener.setOnComplate(msg -> {
            // 保存记录
            saveChat(userId, chat, msg);
        });
        chatCompletion = ChatCompletion.builder()
                .messages(AllMessage)
                .model("gpt-3.5-turbo")
                .temperature(setting.getTemperature()) // 对话温度 使用什么取样温度，0到2之间。越高越奔放。越低越保守
                .frequencyPenalty(setting.getFrequencyPenalty()) // 控制字符的重复度
                .presencePenalty(setting.getPresencePenalty()) // 控制主题的重复度
                .user(String.valueOf(userId)) // 用户唯一标识
                .build();

        chatGPTStream.streamChatCompletion(chatCompletion, listener);
        return sseEmitter;
    }

    private void getComments(List<Message> allMessage, int n, String sessionId) {
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("session_id", sessionId);
        queryWrapper.orderByAsc("create_time");
        List<Chat> allComments = this.list(queryWrapper);

        int t = allComments.size();
        if (t <= n) {
            for (int i = 0; i < t; i += 2) {
                Message tempUserMessage = Message.of(allComments.get(i).getChatContent());
                Message tempGptMessage = Message.ofAssistant(allComments.get(i + 1).getChatContent());
                allMessage.add(tempUserMessage);
                allMessage.add(tempGptMessage);
            }
            return ;
        }
        for (int i = t - n; i <= t - 1; i += 2) {
            Message tempUserMessage = Message.of(allComments.get(i).getChatContent());
            Message tempGptMessage = Message.ofAssistant(allComments.get(i + 1).getChatContent());
            allMessage.add(tempUserMessage);
            allMessage.add(tempGptMessage);
        }
        return ;
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
