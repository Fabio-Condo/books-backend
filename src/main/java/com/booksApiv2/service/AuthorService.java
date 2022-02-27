package com.booksApiv2.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.booksApiv2.domain.Author;
import com.booksApiv2.exception.domain.AuthorNotFoundException;
import com.booksApiv2.repository.AuthorRepository;

@Service
public class AuthorService {

	private static final String NO_AUTHOR_FOUND_BY_ID = "No author found by id: ";

	@Autowired
	private AuthorRepository authorRepository;

	public Page<Author> getAllAuthors(String name, Pageable pageable) {
		return authorRepository.findByNameContaining(name, pageable);
	}

	public Author getExistAuthor(Long id) throws AuthorNotFoundException {
		Author authorSaved = authorRepository.findById(id)
				.orElseThrow(() -> new AuthorNotFoundException(NO_AUTHOR_FOUND_BY_ID + id));
		return authorSaved;
	}

	public Author saveAuthor(Author author) {
		return authorRepository.save(author);
	}

	public Author updateAuthor(Long id, Author author) throws AuthorNotFoundException {
		Author authorSaved = getExistAuthor(id);
		BeanUtils.copyProperties(author, authorSaved, "id");
		return authorRepository.save(authorSaved);
	}

	public void deleteAuthor(Long id) throws AuthorNotFoundException {
		getExistAuthor(id);
		authorRepository.deleteById(id);
	}

	public void upatePropertyActive(Long id, Boolean active) {
		Author authorSaved = authorRepository.getById(id);
		authorSaved.setActive(active);
		authorRepository.save(authorSaved);
	}
}
