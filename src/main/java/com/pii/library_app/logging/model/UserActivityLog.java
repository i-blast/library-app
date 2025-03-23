package com.pii.library_app.logging.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_activity_logs")
public final class UserActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String action;

    private String endpoint;

    private  LocalDateTime timestamp;

    protected UserActivityLog() {
    }

    public UserActivityLog(Long userId, String action, String endpoint) {
        this.userId = userId;
        this.action = action;
        this.endpoint = endpoint;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getAction() {
        return action;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
