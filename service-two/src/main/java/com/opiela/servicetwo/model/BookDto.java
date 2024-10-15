package com.opiela.servicetwo.model;


public record BookDto(
        String title,
        String author,
        String isbn,
        String category,
        String borrower
) {
}
