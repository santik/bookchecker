package com.santik.bookchecker.controller;

import com.santik.bookchecker.mapper.QuestionMapper;
import com.santik.bookchecker.model.Questions;
import com.santik.bookchecker.service.BookCheckerService;
import com.santik.bookchecker.producer.questions.api.DefaultApi;
import com.santik.bookchecker.producer.questions.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class BookCheckerController implements DefaultApi {

    private final BookCheckerService bookCheckerService;

    private final QuestionMapper questionMapper;

    @Override
    @CrossOrigin(origins = "*")
    public ResponseEntity<AnswersCheck> checkAnswers(List<UserAnswer> userAnswer) {
        List<UserAnswerCheck> answersCorrect = bookCheckerService.isAnswersCorrect(userAnswer);
        Optional<UserAnswerCheck> incorrectAnswer = answersCorrect.stream()
                .filter(userAnswerCheck -> !userAnswerCheck.getCorrect())
                .findFirst();
        boolean allCorrect = incorrectAnswer.isEmpty();
        return ResponseEntity.status(HttpStatus.OK).body(
                AnswersCheck.builder().correct(allCorrect).answers(answersCorrect).build()
        );
    }

    @Override
    @CrossOrigin(origins = "*")
    public ResponseEntity<BookChapterQuestions> getBookChapterQuestions(BookChapter bookChapter) {

        Questions questions = bookCheckerService.getQuestions(bookChapter.getBookName(), bookChapter.getChapter(), bookChapter.getLanguage());

        log.info("Questions: {}", questions);

        return ResponseEntity.status(HttpStatus.OK)
                .body(questionMapper.map(questions));
    }
}
