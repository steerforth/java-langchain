package com.steer.langchain.qwen;

import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByCharacterSplitter;
import dev.langchain4j.data.document.splitter.DocumentByRegexSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 支持text,pdf ,poi等
 */
public class DocumentSplitTest {
    private static final String API_KEY = "sk-6b42dfa4cb14468e920d85b80908df70";

    /**
     * 不好用
     */
    @Test
    public void testByCharacterSplitter(){
        Document document = ClassPathDocumentLoader.loadDocument("rag/test.txt");
        DocumentByCharacterSplitter splitter = new DocumentByCharacterSplitter(
                50, //每段最长字数
                10);//自然语言最大重叠字数
        List<TextSegment> segments = splitter.split(document);
        for (TextSegment segment : segments) {
            System.out.println(segment.text());
            System.out.println("-----------------------");
        }
    }

    /**
     * 适用结构化的
     */
    @Test
    public void testByRegexSplitter(){
        Document document = ClassPathDocumentLoader.loadDocument("rag/test.txt");
        DocumentByRegexSplitter splitter = new DocumentByRegexSplitter(
                "\\n\\d+\\.",
                "\n",
                80, //每段最长字数
                20);//自然语言最大重叠字数
        List<TextSegment> segments = splitter.split(document);
        for (TextSegment segment : segments) {
            System.out.println(segment.text());
            System.out.println("-----------------------");
        }
    }

    @Test
    public void test(){
        QwenEmbeddingModel model = QwenEmbeddingModel.builder()
                .apiKey(API_KEY)
                .build();

        Document document = ClassPathDocumentLoader.loadDocument("rag/test.txt");
        DocumentByRegexSplitter splitter = new DocumentByRegexSplitter(
                "\\n\\d+\\.",
                "\n",
                80, //每段最长字数
                20);//自然语言最大重叠字数
        List<TextSegment> segments = splitter.split(document);//分割

        List<Embedding> embeddingList = model.embedAll(segments).content();
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.addAll(embeddingList,segments);//存入本地

        //用户查询的内容
        Embedding queryContent = model.embed("退票要多少钱").content();
        EmbeddingSearchRequest build = EmbeddingSearchRequest.builder().queryEmbedding(queryContent)
//                .maxResults(1)  // 返回结果数量
//                .minScore(0.5) // 满足匹配的最低的结果分数
                .build();

        EmbeddingSearchResult<TextSegment> result = embeddingStore.search(build);
        result.matches().forEach(match -> {
            System.out.println(match.score());//0.745
            System.out.println(match.embedded().text());
            System.out.println(">>>>>>>>>>>>>>");
        });
    }
}
