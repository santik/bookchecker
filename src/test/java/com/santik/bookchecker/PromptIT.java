package com.santik.bookchecker;

import com.santik.bookchecker.model.Answer;
import com.santik.bookchecker.model.Questions;
import com.santik.bookchecker.service.BookCheckerService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
public class PromptIT {

    @Autowired
    private BookCheckerService bookCheckerService;

    @Test
    public void testPrompt() {
        String bookName = "Harry potter and philosopher's stone";
        Integer bookChapter = 1;
        String language = "english";
        Questions questions = bookCheckerService.getQuestions(bookName, bookChapter, language);
        log.info("Questions: {}", questions);

        assertEquals(BookCheckerService.QUESTIONS_COUNT, questions.getQuestions().size());

        questions.getQuestions().forEach(question -> {
            assertEquals(BookCheckerService.ANSWERS_COUNT, question.getAnswers().size());
            List<Answer> list = question.getAnswers().stream().filter(Answer::getIsCorrect).toList();
            assertEquals(1, list.size());
        });
    }

    @Test
    public void testPromptRu() {
        String bookName = "Harry potter and philosopher's stone";
        Integer bookChapter = 1;
        String language = "russian";
        Questions questions = bookCheckerService.getQuestions(bookName, bookChapter, language);
        log.info("Questions: {}", questions);

        assertEquals(BookCheckerService.QUESTIONS_COUNT, questions.getQuestions().size());

        questions.getQuestions().forEach(question -> {
            assertEquals(BookCheckerService.ANSWERS_COUNT, question.getAnswers().size());
            List<Answer> list = question.getAnswers().stream().filter(Answer::getIsCorrect).toList();
            assertEquals(1, list.size());
        });
    }
}
