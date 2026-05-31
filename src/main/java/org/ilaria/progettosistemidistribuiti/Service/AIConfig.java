package org.ilaria.progettosistemidistribuiti.Service;


import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import dev.langchain4j.model.openai.OpenAiChatModel;

@Configuration
public class AIConfig {

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .apiKey("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")
                .modelName("llama-3.1-8b-instant")
                .build();
    }
}

