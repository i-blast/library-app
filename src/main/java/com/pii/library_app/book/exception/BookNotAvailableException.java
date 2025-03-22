package com.pii.library_app.book.exception;

public class BookNotAvailableException extends RuntimeException {

    public BookNotAvailableException(Long bookId) {
        super("Книга недоступна для бронирования ID=" + bookId);
    }
}
