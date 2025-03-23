package com.pii.library_app.book.dto;

import com.pii.library_app.book.model.Book;
import com.pii.library_app.book.model.Genre;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Данные для создания книги")
public record CreateBookDto(

        @Schema(description = "Название книги", example = "The Art of Multiprocessor Programming")
        String title,

        @Schema(description = "Автор книги", example = "Maurice Herlihy, Nir Shavit, Victor Luchangco, Michael Spear")
        String author,

        @Schema(description = "Жанр книги", example = "PROGRAMMING")
        Genre genre
) {

    public Book toBook() {
        return new Book(title(), author(), genre());
    }
}
