package com.pii.library_app.book.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Жанр книги")
public enum Genre {

    @Schema(description = "Фэнтези")
    FANTASY,
    @Schema(description = "Детектив")
    DETECTIVE,
    @Schema(description = "Роман")
    ROMANCE,
    @Schema(description = "Научная фантастика")
    SCIENCE_FICTION,
    @Schema(description = "Триллер")
    THRILLER,
    @Schema(description = "Документальная литература")
    NON_FICTION,
    @Schema(description = "Антиутопия")
    DYSTOPIAN,
    @Schema(description = "Программирование")
    PROGRAMMING

}
