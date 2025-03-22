package com.pii.library_app.book.repo;

import com.pii.library_app.book.model.BorrowedBook;
import com.pii.library_app.book.model.Genre;
import com.pii.library_app.user.model.Role;
import com.pii.library_app.user.repo.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.pii.library_app.util.TestDataFactory.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BorrowedBookRepositoryTest {

    @Autowired
    private BorrowedBookRepository borrowedBookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Должен найти все заимствованные пользователем книги")
    void shouldFindBorrowedBooksByUser() {
        var user = createTestUser("testuser", "password");
        user.addAll(Set.of(Role.USER));
        userRepository.save(user);
        var book1 = createTestBook(
                "Java Concurrency in Practice",
                "Brian Goetz",
                Genre.PROGRAMMING
        );
        var book2 = createTestBook(
                "Practical Unit Testing with JUnit and Mockito",
                "Tomek Kaczanowski",
                Genre.PROGRAMMING
        );
        bookRepository.saveAll(List.of(book1, book2));

        var borrowed1 = createTestBorrowedBook(user, book1, LocalDateTime.now());
        var borrowed2 = createTestBorrowedBook(user, book2, LocalDateTime.now());
        borrowedBookRepository.saveAll(List.of(borrowed1, borrowed2));

        var borrowedBooks = borrowedBookRepository.findByUser(user);
        assertThat(borrowedBooks).hasSize(2);
        assertThat(borrowedBooks).extracting(BorrowedBook::getBook).containsExactly(book1, book2);
    }

    @Test
    @DisplayName("Должен найти все заимствования книги")
    void shouldFindBorrowedBooksByBook() {
        var user1 = createTestUser("user1", "pass1");
        user1.addAll(Set.of(Role.USER));
        var user2 = createTestUser("user2", "pass2");
        user2.addAll(Set.of(Role.USER, Role.ADMIN));
        userRepository.saveAll(List.of(user1, user2));
        var book = createTestBook("Фаренгейт 451", "Рэй Брэдбери", Genre.DYSTOPIAN);
        bookRepository.save(book);

        var borrowed1 = createTestBorrowedBook(user1, book, LocalDateTime.now());
        var borrowed2 = createTestBorrowedBook(user2, book, LocalDateTime.now());
        borrowedBookRepository.saveAll(List.of(borrowed1, borrowed2));

        List<BorrowedBook> borrowedBooks = borrowedBookRepository.findByBook(book);
        assertThat(borrowedBooks).hasSize(2);
        assertThat(borrowedBooks).extracting(BorrowedBook::getUser).containsExactlyInAnyOrder(user1, user2);
    }

    @Test
    @DisplayName("У заимствованной книги не должно быть даты возврата")
    void shouldFindUnreturnedBorrowedBookByUserAndBook() {
        var user = createTestUser("testuser", "password");
        user.addAll(Set.of(Role.USER));
        userRepository.save(user);
        var book = createTestBook("Фаренгейт 451", "Рэй Брэдбери", Genre.DYSTOPIAN);
        bookRepository.save(book);
        var borrowed = createTestBorrowedBook(user, book, LocalDateTime.now());
        borrowedBookRepository.save(borrowed);

        var foundBorrowedBook = borrowedBookRepository.findByUserAndBookAndReturnedAtIsNull(user, book);
        assertThat(foundBorrowedBook).isPresent();
        assertThat(foundBorrowedBook.get().getUser()).isEqualTo(user);
        assertThat(foundBorrowedBook.get().getBook()).isEqualTo(book);
        assertThat(foundBorrowedBook.get().getReturnedAt()).isNull();
    }

    @AfterEach
    void tearDown() {}
}
