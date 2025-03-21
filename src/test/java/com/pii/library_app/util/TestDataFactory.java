package com.pii.library_app.util;

import com.pii.library_app.book.model.Book;
import com.pii.library_app.book.model.Genre;
import com.pii.library_app.user.model.User;

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
}
