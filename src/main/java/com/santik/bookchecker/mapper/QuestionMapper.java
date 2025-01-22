package com.santik.bookchecker.mapper;

import com.santik.bookchecker.model.Questions;
import com.santik.bookchecker.producer.questions.model.BookChapterQuestions;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    BookChapterQuestions map(Questions questions);
}
