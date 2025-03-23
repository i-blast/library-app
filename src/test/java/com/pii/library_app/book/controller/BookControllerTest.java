package com.pii.library_app.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pii.library_app.book.dto.CreateBookDto;
import com.pii.library_app.book.dto.SearchBookFilterDto;
import com.pii.library_app.book.dto.SearchBookResponseDto;
import com.pii.library_app.book.exception.BookNotAvailableException;
import com.pii.library_app.book.exception.BookNotBorrowedException;
import com.pii.library_app.book.exception.BookNotFoundException;
import com.pii.library_app.book.model.Book;
import com.pii.library_app.book.model.BorrowedBook;
import com.pii.library_app.book.model.Genre;
import com.pii.library_app.book.service.BookService;
import com.pii.library_app.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static com.pii.library_app.util.TestDataFactory.createTestBook;
import static com.pii.library_app.util.TestDataFactory.createTestUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private BookService bookService;
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private ObjectMapper objectMapper;

    private Book book;
    private BorrowedBook borrowedBook;

    @BeforeEach
    void setUp() {
        book = createTestBook("1984", "George Orwell", Genre.DYSTOPIAN);
        book.setId(1L);
        borrowedBook = new BorrowedBook();
        borrowedBook.setId(1L);
        borrowedBook.setBook(book);
        borrowedBook.setBorrowedAt(LocalDateTime.of(2025, 3, 20, 12, 0));
        var user = createTestUser("testUser", "password");
        borrowedBook.setUser(user);
    }

    @Test
    @DisplayName("Создание книги - успешный сценарий")
    void shouldCreateBook() throws Exception {
        when(bookService.createBook(any(CreateBookDto.class))).thenReturn(book);
        mockMvc.perform(post("/books")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("1984"))
                .andExpect(jsonPath("$.author").value("George Orwell"));
        verify(bookService, times(1)).createBook(any(CreateBookDto.class));
    }

    @Test
    @DisplayName("Обновление книги - успешный сценарий")
    void shouldUpdateBook() throws Exception {
        when(bookService.updateBook(eq(1L), any(CreateBookDto.class))).thenReturn(book);
        mockMvc.perform(put("/books/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("1984"))
                .andExpect(jsonPath("$.author").value("George Orwell"));
        verify(bookService, times(1)).updateBook(eq(1L), any(CreateBookDto.class));
    }

    @Test
    @DisplayName("Удаление книги - успешный сценарий")
    void shouldDeleteBook() throws Exception {
        doNothing().when(bookService).deleteBook(1L);
        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());
        verify(bookService, times(1)).deleteBook(1L);
    }

    @Disabled
    @Test
    @DisplayName("Создание книги - доступ запрещен для пользователя без роли ADMIN")
    void shouldDenyAccessForNonAdminUser() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isForbidden());
        verify(bookService, never()).createBook(any(CreateBookDto.class));
    }

    @Test
    @DisplayName("Обновление книги - книга не найдена")
    void shouldReturnNotFoundWhenUpdatingNonExistentBook() throws Exception {
        when(bookService.updateBook(eq(69L), any(CreateBookDto.class)))
                .thenThrow(new BookNotFoundException(69L));
        mockMvc.perform(put("/books/69")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isNotFound());
        verify(bookService, times(1)).updateBook(eq(69L), any(CreateBookDto.class));
    }

    @Test
    @DisplayName("Поиск книг - успешный сценарий")
    void shouldSearchBooks() throws Exception {
        var filter = new SearchBookFilterDto("1984", "George Orwell", Genre.DYSTOPIAN);
        List<Book> books = List.of(createTestBook("1984", "George Orwell", Genre.DYSTOPIAN));
        var response = new SearchBookResponseDto(books, books.size());
        when(bookService.searchBooks(any(SearchBookFilterDto.class))).thenReturn(response);
        mockMvc.perform(post("/books/search")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books[0].title").value("1984"))
                .andExpect(jsonPath("$.books[0].author").value("George Orwell"));
        verify(bookService, times(1)).searchBooks(any(SearchBookFilterDto.class));
    }

    @Test
    @DisplayName("Поиск книг по названию - успешный сценарий")
    void shouldSearchBooksByTitle() throws Exception {
        var filter = new SearchBookFilterDto("1984", null, null);
        List<Book> books = List.of(createTestBook("1984", "George Orwell", Genre.DYSTOPIAN));
        var response = new SearchBookResponseDto(books, books.size());
        when(bookService.searchBooks(any(SearchBookFilterDto.class))).thenReturn(response);
        mockMvc.perform(post("/books/search")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books[0].title").value("1984"))
                .andExpect(jsonPath("$.books[0].author").value("George Orwell"));
        verify(bookService, times(1)).searchBooks(any(SearchBookFilterDto.class));
    }

    @Test
    @DisplayName("Поиск книг по жанру - успешный сценарий")
    void shouldSearchBooksByGenre() throws Exception {
        var filter = new SearchBookFilterDto(null, null, Genre.DYSTOPIAN);
        List<Book> books = List.of(createTestBook("1984", "George Orwell", Genre.DYSTOPIAN));
        var response = new SearchBookResponseDto(books, books.size());
        when(bookService.searchBooks(any(SearchBookFilterDto.class))).thenReturn(response);
        mockMvc.perform(post("/books/search")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books[0].title").value("1984"))
                .andExpect(jsonPath("$.books[0].author").value("George Orwell"));
        verify(bookService, times(1)).searchBooks(any(SearchBookFilterDto.class));
    }

    @Test
    @DisplayName("Поиск книг без фильтров - возвращает все книги")
    void shouldReturnAllBooksWhenNoFilterProvided() throws Exception {
        var filter = new SearchBookFilterDto(null, null, null);
        List<Book> books = List.of(
                createTestBook("1984", "George Orwell", Genre.DYSTOPIAN),
                createTestBook("Animal Farm", "George Orwell", Genre.DYSTOPIAN)
        );
        var response = new SearchBookResponseDto(books, books.size());
        when(bookService.searchBooks(any(SearchBookFilterDto.class))).thenReturn(response);
        mockMvc.perform(post("/books/search")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books").isArray())
                .andExpect(jsonPath("$.books.length()").value(2))
                .andExpect(jsonPath("$.books[0].title").value("1984"))
                .andExpect(jsonPath("$.books[1].title").value("Animal Farm"));
        verify(bookService, times(1)).searchBooks(any(SearchBookFilterDto.class));
    }

    @Test
    @DisplayName("Бронирование книги - успешный сценарий")
    void shouldBorrowBookSuccessfully() throws Exception {
        when(bookService.borrowBook(eq(1L), any(String.class))).thenReturn(borrowedBook);
        mockMvc.perform(post("/books/1/borrow").principal(() -> "testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.bookTitle").value("1984"))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.borrowedAt").value("2025-03-20T12:00:00"))
                .andExpect(jsonPath("$.returnedAt").doesNotExist());
        verify(bookService, times(1)).borrowBook(eq(1L), eq("testUser"));
    }

    @Test
    @DisplayName("Возврат книги - книга не найдена")
    void shouldReturnNotFoundWhenBookNotFound() throws Exception {
        var bookId = 1L;
        var username = "testUser";
        when(bookService.returnBook(eq(bookId), eq(username)))
                .thenThrow(new BookNotFoundException(bookId));
        mockMvc.perform(post("/books/{bookId}/return", bookId).principal(() -> username))
                .andDo(print())
                .andExpect(status().isNotFound());
        verify(bookService, times(1)).returnBook(eq(bookId), eq(username));
    }

    @Test
    @DisplayName("Бронирование книги - книга недоступна")
    void shouldReturnBadRequestWhenBookNotAvailable() throws Exception {
        when(bookService.borrowBook(eq(1L), any(String.class)))
                .thenThrow(new BookNotAvailableException(1L));
        mockMvc.perform(post("/books/1/borrow")
                        .principal(() -> "testUser"))
                .andExpect(status().isConflict());
        verify(bookService, times(1)).borrowBook(eq(1L), eq("testUser"));
    }

    @Test
    @DisplayName("Возврат книги - успешный сценарий")
    void shouldReturnBookSuccessfully() throws Exception {
        borrowedBook.setReturnedAt(LocalDateTime.of(2025, 3, 22, 15, 30));
        when(bookService.returnBook(eq(1L), eq("testUser"))).thenReturn(borrowedBook);
        mockMvc.perform(post("/books/1/return").principal(() -> "testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.bookTitle").value("1984"))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.borrowedAt").value("2025-03-20T12:00:00"))
                .andExpect(jsonPath("$.returnedAt").value("2025-03-22T15:30:00"));
        verify(bookService, times(1)).returnBook(eq(1L), eq("testUser"));
    }

    @Test
    @DisplayName("Возврат книги - бронирование не найдено")
    void shouldReturnNotFoundWhenBorrowedBookNotFound() throws Exception {
        when(bookService.returnBook(eq(1L), eq("testUser")))
                .thenThrow(new BookNotFoundException(1L));
        mockMvc.perform(post("/books/{id}/return", 1L).principal(() -> "testUser"))
                .andExpect(status().isNotFound());
        verify(bookService, times(1)).returnBook(eq(1L), eq("testUser"));
    }

    @Test
    @DisplayName("Возврат книги - книга не была забронирована")
    void shouldReturnBadRequestWhenBookNotBorrowed() throws Exception {
        var bookId = 1L;
        var username = "testUser";
        when(bookService.returnBook(eq(bookId), eq(username))).thenThrow(new BookNotBorrowedException(bookId));
        mockMvc.perform(post("/books/{bookId}/return", bookId).principal(() -> username))
                .andExpect(status().isBadRequest());
        verify(bookService, times(1)).returnBook(eq(bookId), eq(username));
    }

}
