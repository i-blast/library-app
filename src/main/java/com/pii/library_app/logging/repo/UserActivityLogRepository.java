package com.pii.library_app.logging.repo;

import com.pii.library_app.logging.model.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
}
