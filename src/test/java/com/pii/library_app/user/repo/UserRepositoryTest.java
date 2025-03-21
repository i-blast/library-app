package com.pii.library_app.user.repository;

import com.pii.library_app.user.model.Role;
import com.pii.library_app.user.repo.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Set;

import static com.pii.library_app.util.TestDataFactory.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Должен сохранять и находить пользователя по ID")
    void shouldSaveAndFindUserByUsername() {
        var user = createTestUser("testuser", "password");
        user.addAll(Set.of(Role.USER));
        userRepository.save(user);
        var foundUser = userRepository.findByUsername("testuser");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        assertThat(foundUser.get().getRoles()).containsExactly(Role.USER);
    }

    @Test
    @DisplayName("Должен возвращать true при поиске по имени пользователя, если пользователь есть в БД")
    void shouldReturnTrueIfUserExistsByUsername() {
        var user = createTestUser("admin", "password");
        user.addAll(Set.of(Role.ADMIN));
        userRepository.save(user);
        assertThat(userRepository.existsByUsername("admin")).isTrue();
    }

    @Test
    @DisplayName("Должен возвращать false при поиске по имени пользователя, если пользователь отсутствует в БД")
    void shouldReturnFalseIfUserDoesNotExistByUsername() {
        boolean exists = userRepository.existsByUsername("nouser");
        assertThat(exists).isFalse();
    }
}
