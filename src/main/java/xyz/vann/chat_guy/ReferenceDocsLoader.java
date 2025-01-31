package xyz.vann.chat_guy;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReferenceDocsLoader {
    private static final Logger log = LoggerFactory.getLogger(ReferenceDocsLoader.class);
    private final JdbcClient jdbcClient;
    private final VectorStore vectorStore;
    private final FileService fileService;
    @Value("classpath:/docs/cs201.pdf")
    private Resource pdfResource;

    public ReferenceDocsLoader(JdbcClient jdbcClient, VectorStore vectorStore, FileService fileService) {
        this.jdbcClient = jdbcClient;
        this.vectorStore = vectorStore;
        this.fileService = fileService;
    }

    @PostConstruct
    public void init() {
        int len = fileService.getDocsDirFileNames().size();
        if (len == 0) {
            log.info("The docs directory is empty.");
        } else {
            log.info("Loading documents into Vector Store");
            for (int i = 0; i < len; i++) {
                String fileName = fileService.getDocsDirFileNames().get(i);
                log.info("Loading file: " + fileName);
                Resource resource = fileService.getResource(fileName);

                TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
                List<Document> documents = tikaDocumentReader.get();
                TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
                List<Document> splitDocuments = tokenTextSplitter.apply(documents);

                vectorStore.accept(splitDocuments);

                log.info("moving " + fileName + " from docs to processed directory");
                fileService.moveFileFromDocsToProcessed(fileName);
            }
        }
        log.info("Application is ready");
    }

//    @PostConstruct
//    public void init() {
//        Integer count = jdbcClient.sql("select count(*) from vector_store")
//                .query(Integer.class)
//                .single();
//
//        log.info("Current count of the Vector Store: {}", count);
//        if (count == 0) {
//            log.info("Loading Spring Boot Reference PDF into Vector Store");
//            TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(pdfResource);
//            List<Document> documents = tikaDocumentReader.get();
//            TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
//            List<Document> splitDocuments = tokenTextSplitter.apply(documents);
//
//            vectorStore.accept(splitDocuments);
//
//            log.info("Application is ready");
//        }
//    }
}
