package com.pii.library_app.auth.controller;

import com.pii.library_app.auth.dto.AuthRequest;
import com.pii.library_app.auth.dto.AuthResponse;
import com.pii.library_app.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API для регистрации и аутентификации пользователей")
public class AuthController {

    private final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создаёт нового пользователя в системе",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован"),
                    @ApiResponse(responseCode = "401", description = "Некорректные данные пользователя"),
                    @ApiResponse(responseCode = "409", description = "Имя пользователя уже занято")
            }
    )
    public ResponseEntity<AuthResponse> register(
            @RequestBody AuthRequest request,
            Principal principal
    ) {
        LOG.debug(
                "\"➤➤➤➤➤➤➤ {} регистрирует пользователя: {}",
                Optional.ofNullable(principal).map(Principal::getName).orElse("anonymous"),
                request
        );
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Аутентификация пользователя",
            description = "Проверяет учётные данные пользователя и возвращает результат",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешная аутентификация"),
                    @ApiResponse(responseCode = "401", description = "Неверное имя пользователя или пароль")
            }
    )
    public ResponseEntity<AuthResponse> login(
            @RequestBody AuthRequest request,
            Principal principal
    ) {
        LOG.debug(
                "\"➤➤➤➤➤➤➤ {} пытается войти по данным: {}",
                Optional.ofNullable(principal).map(Principal::getName).orElse("anonymous"),
                request
        );
        return ResponseEntity.ok(authService.login(request));
    }
}
