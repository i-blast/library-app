package com.pii.library_app.book.repo;

import com.pii.library_app.book.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
