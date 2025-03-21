package com.pii.library_app.auth.dto;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на аутентификацию пользователя")
public final class AuthRequest {

    @Schema(description = "Имя пользователя", example = "user123", required = true)
    private final String username;

    @Schema(description = "Пароль пользователя", example = "password123", required = true)
    private final String password;

    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
