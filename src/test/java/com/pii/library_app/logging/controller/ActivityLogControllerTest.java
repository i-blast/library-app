package com.pii.library_app.logging.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pii.library_app.logging.dto.UserActivityLogDto;
import com.pii.library_app.logging.service.UserActivityLogService;
import com.pii.library_app.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserActivityLogController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ActivityLogControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserActivityLogService logService;
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private ObjectMapper objectMapper;

    private UserActivityLogDto logDto;

    @BeforeEach
    void setUp() {
        logDto = new UserActivityLogDto(1L, 1L, "LOGIN", "/last24h", LocalDateTime.now());
    }

    @Test
    @DisplayName("Получение логов пользователя за 24 часа - успешный сценарий")
    void shouldReturnUserLogsForLast24Hours() throws Exception {
        when(logService.getLogsForUserLast24Hours(1L)).thenReturn(List.of(logDto));
        mockMvc.perform(get("/api/logs/last24h").param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[0].action").value("LOGIN"));
        verify(logService, times(1)).getLogsForUserLast24Hours(1L);
    }
}
