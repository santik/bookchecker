package com.santik.bookchecker.service.parsers;

import com.santik.bookchecker.model.Answer;
import com.santik.bookchecker.model.Question;
import com.santik.bookchecker.model.Questions;
import com.santik.bookchecker.service.ResponseParser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ManualResponseParser implements ResponseParser {

    @Override
    public Questions parseResponse(String response) {

        Questions questions = new Questions();

        String[] questionsWithAnswers = response.split("\n\n");

        List<Question> list = Arrays.stream(questionsWithAnswers).map(item -> {
                    String[] split = item.trim().split("\n");
                    Question question = new Question();
                    question.setQuestion(split[0].trim());
                    List<Answer> answers = new ArrayList<>();
                    for (int i = 1; i < split.length; i++) {
                        Answer answer = new Answer();
                        String substring = split[i].trim();
                        if (substring.endsWith("*") || substring.startsWith("*")) {
                            answer.setIsCorrect(true);
                        }
                        substring = substring
                                .replace("*", "").trim();
                        answer.setAnswer(substring);
                        answers.add(answer);
                    }
                    question.setAnswers(answers);
                    return question;
                }
        ).toList();

        questions.setQuestions(list);


        return questions;
    }
}
