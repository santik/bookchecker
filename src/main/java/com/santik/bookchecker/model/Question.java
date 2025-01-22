package com.santik.bookchecker.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Question implements Serializable {

  private String id = UUID.randomUUID().toString();

  private String question;

  private List<Answer> answers = new ArrayList<>();
}

