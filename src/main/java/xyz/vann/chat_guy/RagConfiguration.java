//package xyz.vann.chat_guy;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.ai.document.Document;
//import org.springframework.ai.embedding.EmbeddingModel;
//import org.springframework.ai.reader.tika.TikaDocumentReader;
//import org.springframework.ai.transformer.splitter.TokenTextSplitter;
//import org.springframework.ai.vectorstore.SearchRequest;
//import org.springframework.ai.vectorstore.SimpleVectorStore;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.Resource;
//
//import java.io.File;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//
//@Configuration
//public class RagConfiguration {
//    private static final Logger log = LoggerFactory.getLogger(RagConfiguration.class);
//    @Value("classpath:/docs/theme.css")
//    private Resource resource;
//
//    @Value("vectorstore.json")
//    private  String vectorstoreName;
//
//    @Bean
//    SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel){
//        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel).build();
//        File vectorestoreFile = getVectorStoreFile();
//        if (vectorestoreFile.exists()){
//            log.info("vector store exists");
//            simpleVectorStore.load(vectorestoreFile);
//        } else {
//            log.info("Vector Store file does not exist, loading document");
//            TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
//            List<Document> documents = tikaDocumentReader.get();
//            TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
//            List<Document> splitDocuments = tokenTextSplitter.apply(documents);
//
//            simpleVectorStore.add(splitDocuments);
//            simpleVectorStore.save(vectorestoreFile);
//
//        }
//
//        return simpleVectorStore;
//    }
//
//
//    private File getVectorStoreFile(){
//        Path path = Paths.get("src", "main", "resources", "data");
//        String absolutePath =  path.toFile().getAbsolutePath() + "/" + vectorstoreName;
//        return new File(absolutePath);
//    }
//}
