package com.booksApiv2.exception.domain;

public class NotAPdfFileException extends Exception {
	private static final long serialVersionUID = 1L;

	public NotAPdfFileException(String message) {
        super(message);
    }
}

