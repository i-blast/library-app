package com.pii.library_app.book.service;

import com.pii.library_app.book.exception.BookNotFoundException;
import com.pii.library_app.book.model.Book;
import com.pii.library_app.book.repo.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(
            BookRepository bookRepository
    ) {
        this.bookRepository = bookRepository;
    }


    public List<Book> getAllBooks() {
        return bookRepository.findAll();
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
}
