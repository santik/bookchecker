package com.santik.bookchecker.model.ai;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Message {
    private String userMessage;
    private String systemMessage;
}
