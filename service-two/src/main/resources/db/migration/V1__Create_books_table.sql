CREATE TABLE books
(
    isbn     VARCHAR(255) PRIMARY KEY,
    title    VARCHAR(255) NOT NULL,
    author   VARCHAR(255) NOT NULL,
    category VARCHAR(255),
    borrower VARCHAR(255)
);