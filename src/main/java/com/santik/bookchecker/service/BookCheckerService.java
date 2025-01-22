package com.santik.bookchecker.service;

import com.santik.bookchecker.exception.MissingAnswersException;
import com.santik.bookchecker.model.Answer;
import com.santik.bookchecker.model.Question;
import com.santik.bookchecker.model.Questions;
import com.santik.bookchecker.model.ai.Message;
import com.santik.bookchecker.producer.questions.model.UserAnswer;
import com.santik.bookchecker.producer.questions.model.UserAnswerCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class BookCheckerService {

    public static final Integer QUESTIONS_COUNT = 5;
    public static final Integer ANSWERS_COUNT = 3;

    private final LangChainOpenAIService langChainOpenAIService;
    private final PersistentChatMemoryStore persistentChatMemoryStore;

    public Questions getQuestions(String bookName, Integer bookChapter, String language) {

        String systemMessage = "You an expert in the book " + bookName;
        String userMessage = ". Generate " + QUESTIONS_COUNT + " questions with " + ANSWERS_COUNT
                + " answer variant and one correct answer for the book " + bookName + " for chapter " + bookChapter + " in " + language + " language. "
                + "Return only JSON in the following JSON format: " +
                "{\"questions\":[{\"question\": \"string\"," +
                "\"answers:\" : [" +
                "\"answer\": \"string\", \"isCorrect\":\"boolean\"" +
                "]" +
                "}]}.  Do not wrap the json codes in JSON markers.";
        Message message = Message.builder()
                .systemMessage(systemMessage)
                .userMessage(userMessage)
                .build();

        Questions questions = langChainOpenAIService.callOpenAI(message);

        saveQuestions(questions);

        return questions;
    }

    private void saveQuestions(Questions questions) {
        questions.getQuestions().forEach(persistentChatMemoryStore::updateQuestion);
    }

    public List<UserAnswerCheck> isAnswersCorrect(List<UserAnswer> userAnswers) {

        if (userAnswers.size() != QUESTIONS_COUNT) {
            throw new MissingAnswersException("Incorrect number of answers");
        }

        List<UserAnswerCheck> userAnswerChecks = userAnswers.stream().map(userAnswer -> {
            boolean answerCorrect = isAnswerCorrect(userAnswer);
            return UserAnswerCheck.builder()
                    .questionId(userAnswer.getQuestionId())
                    .answerId(userAnswer.getAnswerId())
                    .correct(answerCorrect)
                    .build();
        }).toList();

        userAnswers.forEach(userAnswer -> persistentChatMemoryStore.deleteMessages(userAnswer.getQuestionId()));

        return userAnswerChecks;
    }

    private boolean isAnswerCorrect(UserAnswer userAnswer) {
        Question question = persistentChatMemoryStore.getQuestion(userAnswer.getQuestionId());
        var correctAnswer = question.getAnswers().stream().filter(Answer::getIsCorrect).findFirst().orElseThrow();
        return correctAnswer.getId().equals(userAnswer.getAnswerId());
    }
}
