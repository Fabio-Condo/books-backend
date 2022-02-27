package com.booksApiv2.repository.projection;

import java.util.Date;

public class BookSummary {
	private Long id;
	private String name;
	private String bookImageUrl;
	private Date publicationDate;
	private String publishingCompany;
	private String summary;
	private String author;

	public BookSummary(Long id, String name, String bookImageUrl, Date publicationDate, String publishingCompany, String summary, String author) {
		super();
		this.id = id;
		this.name = name;
		this.bookImageUrl = bookImageUrl;
		this.publicationDate = publicationDate;
		this.publishingCompany = publishingCompany;
		this.summary = summary;
		this.author = author;
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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

}
