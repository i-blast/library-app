package com.pii.library_app.book.dto;

import com.pii.library_app.book.model.Genre;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Фильтр для поиска книг")
public record SearchBookFilterDto(
        @Schema(description = "Название книги", example = "Введение в системы баз данных")
        String title,

        @Schema(description = "Автор книги", example = "Дейт К.Дж.")
        String author,

        @Schema(description = "Жанр книги", example = "PROGRAMMING")
        Genre genre
) {
}
