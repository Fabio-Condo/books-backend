package com.booksApiv2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.booksApiv2.domain.Book;

public interface BookRepository extends JpaRepository<Book,Long> , BookRepositoryQuery{
	public Page<Book> findByNameContaining(String name, Pageable pageable);
}
