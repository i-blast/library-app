package com.pii.library_app.auth.exception;

import com.pii.library_app.book.exception.BookNotAvailableException;
import com.pii.library_app.book.exception.BookNotBorrowedException;
import com.pii.library_app.book.exception.BookNotFoundException;
import com.pii.library_app.user.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Object> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException exc) {
        LOG.error("\"➤➤➤➤➤➤➤ Error: {}", exc.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, exc.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Object> handleInvalidCredentialsException(InvalidCredentialsException exc) {
        LOG.error("\"➤➤➤➤➤➤➤ Error: {}", exc.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, exc.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception exc) {
        LOG.error("\"➤➤➤➤➤➤➤ Error: {}", exc.getMessage());
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Возникла непредвиденная ошибка");
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Object> handleBookNotFoundException(BookNotFoundException exc) {
        LOG.error("\"➤➤➤➤➤➤➤ Error: {}", exc.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, exc.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException exc) {
        LOG.error("\"➤➤➤➤➤➤➤ Error: {}", exc.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, exc.getMessage());
    }

    @ExceptionHandler(BookNotAvailableException.class)
    public ResponseEntity<Object> handleBookNotAvailableException(BookNotAvailableException exc) {
        LOG.error("\"➤➤➤➤➤➤➤ Error: {}", exc.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, exc.getMessage());
    }

    @ExceptionHandler(BookNotBorrowedException.class)
    public ResponseEntity<Object> handleBookNotBorrowedException(BookNotBorrowedException exc) {
        LOG.error("\"➤➤➤➤➤➤➤ Error: {}", exc.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exc.getMessage());
    }

    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message) {
        var body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}
