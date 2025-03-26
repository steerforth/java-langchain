package com.steer.langchain.qwen;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.community.model.dashscope.WanxImageModel;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
//import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ChatTest {
    private static final String API_KEY = "sk-6b42dfa4cb14468e920d85b80908df70";
    @Test
    public void testChat() throws IOException {
        StreamingChatLanguageModel model = QwenStreamingChatModel.builder()
                        .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .apiKey(API_KEY)
                .modelName("qwq-plus")
                .build();
//        ChatMessageType d;
//        ChatRequest chatRequest = ChatRequest.withMessages(
//                dev.langchain4j.data.message.AiMessage.withContent("你好，你是谁？")
//        );
//        model.chat("你好，你是谁？", new StreamingChatResponseHandler() {
//            @Override
//            public void onPartialResponse(String s) {
//                System.out.println(s);
//            }
//
//            @Override
//            public void onCompleteResponse(ChatResponse chatResponse) {
//                System.out.println(chatResponse);
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//                throwable.printStackTrace();
//            }
//        });

        System.in.read();
    }

    @Test
    public void testImage() {
        ImageModel model = WanxImageModel.builder()
                //.baseUrl("https://dashscope.aliyuncs.com/api/v1/services/aigc/text2image/image-synthesis")
                .apiKey(API_KEY)
                .modelName("wanx2.1-t2i-turbo")
                .build();
        Response<Image> res = model.generate("美女");
        System.out.println(res.content().url());
    }
}
