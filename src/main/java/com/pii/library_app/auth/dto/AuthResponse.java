package com.pii.library_app.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ на запрос аутентификации")
public record AuthResponse(
        @Schema(description = "Сообщение с результатом аутентификации")
        String message
) {
}
