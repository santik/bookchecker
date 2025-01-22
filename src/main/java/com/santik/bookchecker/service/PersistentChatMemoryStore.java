package com.santik.bookchecker.service;

import com.santik.bookchecker.model.Question;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.mapdb.Serializer.STRING;

@Service
class PersistentChatMemoryStore {

    private final DB db = DBMaker.fileDB("memory.db").transactionEnable().fileLockDisable()
            .make();
    private final Map<String, String> map = db.hashMap("messages", STRING, STRING).createOrOpen();

    public Question getQuestion(String id) {
        String json = map.getOrDefault(id, "{}");
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, Question.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateQuestion(Question question) {
        ObjectMapper mapper = new ObjectMapper();
        String value;
        try {
            value = mapper.writeValueAsString(question);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        map.put(question.getId(), value);
        db.commit();
    }

    public void deleteMessages(String id) {
        map.remove(id);
        db.commit();
    }
}
