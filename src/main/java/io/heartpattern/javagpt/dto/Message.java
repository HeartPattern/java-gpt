package io.heartpattern.javagpt.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public record Message(
        Role role,
        String content
) {
    public enum Role {
        USER,
        SYSTEM,
        ASSISTANT;

        @JsonValue
        public String toLowerCase() {
            return toString().toLowerCase();
        }
    }
}
