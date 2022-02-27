package com.booksApiv2.domain;

import static com.booksApiv2.domain.constant.CommentConstant.MESSAGE_MAX_SIZE;
import static com.booksApiv2.domain.constant.CommentConstant.TEXT_CAN_NOT_BE_EMPTY;
import static com.booksApiv2.domain.constant.CommentConstant.USER_CAN_NOT_BE_NULL;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "comment")
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	private Long id;
	@NotEmpty(message = TEXT_CAN_NOT_BE_EMPTY)
	@Size(max = 1500, message = MESSAGE_MAX_SIZE)
	@JsonProperty("comment") 
	private String text;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date commentDate;
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "book_id")
	private Book book;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@JsonIgnoreProperties(value={"hibernateLazyInitializer"})
	@NotNull(message = USER_CAN_NOT_BE_NULL)
	private User user;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Date getCommentDate() {
		return commentDate;
	}
	public void setCommentDate(Date commentDate) {
		this.commentDate = commentDate;
	}
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	


}
