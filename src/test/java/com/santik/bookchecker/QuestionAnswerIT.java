package com.santik.bookchecker;

import com.santik.bookchecker.model.Questions;
import com.santik.bookchecker.model.Question;
import com.santik.bookchecker.model.Answer;
import com.santik.bookchecker.producer.login.model.AuthToken;
import com.santik.bookchecker.producer.login.model.User;
import com.santik.bookchecker.service.BookCheckerService;
import com.santik.bookchecker.service.LangChainOpenAIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.santik.bookchecker.producer.questions.model.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class QuestionAnswerIT {

    @Autowired
    private MockMvc mockMvc;

    @Value("${http.username}")
    String username;

    @Value("${http.password}")
    String password;

    @MockBean
    private LangChainOpenAIService langChainOpenAIService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCorrectAnswers() throws Exception {

        AuthToken authToken = getAuthToken();

        Questions questions = generateQuestions();

        when(langChainOpenAIService.callOpenAI(any())).thenReturn(questions);


        String header = "Bearer " + authToken.getToken();
        MvcResult getQuestionsResult = mockMvc.perform(
                        post("/api/get-book-chapter-questions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(getGetBookChapterRequest()))
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, header)
                )
                .andExpect(status().isOk())
                .andReturn();

        String getQuestionsResponseContent = getQuestionsResult.getResponse().getContentAsString();
        log.info(getQuestionsResponseContent);
        BookChapterQuestions aiQuestions = mapper.readValue(getQuestionsResponseContent, BookChapterQuestions.class);

        assertEquals(BookCheckerService.QUESTIONS_COUNT, aiQuestions.getQuestions().size());
        assertEquals(BookCheckerService.ANSWERS_COUNT, aiQuestions.getQuestions().get(0).getAnswers().size());


        List<UserAnswer> userAnswers = new ArrayList<>();
        questions.getQuestions().forEach(question -> {
            UserAnswer userAnswer = new UserAnswer();
            userAnswer.questionId(question.getId());
            userAnswer.answerId(question.getAnswers().stream().filter(Answer::getIsCorrect).findFirst().get().getId());
            userAnswers.add(userAnswer);
        });

        MvcResult checkAnswersResult = mockMvc.perform(
                        post("/api/check-answers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userAnswers))
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken.getToken())
                )
                .andExpect(status().isOk())
                .andReturn();

        String checkAnswersResponse = checkAnswersResult.getResponse().getContentAsString();
        log.info(checkAnswersResponse);
        AnswersCheck answersCheck = mapper.readValue(checkAnswersResponse, AnswersCheck.class);

        assertEquals(true, answersCheck.getCorrect());
    }

    @Test
    public void testInCorrectAnswers() throws Exception {

        AuthToken authToken = getAuthToken();

        Questions questions = generateQuestions();

        when(langChainOpenAIService.callOpenAI(any())).thenReturn(questions);


        MvcResult getQuestionsResult = mockMvc.perform(
                        post("/api/get-book-chapter-questions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(getGetBookChapterRequest()))
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAuthToken().getToken())
                )
                .andExpect(status().isOk())
                .andReturn();

        String getQuestionsResponseContent = getQuestionsResult.getResponse().getContentAsString();
        log.info(getQuestionsResponseContent);
        BookChapterQuestions aiQuestions = mapper.readValue(getQuestionsResponseContent, BookChapterQuestions.class);

        assertEquals(BookCheckerService.QUESTIONS_COUNT, aiQuestions.getQuestions().size());
        assertEquals(BookCheckerService.ANSWERS_COUNT, aiQuestions.getQuestions().get(0).getAnswers().size());

        List<UserAnswer> userAnswers = new ArrayList<>();
        questions.getQuestions().forEach(question -> {
            UserAnswer userAnswer = new UserAnswer();
            userAnswer.questionId(question.getId());
            userAnswer.answerId(question.getAnswers().stream().filter(Answer::getIsCorrect).findFirst().get().getId());
            userAnswers.add(userAnswer);
        });

        UserAnswer incorrectUserAnswer = userAnswers.get(0);
        incorrectUserAnswer.answerId(UUID.randomUUID().toString());

        var checkAnswersResult = mockMvc.perform(
                        post("/api/check-answers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userAnswers))
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken.getToken())
                )
                .andExpect(status().isOk())
                .andReturn();

        var checkAnswersResponse = checkAnswersResult.getResponse().getContentAsString();
        log.info(checkAnswersResponse);
        var answersCheck = mapper.readValue(checkAnswersResponse, AnswersCheck.class);

        assertEquals(false, answersCheck.getCorrect());

        UserAnswerCheck incorrectUserAnswerCheck = answersCheck.getAnswers().stream()
                .filter(userAnswerCheck -> userAnswerCheck.getQuestionId().equals(incorrectUserAnswer.getQuestionId()))
                .findFirst().get();

        assertEquals(false, incorrectUserAnswerCheck.getCorrect());


    }

    private AuthToken getAuthToken() throws Exception {

        User user = new User();
        user.username(username);
        user.password(password);

        MvcResult mvcResult = mockMvc.perform(
                        post("/auth/login")
                                .content(mapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String authResponse = mvcResult.getResponse().getContentAsString();

        return new ObjectMapper().readValue(authResponse, AuthToken.class);
    }

    @Test
    public void testValidation() throws Exception {

        AuthToken authToken = getAuthToken();

        mockMvc.perform(
                        post("/api/get-book-chapter-questions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken.getToken())
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void testUnauthorized() throws Exception {

        mockMvc.perform(
                        post("/api/get-book-chapter-questions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andReturn();
    }

    private Questions generateQuestions() {
        Questions questions = new Questions();
        List<Question> questionsList = new ArrayList<>();
        for (int i = 0; i < BookCheckerService.QUESTIONS_COUNT; i++) {
            questionsList.add(generateQuestion());
        }
        questions.setQuestions(questionsList);
        return questions;
    }

    private static BookChapter getGetBookChapterRequest() {
        BookChapter request = new BookChapter();
        request.bookName("Harry Potter and philosopher stone");
        request.chapter(1);
        return request;
    }

    private Question generateQuestion() {
        Question question = new Question();
        question.setQuestion("some question");
        question.setId(UUID.randomUUID().toString());
        List<Answer> answersList = new ArrayList<>();
        answersList.add(generateAnswer(false));
        answersList.add(generateAnswer(false));
        answersList.add(generateAnswer(true));
        question.setAnswers(answersList);
        return question;
    }

    private Answer generateAnswer(boolean isCorrect) {
        Answer answer = new Answer();
        answer.setId(UUID.randomUUID().toString());
        answer.setAnswer("some answer");
        answer.setIsCorrect(isCorrect);
        return answer;
    }
}
