package com.pii.library_app.security.jwt;

import com.pii.library_app.user.model.Role;
import com.pii.library_app.user.model.User;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@TestPropertySource(properties = "JWT_SECRET_KEY=Y/aQcRmTIvybqtIqEfR4KhpqzlKQit+/Yi6igVW1dLg=")
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("Должен корректно генерировать JWT токен")
    void shouldGenerateValidToken() {
        var user = new User("testUser", "encodedPassword", Set.of(Role.USER));
        var token = jwtUtil.generateToken(user);
        assertThat(token).isNotNull();
        assertThat(jwtUtil.validateToken(token)).isTrue();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("testUser");
    }

    @Test
    @DisplayName("Должен валидировать корректный токен")
    void shouldValidateCorrectToken() {
        var user = new User("validUser", "encodedPassword", Set.of(Role.USER));
        var token = jwtUtil.generateToken(user);
        assertThat(jwtUtil.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("Должен отклонять недействительный токен")
    void shouldInvalidateIncorrectToken() {
        var invalidToken = "invalid.token.value";
        assertThat(jwtUtil.validateToken(invalidToken)).isFalse();
    }

    @Test
    @DisplayName("Должен извлекать имя пользователя из токена")
    void shouldExtractUsernameFromToken() {
        var user = new User("usernameTest", "encodedPassword", Set.of(Role.USER));
        var token = jwtUtil.generateToken(user);
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("usernameTest");
    }

    @Test
    @DisplayName("Должен выбрасывать исключение при подделанном токене")
    void shouldThrowExceptionForTamperedToken() {
        var user = new User("hacker", "encodedPassword", Set.of(Role.USER));
        String token = jwtUtil.generateToken(user) + "tampered";
        assertThat(jwtUtil.validateToken(token)).isFalse();
        assertThatThrownBy(() -> jwtUtil.extractUsername(token))
                .isInstanceOf(SignatureException.class);
    }
}
