package com.pii.library_app.auth.service;

import com.pii.library_app.auth.dto.AuthRequest;
import com.pii.library_app.auth.dto.AuthResponse;
import com.pii.library_app.auth.exception.InvalidCredentialsException;
import com.pii.library_app.auth.exception.UsernameAlreadyExistsException;
import com.pii.library_app.security.jwt.JwtUtil;
import com.pii.library_app.user.model.Role;
import com.pii.library_app.user.model.User;
import com.pii.library_app.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthService(
            JwtUtil jwtUtil,
            UserService userService,
            PasswordEncoder passwordEncoder
    ) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    public AuthResponse register(AuthRequest request) {
        if (request.username() == null || request.username().isEmpty()) {
            throw new InvalidCredentialsException("Имя пользователя не может быть пустым");
        }
        if (userService.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException("Имя пользователя '" + request.username() + "' занято");
        }

        var user = new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                Set.of(Role.USER)
        );
        userService.createUser(user);
        return new AuthResponse("Пользователь успешно зарегистрирован");
    }

    public AuthResponse login(AuthRequest request) {
        var user = userService.findByUsername(request.username());
        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Неверное имя пользователя или пароль");
        }
        return new AuthResponse(jwtUtil.generateToken(user));
    }
}
