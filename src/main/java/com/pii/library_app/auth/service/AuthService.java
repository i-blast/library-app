package com.pii.library_app.auth.service;

import com.pii.library_app.auth.dto.AuthRequest;
import com.pii.library_app.auth.dto.AuthResponse;
import com.pii.library_app.auth.exception.UsernameAlreadyExistsException;
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

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(AuthRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Имя пользователя '" + request.getUsername() + "' занято");
        }

        var user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                Set.of(Role.USER)
        );
        userRepository.save(user);
        return new AuthResponse("Пользователь успешно зарегистрирован");
    }

    public AuthResponse login(AuthRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .map(user -> new AuthResponse("Login successful"))
                .orElseGet(() -> new AuthResponse("Invalid username or password"));
    }
}
