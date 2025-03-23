package com.pii.library_app.book.controller;

import com.pii.library_app.book.dto.BorrowedBookDto;
import com.pii.library_app.book.dto.ReturnedBookDto;
import com.pii.library_app.book.dto.SearchBookFilterDto;
import com.pii.library_app.book.dto.SearchBookResponseDto;
import com.pii.library_app.book.model.Book;
import com.pii.library_app.book.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/books")
@Tag(name = "Book", description = "API для управления книгами")
public class BookController {

    private final Logger LOG = LoggerFactory.getLogger(BookController.class);

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Добавление новой книги",
            description = "Добавляет новую книгу в библиотеку. Только для пользователей с ролью ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Книга успешно добавлена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class))
            ),
            @ApiResponse(
                    responseCode = "403", description = "Доступ запрещен. Требуется роль ADMIN."
            )
    })
    public ResponseEntity<Book> createBook(
            @RequestBody Book book,
            Principal principal
    ) {
        LOG.debug(
                "\"➤➤➤➤➤➤➤ Пользователь {} добавляет книгу: {}",
                Optional.ofNullable(principal).map(Principal::getName).orElse("anonymous"),
                book.getTitle()
        );
        return ResponseEntity.ok(bookService.createBook(book));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Редактирование книги",
            description = "Обновляет информацию о книге по её ID. Только для пользователей с ролью ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Книга успешно обновлена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещен. Требуется роль ADMIN"
            ),
            @ApiResponse(responseCode = "404", description = "Книга не найдена")
    })
    public ResponseEntity<Book> updateBook(
            @PathVariable Long id,
            @RequestBody Book book,
            Principal principal
    ) {
        LOG.debug(
                "\"➤➤➤➤➤➤➤ Пользователь {} обновляет книгу c id: {}",
                Optional.ofNullable(principal).map(Principal::getName).orElse("anonymous"),
                id
        );
        return ResponseEntity.ok(bookService.updateBook(id, book));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Удаление книги",
            description = "Удаляет книгу по её ID. Только для пользователей с ролью ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Книга успешно удалена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен. Требуется роль ADMIN."),
            @ApiResponse(responseCode = "404", description = "Книга не найдена")
    })
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long id,
            Principal principal
    ) {
        LOG.debug(
                "\"➤➤➤➤➤➤➤ Пользователь {} удаляет книгу с id: {}",
                Optional.ofNullable(principal).map(Principal::getName).orElse("anonymous"),
                id
        );
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Поиск книг",
            description = "Поиск книг по названию, автору или жанру"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Успешный поиск книг",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchBookResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "Некорректные параметры запроса"
            )
    })
    public ResponseEntity<SearchBookResponseDto> searchBooks(
            @RequestBody SearchBookFilterDto filter,
            Principal principal
    ) {
        LOG.debug(
                "➤➤➤➤➤➤➤ Пользователь '{}' ищет книги по фильтру {}",
                Optional.ofNullable(principal).map(Principal::getName).orElse("anonymous"),
                filter
        );
        return ResponseEntity.ok(bookService.searchBooks(filter));
    }

    @PostMapping("/{bookId}/borrow")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Забронировать книгу",
            description = "Позволяет пользователю забронировать книгу по её ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Книга успешно забронирована",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BorrowedBookDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Книга недоступна для бронирования"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Книга не найдена"
            )
    })
    public ResponseEntity<BorrowedBookDto> borrowBook(
            @PathVariable Long bookId,
            Principal principal
    ) {
        LOG.debug(
                "\"➤➤➤➤➤➤➤ Пользователь {} бронирует книгу с id: {}",
                Optional.ofNullable(principal).map(Principal::getName).orElse("anonymous"),
                bookId
        );
        var borrowedBook = bookService.borrowBook(bookId, principal.getName());
        return ResponseEntity.ok(BorrowedBookDto.fromEntity(borrowedBook));
    }

    @PostMapping("/{bookId}/return")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Вернуть книгу",
            description = "Позволяет пользователю вернуть книгу по её ID"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Книга успешно возвращена",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnedBookDto.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Бронирование не найдено"
    )
    public ResponseEntity<ReturnedBookDto> returnBook(
            @PathVariable Long bookId,
            Principal principal
    ) {
        LOG.debug(
                "\"➤➤➤➤➤➤➤ Пользователь {} возвращает книгу с id: {}",
                principal.getName(),
                bookId
        );
        var returnedBook = bookService.returnBook(bookId, principal.getName());
        return ResponseEntity.ok(ReturnedBookDto.fromEntity(returnedBook));
    }
}
