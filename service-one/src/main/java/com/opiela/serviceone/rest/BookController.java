package com.opiela.serviceone.rest;


import com.opiela.serviceone.model.AddNewBookRequest;
import com.opiela.serviceone.model.BookDto;
import com.opiela.serviceone.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    public ResponseEntity<List<BookDto>> getBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @PostMapping("/")
    public ResponseEntity<Void> addBook(@Valid @RequestBody AddNewBookRequest request) {
        bookService.addBook(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/")
    public ResponseEntity<Void> rentBook(@RequestParam String clientName, @RequestParam String isbn) {
        bookService.rentBook(clientName, isbn);
        return ResponseEntity.ok().build();
    }
}