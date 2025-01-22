package com.santik.bookchecker.model;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class Answer implements Serializable {

  private String id = UUID.randomUUID().toString();

  private String answer;

  private Boolean isCorrect = false;
}

