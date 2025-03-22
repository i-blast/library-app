package com.pii.library_app.book.dto;

import com.pii.library_app.book.model.BorrowedBook;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Книга, забронированная пользователем")
public record BorrowedBookDto(
        @Schema(description = "Идентификатор бронирования")
        Long id,

        @Schema(description = "Название книги", example = "История России")
        String bookTitle,

        @Schema(description = "Имя пользователя", example = "username")
        String username,

        @Schema(description = "Дата и время бронирования", example = "2026-01-01T12:00:00")
        LocalDateTime borrowedAt
) {
    public static BorrowedBookDto fromEntity(BorrowedBook borrowedBook) {
        return new BorrowedBookDto(
                borrowedBook.getId(),
                borrowedBook.getBook().getTitle(),
                borrowedBook.getUser().getUsername(),
                borrowedBook.getBorrowedAt()
        );
    }
}
