package com.pii.library_app.logging.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Запись лога активности пользователя")
public record UserActivityLogDto(
        @Schema(description = "ID лога", example = "1")
        Long id,

        @Schema(description = "ID пользователя", example = "42")
        Long userId,

        @Schema(description = "Действие пользователя", example = "POST /api/meals")
        String action,

        @Schema(description = "Название эндпоинта", example = "MealController")
        String endpoint,

        @Schema(description = "Время запроса", example = "2025-03-22T14:30:00")
        LocalDateTime timestamp
) {
}
