package com.pii.library_app.user.repo;

import com.pii.library_app.book.model.Book;
import com.pii.library_app.book.model.BorrowedBook;
import com.pii.library_app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BorrowedBookRepository extends JpaRepository<BorrowedBook, Long> {

    List<BorrowedBook> findByUser(User user);

    List<BorrowedBook> findByBook(Book book);

    Optional<BorrowedBook> findByUserAndBookAndReturnedAtIsNull(User user, Book book);

}
