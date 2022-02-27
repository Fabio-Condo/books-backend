package com.booksApiv2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booksApiv2.domain.Book;
import com.booksApiv2.domain.Like;
import com.booksApiv2.domain.User;

public interface LikeRepository extends JpaRepository<Like, Long>{
	Like findLikeByUserAndBook(User user, Book book);
}
