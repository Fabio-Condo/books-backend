package com.booksApiv2.domain;

import static com.booksApiv2.domain.constant.BookConstant.NAME_MUST_BE_PROVIDED;
import static com.booksApiv2.domain.constant.BookConstant.PUBLISHER_MANDATORY;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "book")
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	private Long id;
	@NotNull(message = NAME_MUST_BE_PROVIDED)
	private String name;
	private String bookImageUrl;
	private String bookPdfUrl;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date publicationDate;
	@NotNull(message = PUBLISHER_MANDATORY)
	private String publishingCompany;
	private String summary;
	@ManyToOne
	@JoinColumn(name = "author_id")
	private Author author;
	@OneToMany(mappedBy = "book") 
	private List<Comment> comments;
	@OneToMany(mappedBy = "book")
	private List<Like> likes;

	public Book(){}
	
	public Book(Long id, @NotNull(message = "The name must be provided.") String name, String bookImageUrl,String bookPdfUrl, Date publicationDate,@NotNull(message = "Publisher field is mandatory.") String publishingCompany, String summary, Author author,List<Comment> comments, List<Like> likes) {
		super();
		this.id = id;
		this.name = name;
		this.bookImageUrl = bookImageUrl;
		this.bookPdfUrl = bookPdfUrl;
		this.publicationDate = publicationDate;
		this.publishingCompany = publishingCompany;
		this.summary = summary;
		this.author = author;
		this.comments = comments;
		this.likes = likes;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBookImageUrl() {
		return bookImageUrl;
	}

	public void setBookImageUrl(String bookImageUrl) {
		this.bookImageUrl = bookImageUrl;
	}

	public String getBookPdfUrl() {
		return bookPdfUrl;
	}

	public void setBookPdfUrl(String bookPdfUrl) {
		this.bookPdfUrl = bookPdfUrl;
	}

	public Date getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	public String getPublishingCompany() {
		return publishingCompany;
	}

	public void setPublishingCompany(String publishingCompany) {
		this.publishingCompany = publishingCompany;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<Like> getLikes() {
		return likes;
	}

	public void setLikes(List<Like> likes) {
		this.likes = likes;
	}	
}
