package com.pii.library_app.book.service;

import com.pii.library_app.book.dto.CreateBookDto;
import com.pii.library_app.book.dto.SearchBookFilterDto;
import com.pii.library_app.book.dto.SearchBookResponseDto;
import com.pii.library_app.book.exception.BookNotAvailableException;
import com.pii.library_app.book.exception.BookNotBorrowedException;
import com.pii.library_app.book.exception.BookNotFoundException;
import com.pii.library_app.book.model.Book;
import com.pii.library_app.book.model.BorrowedBook;
import com.pii.library_app.book.repo.BookRepository;
import com.pii.library_app.book.repo.BorrowedBookRepository;
import com.pii.library_app.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BookService {

    private final Logger LOG = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;
    private final UserService userService;
    private final BorrowedBookRepository borrowedBookRepository;

    public BookService(
            UserService userService,
            BorrowedBookRepository borrowedBookRepository,
            BookRepository bookRepository
    ) {
        this.bookRepository = bookRepository;
        this.userService = userService;
        this.borrowedBookRepository = borrowedBookRepository;
    }

    @Transactional
    public Book createBook(CreateBookDto dto) {
        return bookRepository.save(dto.toBook());
    }

    @Transactional
    public Book updateBook(Long id, CreateBookDto dto) {
        var foundResult = bookRepository.findById(id);
        if (foundResult.isEmpty()) {
            throw new BookNotFoundException(id);
        }
        var existingBook = foundResult.get();
        existingBook.setTitle(dto.title());
        existingBook.setAuthor(dto.author());
        existingBook.setGenre(dto.genre());
        return bookRepository.save(existingBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        bookRepository.deleteById(id);
    }

    public SearchBookResponseDto searchBooks(SearchBookFilterDto filter) {
        if (isFilterEmpty(filter)) {
            var books = bookRepository.findAll();
            return new SearchBookResponseDto(books, books.size());
        }

        Specification<Book> spec = Specification.where(null);
        spec = addLikeSpec(spec, "title", filter.title());
        spec = addLikeSpec(spec, "author", filter.author());
        spec = addEqualSpec(spec, "genre", filter.genre());

        var books = bookRepository.findAll(spec);
        return new SearchBookResponseDto(books, books.size());
    }

    private boolean isFilterEmpty(SearchBookFilterDto filter) {
        return filter.title() == null &&
                filter.author() == null &&
                filter.genre() == null;
    }

    private Specification<Book> addLikeSpec(Specification<Book> spec, String field, String value) {
        if (value == null) return spec;
        return spec.and((root, query, cb) ->
                cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%"));
    }

    private Specification<Book> addEqualSpec(Specification<Book> spec, String field, Object value) {
        if (value == null) return spec;
        return spec.and((root, query, cb) -> cb.equal(root.get(field), value));
    }

    @Transactional
    public BorrowedBook borrowBook(Long bookId, String username) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        if (!book.isAvailable()) {
            throw new BookNotAvailableException(bookId);
        }

        book.setAvailable(false);
        bookRepository.save(book);
        var user = userService.findByUsername(username);

        var borrowedBook = new BorrowedBook(user, book, LocalDateTime.now());
        LOG.info("➤➤➤➤➤➤➤ Книга '{}' бронируется пользователем {}", bookId, username);
        return borrowedBookRepository.save(borrowedBook);
    }

    @Transactional
    public BorrowedBook returnBook(Long bookId, String username) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        var user = userService.findByUsername(username);
        var borrowedBook = borrowedBookRepository.findByUserAndBookAndReturnedAtIsNull(user, book)
                .orElseThrow(() -> new BookNotBorrowedException(bookId));

        book.setAvailable(true);
        bookRepository.save(book);

        borrowedBook.setReturnedAt(LocalDateTime.now());
        var saved = borrowedBookRepository.save(borrowedBook);
        LOG.info("➤➤➤➤➤➤➤ Книга '{}' возвращена в библиотеку пользователем '{}'", book.getId(), username);
        return saved;
    }
}
