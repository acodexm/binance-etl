package com.acodexm.binanceconnector.scheduled;

import com.devtiro.book.publisher.repositories.BookRepository;
import com.devtiro.book.publisher.services.BookPublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Publishes books at a fixed interval.
 */
@Component
@Slf4j
public class ScheduledBookPublisher {

    private long counter;

    private final BookRepository bookRepository;

    private final BookPublisherService bookPublisherService;

    public ScheduledBookPublisher(final BookPublisherService bookPublisherService, final BookRepository bookRepository) {
        resetCounter();
        this.bookPublisherService = bookPublisherService;
        this.bookRepository = bookRepository;
    }

    @Scheduled(cron = "0/20 * * * * *")
    public void publishBook() {
        bookRepository.findById(counter).ifPresentOrElse(book -> {
            counter += 1;
            bookPublisherService.publish(book);
            log.info("Book '{}' [{}] published.", book.getTitle(), book.getIsbn());
        }, () -> resetCounter());
    }

    private void resetCounter() {
        this.counter = 1;
    }

}
