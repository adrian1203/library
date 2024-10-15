package com.opiela.servicetwo.service;


import com.aopiela.kafka.model.BookMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookKafkaListener {

    private final BookService bookService;

    @KafkaListener(topics = "${spring.kafka.topic.book}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(BookMessage bookMessage) {
        log.debug("Received book information: {}", bookMessage);
        bookService.updateOrCreateBook(bookMessage);
    }
}