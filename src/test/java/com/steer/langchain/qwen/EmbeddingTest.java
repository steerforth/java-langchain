package com.steer.langchain.qwen;

import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.jupiter.api.Test;

public class EmbeddingTest {
    private static final String API_KEY = "sk-6b42dfa4cb14468e920d85b80908df70";
    @Test
    public void testEmbedding() {
        QwenEmbeddingModel embeddingModel = QwenEmbeddingModel.builder()
                .apiKey(API_KEY)
                .modelName("text-embedding-v2")
                .build();
        Response<Embedding> embed = embeddingModel.embed("你好，我是steer");
        System.out.println(embed.content().toString());
        System.out.println(embed.content().vector().length);
    }

    @Test
    public void testEmbedding2() {
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        QwenEmbeddingModel model = QwenEmbeddingModel.builder()
                .apiKey(API_KEY)
                .build();

        //利用向量模型向量化，然后存储到向量数据库
        TextSegment segment1 = TextSegment.from("""
                预定航班:
                -通过我们的网站或移动应用程序预定
                -预定时需要全额付款
                -确保个人信息(姓名，ID等)的准确性，因为更正会产生25元的费用
                """);
        Embedding content1 = model.embed(segment1).content();
        embeddingStore.add(content1,segment1);

        TextSegment segment2 = TextSegment.from("""
                取消预定:
                -最晚在航班起飞前48小时取消。
                -取消费用：经济舱75元，豪华舱50元，商务舱25元
                -退款将在7个工作日内处理
                """);
        Embedding content2 = model.embed(segment2).content();
        embeddingStore.add(content2,segment2);

        //用户查询的内容
        Embedding queryContent = model.embed("退票要多少钱").content();
        EmbeddingSearchRequest build = EmbeddingSearchRequest.builder().queryEmbedding(queryContent)
                .maxResults(1)  // 返回结果数量

                .minScore(0.5) // 满足匹配的最低的结果分数
                .build();

        EmbeddingSearchResult<TextSegment> result = embeddingStore.search(build);
        result.matches().forEach(match -> {
            System.out.println(match.score());//0.745
            System.out.println(match.embedded().text());
        });
    }
}
