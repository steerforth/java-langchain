package com.steer.langchain;

import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByRegexSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class JavaLangchainApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaLangchainApplication.class, args);
    }


    /**
     * 加载本地知识库
     * @param embeddingStore
     * @param qwenEmbeddingModel
     * @return
     */
    @Bean
    CommandLineRunner initStore(EmbeddingStore embeddingStore, QwenEmbeddingModel qwenEmbeddingModel) {
        return args -> {
            Document document = ClassPathDocumentLoader.loadDocument("rag/test.txt");
            DocumentByRegexSplitter splitter = new DocumentByRegexSplitter(
                    "\\n\\d+\\.",
                    "\n",
                    80, //每段最长字数
                    20);//自然语言最大重叠字数
            List<TextSegment> segments = splitter.split(document);//分割

            List<Embedding> embeddingList = qwenEmbeddingModel.embedAll(segments).content();
            embeddingStore.addAll(embeddingList,segments);//存入本地
        };
    }
}
