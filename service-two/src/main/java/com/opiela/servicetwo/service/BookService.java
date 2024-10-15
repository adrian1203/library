package com.opiela.servicetwo.service;


import com.opiela.servicetwo.domain.Book;
import com.opiela.servicetwo.domain.BookRepository;
import com.aopiela.kafka.model.BookMessage;
import com.opiela.servicetwo.model.BookDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookDto> getRentedBooks() {
        return bookRepository.findByBorrowerIsNotNull().stream()
                .map(this::convertToBookDto)
                .collect(Collectors.toList());
    }

    public void updateOrCreateBook(BookMessage bookMessage) {
        Book book = bookRepository.findByIsbn(bookMessage.isbn())
                .orElse(new Book());
        book.setAuthor(bookMessage.author());
        book.setIsbn(bookMessage.isbn());
        book.setCategory(bookMessage.category());
        book.setTitle(bookMessage.title());
        book.setBorrower(bookMessage.borrower());
        bookRepository.save(book);
    }


    protected BookDto convertToBookDto(Book book) {
        return new BookDto(book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getCategory(),
                book.getBorrower());
    }
}