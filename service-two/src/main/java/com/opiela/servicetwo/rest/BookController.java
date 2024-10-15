package com.opiela.servicetwo.rest;


import com.opiela.servicetwo.model.BookDto;
import com.opiela.servicetwo.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> getRentedBooks() {
        return ResponseEntity.ok(bookService.getRentedBooks());
    }
}