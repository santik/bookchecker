package com.santik.bookchecker.service.parsers;

import com.santik.bookchecker.model.Questions;
import com.santik.bookchecker.service.ResponseParser;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LangChainResponseParser implements ResponseParser {

    interface QuestionsExtractor {
        @UserMessage("Extract questions from {{it}}")
        Questions extractQuestions(String text);
    }

    private final ChatLanguageModel chatLanguageModel;

    @Override
    public Questions parseResponse(String response) {
        QuestionsExtractor questionsExtractor = AiServices.create(QuestionsExtractor.class, chatLanguageModel);
        return questionsExtractor.extractQuestions(response);
    }
}


