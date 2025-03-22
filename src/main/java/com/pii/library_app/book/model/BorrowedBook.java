package com.pii.library_app.book.model;

import com.pii.library_app.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "borrowed_books")
@Schema(description = "Бронирование книги в библиотеке")
public class BorrowedBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор бронирования")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "Идентификатор пользователя, забронировавшего книгу")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    @Schema(description = "Идентификатор книги, забронированной пользователем")
    private Book book;

    @Column(nullable = false)
    @Schema(description = "Дата и время бронирования книги")
    private LocalDateTime borrowedAt;

    @Column
    @Schema(description = "Дата и время возврата книги")
    private LocalDateTime returnedAt;

    public BorrowedBook() {}

    public BorrowedBook(User user, Book book, LocalDateTime borrowedAt) {
        this.user = user;
        this.book = book;
        this.borrowedAt = borrowedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDateTime getBorrowedAt() {
        return borrowedAt;
    }

    public void setBorrowedAt(LocalDateTime borrowedAt) {
        this.borrowedAt = borrowedAt;
    }

    public LocalDateTime getReturnedAt() {
        return returnedAt;
    }

    public void setReturnedAt(LocalDateTime returnedAt) {
        this.returnedAt = returnedAt;
    }
}
