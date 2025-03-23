package com.pii.library_app.logging.repo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static com.pii.library_app.util.TestDataFactory.createTestActivityLog;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserActivityLogRepoTest {

    @Autowired
    private UserActivityLogRepository userActivityLogRepository;

    @Test
    @DisplayName("Должен сохранять и находить лог активности по ID")
    void shouldSaveAndFindUserActivityLogById() {
        var log = createTestActivityLog(1L, "LOGIN", "/api/login");
        var savedLog = userActivityLogRepository.save(log);
        var foundLog = userActivityLogRepository.findById(savedLog.getId());
        assertFalse(foundLog.isEmpty());
        assertThat(foundLog.get().getUserId()).isEqualTo(1L);
        assertThat(foundLog.get().getAction()).isEqualTo("LOGIN");
        assertThat(foundLog.get().getEndpoint()).isEqualTo("/api/login");
    }

    @Test
    @DisplayName("Должен находить все логи активности")
    void shouldFindAllUserActivityLogs() {
        var log1 = createTestActivityLog(1L, "LOGIN", "/api/login");
        var log2 = createTestActivityLog(2L, "LOGOUT", "/api/logout");
        List.of(log1, log2).forEach(userActivityLogRepository::save);
        assertThat(userActivityLogRepository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("Должен удалять лог активности")
    void shouldDeleteUserActivityLog() {
        var log = createTestActivityLog(1L, "LOGIN", "/api/login");
        var savedLog = userActivityLogRepository.save(log);
        userActivityLogRepository.deleteById(savedLog.getId());
        assertTrue(userActivityLogRepository.findById(savedLog.getId()).isEmpty());
    }
}
