package xyz.vann.chat_guy;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/claude")
public class AnthropicChatController {
    private final ChatClient chatClient;

    @Autowired
    public AnthropicChatController(@Qualifier("anthropicChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    @GetMapping("/chat")
    public String Claude(){
        return chatClient.prompt()
                .user("tell me an interesting fact about anthropic")
                .call()
                .content();
    }

    @PostMapping("/chat/stream")
    public Flux<Map<String, String>> chatStream(@RequestBody Map<String, String> request){
        try{
            String userMessage = request.get("message");

            String systemPrompt = "You are \"Claude Taobao\" and  a programming assistant.\n" +
                    "Always tell the amount of the input/output tokens used at the end of the response." +
                    "Follow these rules for formatting:\n" +
                    "1. Code: Use ```language blocks\n" +
                    "2. Lists: Put blank lines between items\n" +
                    "3. Math: Do not use code block for math";

            Flux<Map<String, String>> map = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userMessage)
                    .stream()
                    .content()
                    .map(content -> Map.of(
                            "message", content,
                            "status", "pending"
                    ))
                    .concatWith(Flux.just(Map.of(
                            "message", "",
                            "status", "complete"
                    )))
                    .onErrorResume(e -> {
                        e.printStackTrace();
                        return Flux.just(Map.of(
                                "message", "An error occurred: " + e.getMessage(),
                                "status", "error"
                        ));
                    });

            return map;

        } catch (Exception e){
            e.printStackTrace();
            return Flux.just(Map.of(
                    "message", "An error occurred: " + e.getMessage(),
                    "status", "error"
            ));
        }
    }
}
