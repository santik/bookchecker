package com.santik.bookchecker.service.parsers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.santik.bookchecker.model.Questions;
import com.santik.bookchecker.service.ResponseParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JsonResponseParser implements ResponseParser {

    private final ObjectMapper mapper;

    @Override
    public Questions parseResponse(String response) {
        try {
            return mapper.readValue(response, Questions.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
