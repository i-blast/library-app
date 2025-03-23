package com.pii.library_app.util;

import com.pii.library_app.auth.dto.AuthRequest;
import com.pii.library_app.book.model.Book;
import com.pii.library_app.book.model.BorrowedBook;
import com.pii.library_app.book.model.Genre;
import com.pii.library_app.logging.model.UserActivityLog;
import com.pii.library_app.user.model.User;

import java.time.LocalDateTime;

public final class TestDataFactory {

    public static Book createTestBook(
            String title,
            String author,
            Genre genre
    ) {
        return new Book(title, author, genre);
    }

    public static User createTestUser(
            String username,
            String password
    ) {
        return new User(username, password);
    }

    public static BorrowedBook createTestBorrowedBook(
            User user,
            Book book,
            LocalDateTime borrowedAt
    ) {
        return new BorrowedBook(user, book, borrowedAt);
    }

    public static AuthRequest createTestAuthRequest(
            String username,
            String password
    ) {
        return new AuthRequest(username, password);
    }

    public static UserActivityLog createTestActivityLog(
            Long userId,
            String action,
            String endpoint
    ) {
        return new UserActivityLog(userId, action, endpoint);
    }
}
