package com.pii.library_app.logging.repo;

import com.pii.library_app.logging.model.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface  UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {

    @Query("SELECT log FROM UserActivityLog log WHERE log.userId = :userId AND log.timestamp >= :since")
    List<UserActivityLog> findLogsByUserSince(Long userId, LocalDateTime since);
}
