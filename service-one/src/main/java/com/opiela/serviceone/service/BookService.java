package com.opiela.serviceone.service;


import com.opiela.serviceone.domain.Book;
import com.opiela.serviceone.domain.BookRepository;
import com.aopiela.kafka.model.BookMessage;
import com.opiela.serviceone.exception.BookAlreadyExistsException;
import com.opiela.serviceone.exception.BookNotFoundException;
import com.opiela.serviceone.model.AddNewBookRequest;
import com.opiela.serviceone.model.BookDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final KafkaTemplate<String, BookMessage> kafkaTemplate;
    private final String bookTopic;

    public BookService(BookRepository bookRepository,
                       KafkaTemplate<String, BookMessage> kafkaTemplate,
                       @Value("${spring.kafka.topic.book}") String bookTopic) {
        this.bookRepository = bookRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.bookTopic = bookTopic;
    }

    @Transactional(readOnly = true)
    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::convertToBookDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addBook(AddNewBookRequest bookDTO) {
        if (bookRepository.existsByIsbn(bookDTO.isbn())) {
            throw new BookAlreadyExistsException(bookDTO.isbn());
        }

        Book book = convertToEntity(bookDTO);
        bookRepository.save(book);
        kafkaTemplate.send(bookTopic, convertToMessage(book));
    }

    @Transactional
    public void rentBook(String clientName, String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
        book.setBorrower(clientName);
        bookRepository.save(book);

        BookMessage bookMessage = convertToMessage(book);
        kafkaTemplate.send(bookTopic, bookMessage);
    }

    protected BookMessage convertToMessage(Book book) {
        return new BookMessage(book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getCategory(),
                book.getBorrower());
    }

    protected Book convertToEntity(AddNewBookRequest bookDTO) {
        return new Book(
                bookDTO.isbn(),
                bookDTO.title(),
                bookDTO.author(),
                bookDTO.category(),
                bookDTO.borrower()
        );
    }


    protected BookDto convertToBookDto(Book book) {
        return new BookDto(book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getCategory(),
                book.getBorrower());
    }

}