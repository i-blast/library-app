package com.pii.library_app.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ на запрос аутентификации")
public final class AuthResponse {

    @Schema(description = "Сообщение с результатом аутентификации")
    private final String message;

    public AuthResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
