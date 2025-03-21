package com.pii.library_app.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на аутентификацию пользователя")
public record AuthRequest(
        @Schema(description = "Имя пользователя", required = true)
        String username,

        @Schema(description = "Пароль пользователя", required = true)
        String password
) {
}
