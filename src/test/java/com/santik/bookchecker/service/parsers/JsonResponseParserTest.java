package com.santik.bookchecker.service.parsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.santik.bookchecker.model.Questions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonResponseParserTest {

    @Test
    void parseResponse() throws IOException {

        JsonResponseParser parser = new JsonResponseParser(new ObjectMapper());

        ClassPathResource resource = new ClassPathResource("airesponse.json");
        String contentAsString = resource.getContentAsString(StandardCharsets.UTF_8);

        Questions questions = parser.parseResponse(contentAsString);

        assertEquals(5, questions.getQuestions().size());

    }
}