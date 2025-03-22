package com.pii.library_app.book.service;

import com.pii.library_app.book.dto.SearchBookFilterDto;
import com.pii.library_app.book.dto.SearchBookResponseDto;
import com.pii.library_app.book.exception.BookNotFoundException;
import com.pii.library_app.book.model.Book;
import com.pii.library_app.book.repo.BookRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(
            BookRepository bookRepository
    ) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Long id, Book updatedBook) {
        return bookRepository.findById(id)
                .map(existingBook -> {
                    existingBook.setTitle(updatedBook.getTitle());
                    existingBook.setAuthor(updatedBook.getAuthor());
                    existingBook.setGenre(updatedBook.getGenre());
                    existingBook.setAvailable(updatedBook.isAvailable());
                    return bookRepository.save(existingBook);
                })
                .orElseThrow(() -> new BookNotFoundException(id));
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
}
