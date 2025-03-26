package com.steer.langchain.controller;

import com.steer.langchain.config.AiConfig;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.TokenStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/ai")
public class ChatController {

    @Autowired
    private QwenChatModel qwenChatModel;

    @Autowired
    private QwenStreamingChatModel qwenStreamingChatModel;

    @Autowired
    private AiConfig.Assistant assistant;

    @Autowired
    private AiConfig.AssistantUnique assistantUnique;

    @Autowired
    private AiConfig.ToolAssistant toolAassistant;
    /**
     * 普通对话
     * @param msg
     * @return
     */
    @RequestMapping("/chat")
    public String chat(@RequestParam(defaultValue = "你是谁") String msg) {
        List<ChatMessage> messages = new LinkedList<>();
        messages.add(SystemMessage.from("你是Jack,你喜欢回答大家的问题，并总是以“谢谢”说话结尾"));
        messages.add(UserMessage.from(msg));
        ChatRequest chatRequest = new ChatRequest.Builder().messages(messages).build();
        ChatResponse chatResponse = qwenChatModel.chat(chatRequest);
        return chatResponse.aiMessage().text();
    }

    /**
     * 流式响应，无记忆功能
     * @param msg
     * @return
     */
    @RequestMapping(value = "/stream",produces = "text/stream;charset=UTF-8")
    public Flux<String> stream(@RequestParam(defaultValue = "你是谁") String msg) {
        Flux<String> flux = Flux.create(f -> {
            qwenStreamingChatModel.chat(msg, new StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String s) {
                    f.next(s);
                }

                @Override
                public void onCompleteResponse(ChatResponse chatResponse) {
                    f.complete();
                }

                @Override
                public void onError(Throwable throwable) {
                    f.error(throwable);
                }
            });
        });
        return flux;
    }

    /**
     * 记忆对话
     * @param msg
     * @return
     */
    @RequestMapping(value = "/memoryStream",produces = "text/stream;charset=UTF-8")
    public Flux<String> memoryStream(@RequestParam(defaultValue = "你是谁") String msg) {

        TokenStream stream = assistant.stream(msg);
        Flux<String> flux = Flux.create(f -> {
            stream.onPartialResponse(s -> f.next(s))
                    .onCompleteResponse(chatResponse -> f.complete())
                    .onError(throwable -> f.error(throwable))
                    .start();
        });
        return flux;
    }

    /**
     * 记忆对话  隔离用户
     * @param msg
     * @return
     */
    @RequestMapping(value = "/memoryStreamUnique",produces = "text/stream;charset=UTF-8")
    public String memoryStreamUnique(@RequestParam(defaultValue = "你是谁") String msg,Integer userId) {
        String chat = assistantUnique.chat(userId, msg);
        return chat;
    }

    /**
     * 调用function-call
     * @param msg
     * @return
     */
    @RequestMapping(value = "/toolStream",produces = "text/stream;charset=UTF-8")
    public Flux<String> toolStream(@RequestParam(defaultValue = "你是谁") String msg,Integer userId) {
        TokenStream stream = toolAassistant.stream(userId,msg, LocalDate.now().toString());
        Flux<String> flux = Flux.create(f -> {
            stream.onPartialResponse(s -> f.next(s))
                    .onCompleteResponse(chatResponse -> f.complete())
                    .onError(throwable -> f.error(throwable))
                    .start();
        });
        return flux;
    }
}
