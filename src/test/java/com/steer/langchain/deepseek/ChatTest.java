package com.steer.langchain.deepseek;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.Test;

public class ChatTest {
    private static final String API_KEY = "sk-301de13bdd1e470dba299bb434c2ad73";
    @Test
    public void testChat() {
        ChatLanguageModel model = OpenAiChatModel.builder()
                        .baseUrl("https://api.deepseek.com")
                .apiKey(API_KEY)
                .modelName("deepseek-chat")
                .build();
//        ChatMessage message = new Cha
        ChatRequest request = new ChatRequest.Builder().messages().build();
        ChatResponse res = model.chat(request);
        System.out.println(res.aiMessage().text());
    }
}
