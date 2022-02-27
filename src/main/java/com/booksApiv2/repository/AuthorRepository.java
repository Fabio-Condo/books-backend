package com.booksApiv2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.booksApiv2.domain.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
	public Page<Author> findByNameContaining(String name, Pageable pageable);

}
