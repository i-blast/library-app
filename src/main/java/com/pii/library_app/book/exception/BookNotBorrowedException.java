package com.pii.library_app.book.exception;

public class BookNotBorrowedException extends RuntimeException {

    public BookNotBorrowedException(Long bookId) {
        super("Книга недоступна для возврата ID=" + bookId);
    }
}
