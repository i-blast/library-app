package com.pii.library_app.logging.service;

import com.pii.library_app.logging.model.UserActivityLog;
import com.pii.library_app.logging.repo.UserActivityLogRepository;
import com.pii.library_app.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static com.pii.library_app.util.TestDataFactory.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActivityLogServiceTest {

    @Mock
    private UserActivityLogRepository logRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private UserActivityLogService logService;

    private UserActivityLog log1;
    private UserActivityLog log2;

    @BeforeEach
    void setUp() {
        log1 = new UserActivityLog(1L, "/last24h", "LOGIN");
        log2 = new UserActivityLog(2L, "/last24h", "LOGIN");
    }

    @Test
    void getLogsForUserLast24Hours_ReturnsLogs() {
        var testUser = createTestUser("testuser", "password");
        when(userService.findUserById(eq(1L))).thenReturn(testUser);
        when(logRepository.findLogsByUserSince(eq(1L), any(LocalDateTime.class))).thenReturn(List.of(log1, log2));
        var result = logService.getLogsForUserLast24Hours(1L);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).action()).isEqualTo("/last24h");
        assertThat(result.get(1).action()).isEqualTo("/last24h");
        verify(userService).findUserById(1L);
        verify(logRepository).findLogsByUserSince(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void getLogsForUserLast24Hours_WhenNoLogs_ReturnsEmptyList() {
        when(userService.findUserById(1L)).thenReturn(createTestUser("testuser", "password"));
        when(logRepository.findLogsByUserSince(eq(1L), any(LocalDateTime.class))).thenReturn(List.of());
        var result = logService.getLogsForUserLast24Hours(1L);
        assertThat(result).isEmpty();
        verify(userService).findUserById(1L);
        verify(logRepository).findLogsByUserSince(eq(1L), any(LocalDateTime.class));
    }
}
