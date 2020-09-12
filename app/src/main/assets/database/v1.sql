CREATE TABLE `db_version` (
    `version`   INTEGER   NOT NULL    PRIMARY KEY
);

CREATE TABLE `genres` (
    `_id`       INTEGER         NOT NULL    PRIMARY KEY     AUTOINCREMENT,
    `name`      VARCHAR(255)    NOT NULL
);

CREATE TABLE `series` (
    `_id`       INTEGER         NOT NULL    PRIMARY KEY     AUTOINCREMENT,
    `name`      VARCHAR(255)    NOT NULL,
    `complete`  INTEGER         NOT NULL   DEFAULT 0
);

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
    `url`               TEXT            NULL
);
