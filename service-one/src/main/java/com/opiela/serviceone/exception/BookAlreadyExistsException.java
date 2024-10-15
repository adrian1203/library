package com.opiela.serviceone.exception;

public class BookAlreadyExistsException extends BusinessException {
    private static final String MESSAGE = "Book with ISBN already exists: ";

    public BookAlreadyExistsException(String isbn) {
        super(MESSAGE + isbn);
    }
}