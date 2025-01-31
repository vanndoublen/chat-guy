package xyz.vann.chat_guy;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Autowired
	VectorStore vectorStore;

	@Bean
	public ChatClient openAIChatClient(OpenAiChatModel chatModel){
		return ChatClient.builder(chatModel).defaultAdvisors(
				new MessageChatMemoryAdvisor(new InMemoryChatMemory()),
				new QuestionAnswerAdvisor(vectorStore)
		).build();
	}

	@Bean
	public ChatClient anthropicChatClient(AnthropicChatModel chatModel){
		return ChatClient.builder(chatModel).defaultAdvisors(
				new MessageChatMemoryAdvisor(new InMemoryChatMemory()),
				new QuestionAnswerAdvisor(vectorStore)
		).build();
	}
}
