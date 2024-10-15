package com.aopiela.kafka.model;


public record BookMessage(
        String title,
        String author,
        String isbn,
        String category,
        String borrower
) {
}