package com.booksApiv2.domain;

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
import javax.validation.constraints.NotNull;

import com.booksApiv2.enumeration.LikeType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "likes")
public class Like {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	private Long id;
	@JsonProperty("type")
	private LikeType likeType;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date likeDate;
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
	public LikeType getLikeType() {
		return likeType;
	}
	public void setLikeType(LikeType likeType) {
		this.likeType = likeType;
	}
	public Date getLikeDate() {
		return likeDate;
	}
	public void setLikeDate(Date likeDate) {
		this.likeDate = likeDate;
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
