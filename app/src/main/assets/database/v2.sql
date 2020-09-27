CREATE TABLE `books_genres` (
    `book_id`       INTEGER         NOT NULL,
    `genre_id`      INTEGER         NOT NULL,

    PRIMARY KEY (book_id, genre_id)
);
