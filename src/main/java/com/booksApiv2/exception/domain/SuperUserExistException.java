package com.booksApiv2.exception.domain;

public class SuperUserExistException extends Exception {
	private static final long serialVersionUID = 1L;

	public SuperUserExistException(String message) {
        super(message);
    }
}
