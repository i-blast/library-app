package com.pii.library_app.logging.service;

import com.pii.library_app.logging.dto.UserActivityLogDto;
import com.pii.library_app.logging.repo.UserActivityLogRepository;
import com.pii.library_app.user.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserActivityLogService {

    private final UserActivityLogRepository logRepository;
    private final UserService userService;

    public UserActivityLogService(
            UserActivityLogRepository logRepository,
            UserService userService
    ) {
        this.logRepository = logRepository;
        this.userService = userService;
    }

    public List<UserActivityLogDto> getLogsForUserLast24Hours(Long userId) {
        var user = userService.findUserById(userId);
        var logs = logRepository.findLogsByUserSince(userId, LocalDateTime.now().minusDays(1));
        return logs.stream()
                .map(log -> new UserActivityLogDto(
                        log.getId(),
                        log.getUserId(),
                        log.getAction(),
                        log.getEndpoint(),
                        log.getTimestamp()))
                .toList();
    }
}
