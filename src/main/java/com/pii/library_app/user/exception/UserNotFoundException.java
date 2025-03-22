package com.pii.library_app.user.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super("Пользователь на найден ID=" + userId);
    }

    public UserNotFoundException(String username) {
        super("Пользователь на найден username=" + username);
    }
}
