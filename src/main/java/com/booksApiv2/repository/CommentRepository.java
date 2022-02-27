package com.booksApiv2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booksApiv2.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>{
	List<Comment> findCommentByBook(Long bookId);
}
