package com.opiela.serviceone.model;


public record BookDto(
        String title,
        String author,
        String isbn,
        String category,
        String borrower
) {
}
