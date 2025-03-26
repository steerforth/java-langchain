package com.steer.langchain.config;

import com.steer.langchain.functionCall.ToolService;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    /**
     * 普通对话
     */
    public interface Assistant{
        String chat(String message);
        //流式响应
        TokenStream stream(String message);
    }

    @Bean
    public Assistant assistant(ChatLanguageModel chatLanguageModel, StreamingChatLanguageModel streamingChatLanguageModel){
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        //创建动态代理对象
        Assistant assistant = AiServices.builder(Assistant.class).chatLanguageModel(chatLanguageModel)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .chatMemory(chatMemory)
                .build();
        return assistant;
    }

    /**
     * 带记忆对话
     */
    public interface AssistantUnique{
        String chat(@MemoryId int memoryId,@UserMessage String msg);
        //流式响应
        TokenStream stream(@MemoryId int memoryId,@UserMessage String msg);
    }

    @Bean
    public AssistantUnique assistantUnique(ChatLanguageModel chatLanguageModel, StreamingChatLanguageModel streamingChatLanguageModel){
        //可自己实现ChatMemoryStore接口，自定义将记忆对话存储在对应的数据库中
        //        ChatMemoryProvider provider = memoryId->MessageWindowChatMemory.builder()
//                .maxMessages(10)
//                .id(memoryId)
//                .chatMemoryStore()   ===>
//                .build();

        //创建动态代理对象 对话存储在内存中
        AssistantUnique assistant = AiServices.builder(AssistantUnique.class).chatLanguageModel(chatLanguageModel)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .chatMemoryProvider(memoryId->MessageWindowChatMemory.builder().maxMessages(10).id(memoryId).build())
                .build();
        return assistant;
    }

    /**
     * 调用functionCall
     */
    public interface ToolAssistant{
        //流式响应   cur_date自定义变量填充
        @SystemMessage("""
                你是Steer航空公司的客户聊天代理。请以友好、乐于助人的方式来回复。
                在提供有关预定或取消预订的信息之前，您必须始终从用户处获取一下信息:预定号、客户姓名。
                今天的日期是{{cur_date}},
                你喜欢回答大家的问题，并总是以“谢谢”说话结尾
                """)
        TokenStream stream(@MemoryId int memoryId,@UserMessage String msg, @V("cur_date") String curDate);
    }

    @Bean
    public ToolAssistant toolAassistant(ChatLanguageModel chatLanguageModel, StreamingChatLanguageModel streamingChatLanguageModel, ToolService toolService){
        ToolAssistant assistant = AiServices.builder(ToolAssistant.class).chatLanguageModel(chatLanguageModel)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .tools(toolService) //添加工具function-call
                .chatMemoryProvider(memoryId->MessageWindowChatMemory.builder().maxMessages(10).id(memoryId).build())
                .build();
        return assistant;
    }


}
