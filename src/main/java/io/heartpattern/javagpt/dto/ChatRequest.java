package io.heartpattern.javagpt.dto;

import java.util.List;

public record ChatRequest(
        String model,
        List<Message> messages
) {
}
