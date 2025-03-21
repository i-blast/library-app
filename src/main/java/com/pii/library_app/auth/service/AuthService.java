package com.pii.library_app.auth.service;

import com.pii.library_app.auth.dto.AuthRequest;
import com.pii.library_app.auth.dto.AuthResponse;
import com.pii.library_app.auth.exception.InvalidCredentialsException;
import com.pii.library_app.auth.exception.UsernameAlreadyExistsException;
import com.pii.library_app.security.jwt.JwtUtil;
import com.pii.library_app.user.model.Role;
import com.pii.library_app.user.model.User;
import com.pii.library_app.user.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
            JwtUtil jwtUtil,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(AuthRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException("Имя пользователя '" + request.username() + "' занято");
        }

        var user = new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                Set.of(Role.USER)
        );
        userRepository.save(user);
        return new AuthResponse("Пользователь успешно зарегистрирован");
    }

    public AuthResponse login(AuthRequest request) {
        return userRepository.findByUsername(request.username())
                .filter(user -> passwordEncoder.matches(request.password(), user.getPassword()))
                .map(user -> new AuthResponse(jwtUtil.generateToken(user.getUsername())))
                .orElseThrow(() -> new InvalidCredentialsException("Неверное имя пользователя или пароль"));
    }
}
