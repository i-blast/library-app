package com.pii.library_app.book.exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long id) {
        super("Книга с ID " + id + " не найдена");
    }
}
