package com.mt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mt.common.Result;
import com.mt.entity.Chat;

/**
 * Author: csy100
 * Date: 2023/7/28
 */
public interface ChatService extends IService<Chat> {
    Result sendMessage(Chat chat);
    
    Result initChatMessages(String sessionId);
    
}
