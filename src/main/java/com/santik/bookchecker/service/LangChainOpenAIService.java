package com.santik.bookchecker.service;

import com.santik.bookchecker.model.Questions;
import com.santik.bookchecker.model.ai.Message;
import com.santik.bookchecker.service.parsers.JsonResponseParser;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LangChainOpenAIService {

    private final ChatLanguageModel chatLanguageModel;
    private final JsonResponseParser responseParser;

    public Questions callOpenAI(Message message) {

        ChatRequest chatRequest = ChatRequest.builder()
                .messages(
                        new SystemMessage(message.getSystemMessage()),
                        new UserMessage(message.getUserMessage())
                )
                .build();

        ChatResponse chat = chatLanguageModel.chat(chatRequest);

        log.info("Response: {}", chat);

        return responseParser.parseResponse(chat.aiMessage().text());
    }
}