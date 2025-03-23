package com.pii.library_app.logging.controller;

import com.pii.library_app.logging.dto.UserActivityLogDto;
import com.pii.library_app.logging.service.UserActivityLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/logs")
public class UserActivityLogController {

    private final Logger LOG = LoggerFactory.getLogger(UserActivityLogController.class);

    private final UserActivityLogService logService;

    public UserActivityLogController(UserActivityLogService logService) {
        this.logService = logService;
    }

    @Operation(
            summary = "Получить логи пользователя за последние 24 часа",
            description = "Возвращает список логов активности указанного пользователя за последние сутки"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Список логов успешно получен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserActivityLogDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/last24h")
    public List<UserActivityLogDto> getUserLast24HoursLogs(
            @RequestParam @Schema(description = "ID пользователя", example = "99") Long userId,
            Principal principal
    ) {
        LOG.debug(
                "➤➤➤➤➤➤➤ Пользователь {} получает записи по пользователю с ID= {}",
                Optional.ofNullable(principal).map(Principal::getName).orElse("anonymous"),
                userId
        );
        return logService.getLogsForUserLast24Hours(userId);
    }
}
