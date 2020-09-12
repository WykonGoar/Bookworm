INSERT INTO genres(_id, name) VALUES
    (1, "Fantasy"),
    (2, "Roman")
;

INSERT INTO series(_id, name) VALUES
    (1, "Percy Jackson and the Olympians")
;

INSERT INTO books(author_last_name, author_first_name, title, rating, book_number, serie, genre) VALUES
    ("Wouter", NULL, "Life as Jezus", 0.00, -1.0, NULL, NULL),
    ("Riordan", "Rick", "The Lightning Thief", 3.25, 1.0, 1, 1)
;

UPDATE books
SET description = "Twelve-year-old Percy Jackson is on the most dangerous quest of his life. With the help of a satyr and a daughter of Athena, Percy must journey across the United States to catch a thief who has stolen the original weapon of mass destruction — Zeus’ master bolt. Along the way, he must face a host of mythological enemies determined to stop him. Most of all, he must come to terms with a father he has never known, and an Oracle that has warned him of betrayal by a friend."
WHERE title = "The Lightning Thief";
