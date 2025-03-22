package com.pii.library_app.book.service;

import com.pii.library_app.book.dto.SearchBookFilterDto;
import com.pii.library_app.book.exception.BookNotAvailableException;
import com.pii.library_app.book.exception.BookNotFoundException;
import com.pii.library_app.book.model.Book;
import com.pii.library_app.book.model.BorrowedBook;
import com.pii.library_app.book.model.Genre;
import com.pii.library_app.book.repo.BookRepository;
import com.pii.library_app.book.repo.BorrowedBookRepository;
import com.pii.library_app.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.pii.library_app.util.TestDataFactory.createTestBook;
import static com.pii.library_app.util.TestDataFactory.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private BookService bookService;
    @Mock
    private BorrowedBookRepository borrowedBookRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Создание книги - успешный сценарий")
    void shouldCreateBook() {
        var book = createTestBook("451 градус по фаренгейту", "Рэй Брэдбери", Genre.DYSTOPIAN);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        var savedBook = bookService.createBook(book);
        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("451 градус по фаренгейту");
        assertThat(savedBook.getAuthor()).isEqualTo("Рэй Брэдбери");
        assertThat(savedBook.getGenre()).isEqualTo(Genre.DYSTOPIAN);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("Обновление книги - успешный сценарий")
    void shouldUpdateBook() {
        var existingBook = createTestBook("451 градус по фаренгейту", "Рэй Брэдбери", Genre.DYSTOPIAN);
        existingBook.setId(1L);

        var updatedBook = createTestBook("451 градус по фаренгейту", "Рэй Брэдбери", Genre.DYSTOPIAN);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        var result = bookService.updateBook(1L, updatedBook);
        assertThat(result.getTitle()).isEqualTo("451 градус по фаренгейту");
        assertThat(result.getAuthor()).isEqualTo("Рэй Брэдбери");
        assertThat(result.getGenre()).isEqualTo(Genre.DYSTOPIAN);
        verify(bookRepository, times(1)).save(existingBook);
    }

    @Test
    @DisplayName("Обновление книги - книга не найдена")
    void shouldThrowExceptionWhenUpdatingNonExistentBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        var exception = assertThrows(BookNotFoundException.class, () -> {
            bookService.updateBook(1L, createTestBook("451 градус по фаренгейту", "Рэй Брэдбери", Genre.DYSTOPIAN));
        });
        assertThat(exception.getMessage()).isEqualTo("Книга с ID 1 не найдена");
    }

    @Test
    @DisplayName("Удаление книги - успешный сценарий")
    void shouldDeleteBook() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        bookService.deleteBook(1L);
        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Удаление книги - книга не найдена")
    void shouldThrowExceptionWhenDeletingNonExistentBook() {
        when(bookRepository.existsById(1L)).thenReturn(false);
        var exception = assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(1L));
        assertThat(exception.getMessage()).isEqualTo("Книга с ID 1 не найдена");
    }

    @Test
    @DisplayName("Поиск книг по названию - успешный сценарий")
    void shouldSearchBooksByTitle() {
        var filter = new SearchBookFilterDto("1984", null, null);
        var book = createTestBook("1984", "George Orwell", Genre.DYSTOPIAN);
        when(bookRepository.findAll(any(Specification.class))).thenReturn(List.of(book));

        var result = bookService.searchBooks(filter);
        assertThat(result.books()).hasSize(1);
        assertThat(result.books().get(0).getTitle()).isEqualTo("1984");
        verify(bookRepository, times(1)).findAll(any(Specification.class));
    }


    @Test
    @DisplayName("Поиск книг по жанру - успешный сценарий")
    void shouldSearchBooksByGenre() {
        // Arrange
        var filter = new SearchBookFilterDto(null, null, Genre.DYSTOPIAN);
        var book = createTestBook("1984", "George Orwell", Genre.DYSTOPIAN);
        when(bookRepository.findAll(any(Specification.class))).thenReturn(List.of(book));

        var result = bookService.searchBooks(filter);
        assertThat(result.books()).hasSize(1);
        assertThat(result.books().get(0).getGenre()).isEqualTo(Genre.DYSTOPIAN);
        verify(bookRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Поиск книг по названию и автору - успешный сценарий")
    void shouldSearchBooksByTitleAndAuthor() {
        var filter = new SearchBookFilterDto("1984", "George Orwell", null);
        var book1 = createTestBook("1984", "George Orwell", Genre.DYSTOPIAN);
        var book2 = createTestBook("Animal Farm", "George Orwell", Genre.DYSTOPIAN);
        when(bookRepository.findAll(any(Specification.class))).thenReturn(List.of(book1, book2));

        var result = bookService.searchBooks(filter);
        assertThat(result.books()).hasSize(2);
        assertThat(result.books().get(0).getTitle()).isEqualTo("1984");
        assertThat(result.books().get(1).getTitle()).isEqualTo("Animal Farm");
        verify(bookRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Поиск книг без фильтров - возвращает все книги")
    void shouldReturnAllBooksWhenNoFilterProvided() {
        var filter = new SearchBookFilterDto(null, null, null);
        var book1 = createTestBook("1984", "George Orwell", Genre.DYSTOPIAN);
        var book2 = createTestBook("Animal Farm", "George Orwell", Genre.DYSTOPIAN);
        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

        var result = bookService.searchBooks(filter);
        assertThat(result.totalCount()).isEqualTo(2L);
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Бронирование книги - успешный сценарий")
    void shouldBorrowBookSuccessfully() {
        Long bookId = 1L;
        String username = "testUser";
        Book book = createTestBook("1984", "George Orwell", Genre.DYSTOPIAN);
        book.setId(bookId);
        book.setAvailable(true);
        var user = createTestUser(username, "password");
        user.setId(1L);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userService.findByUsername(username)).thenReturn(user);
        when(borrowedBookRepository.save(any(BorrowedBook.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var borrowedBook = bookService.borrowBook(bookId, username);
        assertThat(borrowedBook).isNotNull();
        assertThat(borrowedBook.getBook()).isEqualTo(book);
        assertThat(borrowedBook.getUser()).isEqualTo(user);
        assertThat(borrowedBook.getBorrowedAt()).isNotNull();
        assertThat(book.isAvailable()).isFalse();
        verify(bookRepository, times(1)).save(book);
        verify(borrowedBookRepository, times(1)).save(any(BorrowedBook.class));
    }

    @Test
    @DisplayName("Бронирование книги - книга недоступна")
    void shouldThrowExceptionWhenBookIsNotAvailable() {
        Long bookId = 1L;
        String username = "testUser";
        Book book = createTestBook("1984", "George Orwell", Genre.DYSTOPIAN);
        book.setId(bookId);
        book.setAvailable(false);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        assertThrows(BookNotAvailableException.class, () -> bookService.borrowBook(bookId, username));
        verify(bookRepository, never()).save(any(Book.class));
        verify(borrowedBookRepository, never()).save(any(BorrowedBook.class));
    }

    @Test
    @DisplayName("Бронирование книги - книга не найдена")
    void shouldThrowExceptionWhenBookNotFound() {
        Long bookId = 1L;
        String username = "testUser";
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> bookService.borrowBook(bookId, username));
        verify(bookRepository, never()).save(any(Book.class));
        verify(borrowedBookRepository, never()).save(any(BorrowedBook.class));
    }

    @Test
    @DisplayName("Возврат книги - успешный сценарий")
    void shouldReturnBookSuccessfully() {
        Long borrowedBookId = 1L;
        Book book = createTestBook("1984", "George Orwell", Genre.DYSTOPIAN);
        book.setId(1L);
        book.setAvailable(false);
        var borrowedBook = new BorrowedBook();
        borrowedBook.setId(borrowedBookId);
        borrowedBook.setBook(book);
        borrowedBook.setBorrowedAt(LocalDateTime.now());
        when(borrowedBookRepository.findById(borrowedBookId)).thenReturn(Optional.of(borrowedBook));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(borrowedBookRepository.save(any(BorrowedBook.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BorrowedBook returnedBook = bookService.returnBook(borrowedBookId);
        assertThat(returnedBook).isNotNull();
        assertThat(returnedBook.getReturnedAt()).isNotNull();
        assertThat(book.isAvailable()).isTrue();
        verify(bookRepository, times(1)).save(book);
        verify(borrowedBookRepository, times(1)).save(borrowedBook);
    }

    @Test
    @DisplayName("Возврат книги - бронирование не найдено")
    void shouldThrowExceptionWhenBorrowedBookNotFound() {
        Long borrowedBookId = 1L;
        when(borrowedBookRepository.findById(borrowedBookId)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.returnBook(borrowedBookId));
        verify(bookRepository, never()).save(any(Book.class));
        verify(borrowedBookRepository, never()).save(any(BorrowedBook.class));
    }
}