package com.pii.library_app.user.service;

import com.pii.library_app.auth.exception.UsernameAlreadyExistsException;
import com.pii.library_app.user.exception.UserNotFoundException;
import com.pii.library_app.user.model.User;
import com.pii.library_app.user.repo.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.pii.library_app.util.TestDataFactory.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Должен вернуть true, если пользователь существует")
    void shouldReturnTrueIfUserExists() {
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);
        assertThat(userService.existsByUsername("existingUser")).isTrue();
        verify(userRepository, times(1)).existsByUsername("existingUser");
    }

    @Test
    @DisplayName("Должен вернуть false, если пользователя нет в системе")
    void shouldReturnFalseIfUserDoesNotExist() {
        when(userRepository.existsByUsername("nonExistentUser")).thenReturn(false);
        assertThat(userService.existsByUsername("nonExistentUser")).isFalse();
        verify(userRepository, times(1)).existsByUsername("nonExistentUser");
    }

    @Test
    @DisplayName("Должен найти пользователя по имени")
    void shouldFindUserByUsername() {
        var user = createTestUser("testUser", "encodedPassword");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        var foundUser = userService.findByUsername("testUser");
        assertThat(foundUser).isEqualTo(user);
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    @DisplayName("Должен выбросить исключение, если пользователь не найден по имени")
    void shouldThrowExceptionIfUserNotFoundByUsername() {
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findByUsername("unknownUser"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("unknownUser");
    }

    @Test
    @DisplayName("Должен найти пользователя по ID")
    void shouldFindUserById() {
        var user = createTestUser("testUser", "encodedPassword");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        var foundUser = userService.findUserById(1L);
        assertThat(foundUser).isEqualTo(user);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Должен выбросить исключение, если пользователь не найден по ID")
    void shouldThrowExceptionIfUserNotFoundById() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findUserById(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Должен создать нового пользователя")
    void shouldCreateNewUser() {
        var user = createTestUser("newUser", "encodedPassword");
        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        userService.createUser(user);
        verify(userRepository, times(1)).existsByUsername("newUser");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Должен выбросить исключение, если имя пользователя уже занято")
    void shouldThrowExceptionIfUsernameAlreadyExists() {
        var user = createTestUser("existingUser", "encodedPassword");
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);
        assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessageContaining("Имя пользователя 'existingUser' занято");
    }
}
