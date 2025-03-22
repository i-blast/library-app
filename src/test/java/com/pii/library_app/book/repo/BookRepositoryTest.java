package com.pii.library_app.book.repo;

import com.pii.library_app.book.model.Genre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static com.pii.library_app.util.TestDataFactory.createTestBook;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Должен сохранять и находить книгу по ID")
    void shouldSaveAndFindBookById() {
        var book = createTestBook("451 градус по фаренгейту", "Рэй Брэдбери", Genre.DYSTOPIAN);
        var savedBook = bookRepository.save(book);
        var foundBook = bookRepository.findById(savedBook.getId());
        assertFalse(foundBook.isEmpty());
        assertThat(foundBook.get().getTitle()).isEqualTo("451 градус по фаренгейту");
    }

    @Test
    @DisplayName("Должен находить все книги")
    void shouldFindAllBooks() {
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
        List.of(book1, book2).forEach(bookRepository::save);
        assertThat(bookRepository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("Должен удалять книгу")
    void shouldDeleteBook() {
        var book = createTestBook("451 градус по фаренгейту", "Рэй Брэдбери", Genre.DYSTOPIAN);
        var savedBook = bookRepository.save(book);
        bookRepository.deleteById(savedBook.getId());
        assertTrue(bookRepository.findById(savedBook.getId()).isEmpty());
    }
}
