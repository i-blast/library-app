-- sdf
ALTER TABLE books
    DROP CONSTRAINT IF EXISTS books_genre_check;

ALTER TABLE books
    ADD CONSTRAINT books_genre_check
        CHECK (genre IN ('FANTASY', 'DETECTIVE', 'ROMANCE', 'SCIENCE_FICTION', 'THRILLER', 'NON_FICTION', 'DYSTOPIAN',
                         'PROGRAMMING'));
