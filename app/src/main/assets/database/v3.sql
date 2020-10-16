ALTER TABLE `books` RENAME TO `_books_old`;

CREATE TABLE `books` (
    `_id`               INTEGER         NOT NULL    PRIMARY KEY     AUTOINCREMENT,
    `title`             VARCHAR(255)    NOT NULL,
    `author_last_name`  VARCHAR(255)    NOT NULL,
    `author_first_name` VARCHAR(255)    NULL,
    `rating`            DECIMAL(3, 2)   NOT NULL    DEFAULT 0.00,
    `book_number`       DECIMAL(4, 1)   NOT NULL    DEFAULT -1.0,
    `serie`             INTEGER         NULL,
    `description`       TEXT            NULL,
    `genre`             INTEGER         NULL,
    `url`               TEXT            NULL,
    `is_wish`           INTEGER         NOT NULL    DEFAULT 0,
    `release_date`      VARCHAR(10)     NULL
);

INSERT INTO `books` (`_id`, `title`, `author_last_name`, `author_first_name`, `rating`, `book_number`, `serie`, `description`, `genre`, `url`)
  SELECT `_id`, `title`, `author_last_name`, `author_first_name`, `rating`, `book_number`, `serie`, `description`, `genre`, `url`
  FROM `_books_old`
;

DROP TABLE `_books_old`;
