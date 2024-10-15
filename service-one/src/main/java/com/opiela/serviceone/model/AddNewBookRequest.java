package com.opiela.serviceone.model;

import jakarta.validation.constraints.NotEmpty;

public record AddNewBookRequest(
        @NotEmpty String title,
        @NotEmpty String author,
        @NotEmpty String isbn,
        String category,
        String borrower
) {
}
