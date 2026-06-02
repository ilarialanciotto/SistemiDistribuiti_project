package org.ilaria.progettosistemidistribuiti.Service.AI;


import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import dev.langchain4j.model.openai.OpenAiChatModel;

@Configuration
public class AIConfig {

    @Value("${ai.setup.key}")
    private String AIkey;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .apiKey(AIkey)
                .modelName("llama-3.1-8b-instant")
                .build();
    }
}

