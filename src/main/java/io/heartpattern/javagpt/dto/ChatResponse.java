package io.heartpattern.javagpt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ChatResponse(String id, String model, String object, Long created, List<Choice> choices, Usage usage) {
    public record Choice(String index, Message message, @JsonProperty("finish_reason") String finishReason) {
    }

    public record Usage(
            @JsonProperty("prompt_tokens")
            Long promptTokens,
            @JsonProperty("completion_tokens")
            Long completionTokens,
            @JsonProperty("total_tokens")
            Long totalTokens
    ) {
    }
}
