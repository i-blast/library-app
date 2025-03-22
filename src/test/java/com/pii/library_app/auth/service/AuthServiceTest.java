package com.pii.library_app.auth.service;

import com.pii.library_app.auth.exception.InvalidCredentialsException;
import com.pii.library_app.auth.exception.UsernameAlreadyExistsException;
import com.pii.library_app.security.jwt.JwtUtil;
import com.pii.library_app.user.model.Role;
import com.pii.library_app.user.model.User;
import com.pii.library_app.user.repo.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static com.pii.library_app.util.TestDataFactory.createTestAuthRequest;
import static com.pii.library_app.util.TestDataFactory.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Должен успешно зарегистрировать нового пользователя")
    void shouldRegisterNewUserSuccessfully() {
        var authRequest = createTestAuthRequest("newUser", "securePassword");
        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(passwordEncoder.encode("securePassword")).thenReturn("encodedPassword");

        var response = authService.register(authRequest);

        assertThat(response.message()).isEqualTo("Пользователь успешно зарегистрирован");
        verify(userRepository, times(1)).existsByUsername("newUser");
        verify(passwordEncoder, times(1)).encode("securePassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Должен выбросить исключение, если пользователь уже есть в системе")
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        var request = createTestAuthRequest("existingUser", "securePassword");
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessage("Имя пользователя '" + request.username() + "' занято");
        verify(userRepository, times(1)).existsByUsername("existingUser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Должен авторизовать пользователя и вернуть JWT-токен")
    void shouldLoginSuccessfullyAndReturnJwtToken() {
        var request = createTestAuthRequest("validUser", "correctPassword");
        var user = createTestUser("validUser", "encodedPassword");
        user.addAll(Set.of(Role.USER));

        when(userRepository.findByUsername("validUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("correctPassword", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(user)).thenReturn("mockJwtToken");

        var response = authService.login(request);
        assertThat(response.message()).isEqualTo("mockJwtToken");
        verify(userRepository, times(1)).findByUsername("validUser");
        verify(passwordEncoder, times(1)).matches("correctPassword", "encodedPassword");
        verify(jwtUtil, times(1)).generateToken(user);
    }

    @Test
    @DisplayName("Должен выбросить исключение, если пользователь не найден")
    void shouldReturnInvalidLoginWhenUserNotFound() {
        var request = createTestAuthRequest("nonExistentUser", "securePassword");
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Неверное имя пользователя или пароль");
        verify(userRepository, times(1)).findByUsername("nonExistentUser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Должен выбросить исключение, если данные для входа неверны")
    void shouldReturnInvalidLoginWhenCredentialsAreIncorrect() {
        var request = createTestAuthRequest("validUser", "wrongPassword");
        var user = createTestUser("validUser", "encodedPassword");
        user.addAll(Set.of(Role.USER));
        when(userRepository.findByUsername("validUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Неверное имя пользователя или пароль");
        verify(userRepository, times(1)).findByUsername("validUser");
        verify(passwordEncoder, times(1)).matches("wrongPassword", "encodedPassword");
        verify(jwtUtil, never()).generateToken(user);
    }
}
