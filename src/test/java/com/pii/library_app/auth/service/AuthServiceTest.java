package com.pii.library_app.auth.service;

import com.pii.library_app.auth.dto.AuthRequest;
import com.pii.library_app.auth.dto.AuthResponse;
import com.pii.library_app.auth.exception.UsernameAlreadyExistsException;
import com.pii.library_app.user.model.Role;
import com.pii.library_app.user.model.User;
import com.pii.library_app.user.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthService authService;

/*    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder);
    }*/

    @Test
    void shouldRegisterNewUserSuccessfully() {
        AuthRequest authRequest = new AuthRequest("newUser", "securePassword");
        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(passwordEncoder.encode("securePassword")).thenReturn("encodedPassword");

        AuthResponse response = authService.register(authRequest);
        assertThat(response.getMessage()).isEqualTo("Пользователь успешно зарегистрирован");
        verify(userRepository, times(1)).existsByUsername("newUser");
        verify(passwordEncoder, times(1)).encode("securePassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    /*@Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setUsername("existingUser");
        request.setPassword("password123");

        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessage("Username 'existingUser' is already taken");

        verify(userRepository, times(1)).existsByUsername("existingUser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginSuccessfullyWhenCredentialsAreCorrect() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setUsername("validUser");
        request.setPassword("correctPassword");

        User user = new User("validUser", "encodedPassword", Set.of(Role.USER));

        when(userRepository.findByUsername("validUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("correctPassword", "encodedPassword")).thenReturn(true);

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertThat(response.getMessage()).isEqualTo("Login successful");

        verify(userRepository, times(1)).findByUsername("validUser");
        verify(passwordEncoder, times(1)).matches("correctPassword", "encodedPassword");
    }

    @Test
    void shouldReturnInvalidLoginWhenCredentialsAreIncorrect() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setUsername("validUser");
        request.setPassword("wrongPassword");

        User user = new User("validUser", "encodedPassword", Set.of(Role.USER));

        when(userRepository.findByUsername("validUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertThat(response.getMessage()).isEqualTo("Invalid username or password");

        verify(userRepository, times(1)).findByUsername("validUser");
        verify(passwordEncoder, times(1)).matches("wrongPassword", "encodedPassword");
    }

    @Test
    void shouldReturnInvalidLoginWhenUserNotFound() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setUsername("nonExistentUser");
        request.setPassword("password");

        when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertThat(response.getMessage()).isEqualTo("Invalid username or password");

        verify(userRepository, times(1)).findByUsername("nonExistentUser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }*/
}
