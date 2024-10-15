package com.opiela.serviceone.service;

import com.opiela.serviceone.domain.Book;
import com.opiela.serviceone.domain.BookRepository;
import com.aopiela.kafka.model.BookMessage;
import com.opiela.serviceone.exception.BookAlreadyExistsException;
import com.opiela.serviceone.exception.BookNotFoundException;
import com.opiela.serviceone.model.AddNewBookRequest;
import com.opiela.serviceone.model.BookDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private KafkaTemplate<String, BookMessage> kafkaTemplate;

    private BookService bookService;

    private final String BOOK_TOPIC = "test-topic";

    @BeforeEach
    void setUp() {
        bookService = new BookService(bookRepository, kafkaTemplate, BOOK_TOPIC);
    }

    @Test
    void getAllBooks_shouldReturnListOfBookDtos() {
        // Given
        Book book1 = new Book("1234", "Title1", "Author1", "Category1", null);
        Book book2 = new Book("5678", "Title2", "Author2", "Category2", null);
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

        // When
        List<BookDto> result = bookService.getAllBooks();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).isbn()).isEqualTo("1234");
        assertThat(result.get(1).isbn()).isEqualTo("5678");
    }

    @Test
    void addBook_shouldSaveBookAndSendKafkaMessage() {
        // Given
        AddNewBookRequest request = new AddNewBookRequest("Title", "Author", "1234", "Category", null);
        when(bookRepository.existsByIsbn("1234")).thenReturn(false);

        // When
        bookService.addBook(request);

        // Then
        verify(bookRepository).save(any(Book.class));
        verify(kafkaTemplate).send(eq(BOOK_TOPIC), any(BookMessage.class));
    }

    @Test
    void addBook_shouldThrowExceptionWhenBookAlreadyExists() {
        // Given
        AddNewBookRequest request = new AddNewBookRequest("Title", "Author", "1234", "Category", null);
        when(bookRepository.existsByIsbn("1234")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> bookService.addBook(request))
                .isInstanceOf(BookAlreadyExistsException.class)
                .hasMessageContaining("1234");
    }

    @Test
    void rentBook_shouldUpdateBookAndSendKafkaMessage() {
        // Given
        Book book = new Book("1234", "Title", "Author", "Category", null);
        when(bookRepository.findByIsbn("1234")).thenReturn(Optional.of(book));

        // When
        bookService.rentBook("John", "1234");

        // Then
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookCaptor.capture());
        assertThat(bookCaptor.getValue().getBorrower()).isEqualTo("John");

        ArgumentCaptor<BookMessage> messageCaptor = ArgumentCaptor.forClass(BookMessage.class);
        verify(kafkaTemplate).send(eq(BOOK_TOPIC), messageCaptor.capture());
        assertThat(messageCaptor.getValue().borrower()).isEqualTo("John");
    }

    @Test
    void rentBook_shouldThrowExceptionWhenBookNotFound() {
        // Given
        when(bookRepository.findByIsbn("1234")).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> bookService.rentBook("John", "1234"))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("1234");
    }

    @Test
    void convertToMessage_shouldCreateCorrectBookMessage() {
        // Given
        Book book = new Book("1234", "Title", "Author", "Category", "John");

        // When
        BookMessage result = bookService.convertToMessage(book);

        // Then
        assertThat(result.isbn()).isEqualTo("1234");
        assertThat(result.title()).isEqualTo("Title");
        assertThat(result.author()).isEqualTo("Author");
        assertThat(result.category()).isEqualTo("Category");
        assertThat(result.borrower()).isEqualTo("John");
    }

    @Test
    void convertToEntity_shouldCreateCorrectBook() {
        // Given
        AddNewBookRequest request = new AddNewBookRequest("Title", "Author", "1234", "Category", null);

        // When
        Book result = bookService.convertToEntity(request);

        // Then
        assertThat(result.getIsbn()).isEqualTo("1234");
        assertThat(result.getTitle()).isEqualTo("Title");
        assertThat(result.getAuthor()).isEqualTo("Author");
        assertThat(result.getCategory()).isEqualTo("Category");
        assertThat(result.getBorrower()).isNull();
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