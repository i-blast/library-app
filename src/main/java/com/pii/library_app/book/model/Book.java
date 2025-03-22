package com.pii.library_app.book.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Entity
@Table(name = "books")
@Schema(description = "Модель книги")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Идентификатор книги", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Название книги", example = "The Art of Multiprocessor Programming")
    private String title;

    @Column(nullable = false)
    @Schema(description = "Автор книги", example = "Maurice Herlihy, Nir Shavit, Victor Luchangco, Michael Spear")
    private String author;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Schema(description = "Жанр книги", example = "PROGRAMMING")
    private Genre genre;

    @Column(nullable = false)
    @Schema(description = "Доступность книги для бронирования", example = "true")
    private boolean available = true;

    public Book() {}

    public Book(String title, String author, Genre genre) {
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }
}
