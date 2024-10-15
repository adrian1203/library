package com.opiela.servicetwo.service;

import com.opiela.servicetwo.domain.Book;
import com.opiela.servicetwo.domain.BookRepository;
import com.aopiela.kafka.model.BookMessage;
import com.opiela.servicetwo.model.BookDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService(bookRepository);
    }

    @Test
    void getRentedBooks_shouldReturnListOfRentedBookDtos() {
        // Given
        Book rentedBook1 = new Book("1234", "Title1", "Author1", "Category1", "John");
        Book rentedBook2 = new Book("5678", "Title2", "Author2", "Category2", "Jane");
        when(bookRepository.findByBorrowerIsNotNull()).thenReturn(Arrays.asList(rentedBook1, rentedBook2));

        // When
        List<BookDto> result = bookService.getRentedBooks();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).isbn()).isEqualTo("1234");
        assertThat(result.get(0).borrower()).isEqualTo("John");
        assertThat(result.get(1).isbn()).isEqualTo("5678");
        assertThat(result.get(1).borrower()).isEqualTo("Jane");
    }

    @Test
    void updateOrCreateBook_shouldUpdateExistingBook() {
        // Given
        String isbn = "1234";
        Book existingBook = new Book(isbn, "OldTitle", "OldAuthor", "OldCategory", null);
        BookMessage bookMessage = new BookMessage("NewTitle", "NewAuthor", isbn, "NewCategory", "John");
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(existingBook));

        // When
        bookService.updateOrCreateBook(bookMessage);

        // Then
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookCaptor.capture());
        Book savedBook = bookCaptor.getValue();
        assertThat(savedBook.getIsbn()).isEqualTo(isbn);
        assertThat(savedBook.getTitle()).isEqualTo("NewTitle");
        assertThat(savedBook.getAuthor()).isEqualTo("NewAuthor");
        assertThat(savedBook.getCategory()).isEqualTo("NewCategory");
        assertThat(savedBook.getBorrower()).isEqualTo("John");
    }

    @Test
    void updateOrCreateBook_shouldCreateNewBook() {
        // Given
        String isbn = "1234";
        BookMessage bookMessage = new BookMessage("Title", "Author", isbn, "Category", "John");
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        // When
        bookService.updateOrCreateBook(bookMessage);

        // Then
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookCaptor.capture());
        Book savedBook = bookCaptor.getValue();
        assertThat(savedBook.getIsbn()).isEqualTo(isbn);
        assertThat(savedBook.getTitle()).isEqualTo("Title");
        assertThat(savedBook.getAuthor()).isEqualTo("Author");
        assertThat(savedBook.getCategory()).isEqualTo("Category");
        assertThat(savedBook.getBorrower()).isEqualTo("John");
    }

    @Test
    void convertToBookDto_shouldCreateCorrectBookDto() {
        // Given
        Book book = new Book("1234", "Title", "Author", "Category", "John");

        // When
        BookDto result = bookService.convertToBookDto(book);

        // Then
        assertThat(result.isbn()).isEqualTo("1234");
        assertThat(result.title()).isEqualTo("Title");
        assertThat(result.author()).isEqualTo("Author");
        assertThat(result.category()).isEqualTo("Category");
        assertThat(result.borrower()).isEqualTo("John");
    }
}