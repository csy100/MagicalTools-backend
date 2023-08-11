package com.mt.utils;

import com.alibaba.fastjson.JSON;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import com.plexpt.chatgpt.util.SseHelper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 描述：OpenAIEventSourceListener
 *
 * @author https:www.unfbx.com
 * @date 2023-02-22
 */
@Slf4j
@RequiredArgsConstructor
public class GPTEventSourceListener extends EventSourceListener {

    final SseEmitter sseEmitter;

    String last = "";
    @Setter
    Consumer<String> onComplate = s -> {
    };


    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(EventSource eventSource, Response response) {

    }

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        if (data.equals("[DONE]")) {
//            sseEmitter.send("[DONE]");
            onComplate.accept(last);
            SseHelper.complete(sseEmitter);
            return;
        }

        ChatCompletionResponse completionResponse = JSON.parseObject(data,
                ChatCompletionResponse.class); // 读取Json
        Message delta = completionResponse.getChoices().get(0).getDelta();
        String text = delta.getContent();
        if (text != null) {
            last += text;
            sseEmitter.send(text);
        }
    }


    @Override
    public void onClosed(EventSource eventSource) {
        SseHelper.complete(sseEmitter);
    }


    @SneakyThrows
    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        if (Objects.isNull(response)) {
            return;
        }
        ResponseBody body = response.body();
        String errorMsg = "-1";
        if (Objects.nonNull(body)) {
            String content = body.string();
            log.error("OpenAI  sse连接异常data：{}，异常：{}", content, t);
            errorMsg = content;
        } else {
            log.error("OpenAI  sse连接异常data：{}，异常：{}", response, t);
            errorMsg = String.valueOf(response);
        }
        sseEmitter.send("\n\n\n ### [接收消息处理异常，响应中断，本次回答不扣费]");
        eventSource.cancel();
        SseHelper.complete(sseEmitter);
    }
}
