package com.afis.fraud.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.afis.fraud.ai.FraudAssistant;
import com.afis.fraud.ai.FraudInvestigationTools;

import dev.langchain4j.service.AiServices;

@Configuration
public class AIConfig {

    @Bean
    public ChatModel chatModel(
            @Value("${afis.ai.api-key}") String apiKey,
            @Value("${afis.ai.model}") String model) {

        return OpenAiChatModel.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .apiKey(apiKey)
                .modelName(model)
                .temperature(0.0)
                .build();
    }
    // @Bean
    // public ChatModel chatModel(
    // @Value("${afis.ai.api-key}") String apiKey,
    // @Value("${afis.ai.model}") String model) {

    // System.out.println("API KEY PRESENT: " + (apiKey != null &&
    // !apiKey.isBlank()));
    // System.out.println("API KEY PREFIX: " +
    // (apiKey != null && apiKey.length() >= 8
    // ? apiKey.substring(0, 8)
    // : "INVALID"));

    // System.out.println("MODEL: " + model);

    // return OpenAiChatModel.builder()
    // .baseUrl("https://api.groq.com/openai/v1")
    // .apiKey(apiKey)
    // .modelName(model)
    // .temperature(0.0)
    // .build();
    // }

    @Bean
    public FraudAssistant fraudAssistant(
            ChatModel chatModel,
            FraudInvestigationTools fraudInvestigationTools) {

        return AiServices.builder(FraudAssistant.class)
                .chatModel(chatModel)
                .tools(fraudInvestigationTools)
                .build();
    }
}