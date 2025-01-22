package com.santik.bookchecker.service;

import com.santik.bookchecker.model.Questions;

public interface ResponseParser {

    Questions parseResponse(String response);
}
