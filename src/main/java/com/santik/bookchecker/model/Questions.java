package com.santik.bookchecker.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class Questions implements Serializable {
    private List<Question> questions = new ArrayList<>();
}

