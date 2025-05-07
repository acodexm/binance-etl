package com.acodexm.datatransformer.repositories;

import com.devtiro.book.domain.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BookRepository extends CrudRepository<Book, String>,
        PagingAndSortingRepository<Book, String> {
}
