package com.booksApiv2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.booksApiv2.domain.Book;
import com.booksApiv2.repository.filter.BookFilter;
import com.booksApiv2.repository.projection.BookSummary;

public interface BookRepositoryQuery {
	public Page<Book> bookFilter(BookFilter bookFilter, Pageable pageable);
	public Page<BookSummary> summary(BookFilter bookFilter, Pageable pageable);
}
