//package xyz.vann.chat_guy;
//
//
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.prompt.Prompt;
//import org.springframework.ai.chat.prompt.PromptTemplate;
//import org.springframework.ai.document.Document;
//import org.springframework.ai.vectorstore.SearchRequest;
//import org.springframework.ai.vectorstore.VectorStore;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.Resource;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Vector;
//
//@RestController
//public class ThemeController {
//    private final ChatClient chatClient;
//    private final VectorStore vectorStore;
//
//    public ThemeController(@Qualifier("openAIChatClient") ChatClient chatClient, VectorStore vectorStore) {
//        this.chatClient = chatClient;
//        this.vectorStore = vectorStore;
//    }
//
//    @Value("classpath:/prompts/rag-prompt-template.st")
//    private Resource ragPromptTemplate;
//
//    @GetMapping("/theme")
//    public String theme(@RequestParam(value = "message", defaultValue = "explain keyframes in hypno css class, what are they doing") String message){
//        List<Document> similarDocuments = vectorStore.similaritySearch(SearchRequest.builder().query(message).build());
//        List<String> contentList = similarDocuments.stream().map(Document::getContent).toList();
//        PromptTemplate promptTemplate = new PromptTemplate(ragPromptTemplate);
//        Map<String, Object> promptParameters = new HashMap<>() ;
//        promptParameters.put("input", message);
//        promptParameters.put("documents", String.join("\n", contentList));
//        Prompt prompt = promptTemplate.create(promptParameters);
//
//        return chatClient.prompt(prompt).call().content();
//    }
//}
