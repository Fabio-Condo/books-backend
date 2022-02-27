package com.booksApiv2.exception.domain;

public class LikeNotFoundException extends Exception{
	private static final long serialVersionUID = 1L;

	public LikeNotFoundException (String message) {
		super (message);
	}
}
