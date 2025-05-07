package com.acodexm.datatransformer.services;

import com.devtiro.book.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BooksService {
    Book save(Book book);

    Page<Book> listBooks(Pageable pagable);

}
