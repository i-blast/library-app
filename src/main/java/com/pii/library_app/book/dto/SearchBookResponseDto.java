package com.pii.library_app.book.dto;

import com.pii.library_app.book.model.Book;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Ответ на запрос поиска книг")
public record SearchBookResponseDto(
        @Schema(description = "Список найденных книг")
        List<Book> books,

        @Schema(description = "Общее количество найденных книг", example = "1")
        long totalCount
) {
}
