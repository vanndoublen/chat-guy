package xyz.vann.chat_guy;

import jakarta.annotation.PostConstruct;
import org.jsoup.Jsoup;
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

import java.nio.charset.StandardCharsets;
import java.util.Collections;
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
        List<String> fileNames = fileService.getDocsDirFileNames();
        int len = fileNames.size();

        if (len == 0) {
            log.info("The docs directory is empty.");
        } else {
            log.info("Loading documents into Vector Store");
            for (String fileName : fileNames) {
                try {
                    log.info("Loading file: " + fileName);
                    Resource resource = fileService.getResource(fileName);

                    List<Document> documents;
                    if (fileName.endsWith(".html")) {
                        // Use Jsoup for HTML files
                        String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                        org.jsoup.nodes.Document doc = Jsoup.parse(html);
                        // Preserve formatting and structure
                        doc.outputSettings().prettyPrint(false);
                        String cleanHtml = doc.html();
                        documents = Collections.singletonList(new Document(cleanHtml));
                    } else {
                        // Use Tika for non-HTML files
                        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
                        documents = tikaDocumentReader.get();
                    }

                    TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
                    List<Document> splitDocuments = tokenTextSplitter.apply(documents);

                    vectorStore.accept(splitDocuments);

                    log.info("moving " + fileName + " from docs to processed directory");
                    fileService.moveFileFromDocsToProcessed(fileName);
                } catch (Exception e) {
                    log.error("Error processing file: " + fileName, e);
                }
            }
        }
        log.info("Application is ready");
    }

//    @PostConstruct
//    public void init() {
//        // Get the list of files once at the start
//        List<String> fileNames = fileService.getDocsDirFileNames();
//        int len = fileNames.size();
//
//        if (len == 0) {
//            log.info("The docs directory is empty.");
//        } else {
//            log.info("Loading documents into Vector Store");
//            for (String fileName : fileNames) {  // Use the saved list
//                try {
//                    log.info("Loading file: " + fileName);
//                    Resource resource = fileService.getResource(fileName);
//
//                    TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
//                    List<Document> documents = tikaDocumentReader.get();
//                    TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
//                    List<Document> splitDocuments = tokenTextSplitter.apply(documents);
//
//                    vectorStore.accept(splitDocuments);
//
//                    log.info("moving " + fileName + " from docs to processed directory");
//                    fileService.moveFileFromDocsToProcessed(fileName);
//                } catch (Exception e) {
//                    log.error("Error processing file: " + fileName, e);
//                    // Decide whether to continue with other files or throw
//                }
//            }
//        }
//        log.info("Application is ready");
//    }

//    @PostConstruct
//    public void init() {
//        int len = fileService.getDocsDirFileNames().size();
//        if (len == 0) {
//            log.info("The docs directory is empty.");
//        } else {
//            log.info("Loading documents into Vector Store");
//            for (int i = 0; i < len; i++) {
//                String fileName = fileService.getDocsDirFileNames().get(i);
//                log.info("Loading file: " + fileName);
//                Resource resource = fileService.getResource(fileName);
//
//                TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
//                List<Document> documents = tikaDocumentReader.get();
//                TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
//                List<Document> splitDocuments = tokenTextSplitter.apply(documents);
//
//                vectorStore.accept(splitDocuments);
//
//                log.info("moving " + fileName + " from docs to processed directory");
//                fileService.moveFileFromDocsToProcessed(fileName);
//            }
//        }
//        log.info("Application is ready");
//    }

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
