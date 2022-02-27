package com.booksApiv2.exception.domain;

public class OwnerCommentNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public OwnerCommentNotFoundException(String message) {
        super(message);
    }
}
