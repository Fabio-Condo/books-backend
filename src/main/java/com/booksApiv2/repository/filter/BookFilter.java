package com.booksApiv2.repository.filter;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class BookFilter {
	private String name;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date publicationDateBegin;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date publicationDateEnd;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getPublicationDateBegin() {
		return publicationDateBegin;
	}

	public void setPublicationDateBegin(Date publicationDateBegin) {
		this.publicationDateBegin = publicationDateBegin;
	}

	public Date getPublicationDateEnd() {
		return publicationDateEnd;
	}

	public void setPublicationDateEnd(Date publicationDateEnd) {
		this.publicationDateEnd = publicationDateEnd;
	}

}
