package com.opiela.serviceone.exception;

public class BookNotFoundException extends BusinessException {

    private final static String MESSAGE = "Book not found: ";

    public BookNotFoundException(String isbn) {
        super(MESSAGE + isbn);
    }
}
