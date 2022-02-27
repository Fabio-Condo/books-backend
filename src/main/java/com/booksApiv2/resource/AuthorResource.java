package com.booksApiv2.resource;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.booksApiv2.domain.Author;
import com.booksApiv2.domain.HttpResponse;
import com.booksApiv2.exception.domain.AuthorNotFoundException;
import com.booksApiv2.exception.ExceptionHandling;
import com.booksApiv2.service.AuthorService;

@RestController
@RequestMapping(path = { "/", "/authors" })
public class AuthorResource extends ExceptionHandling {

	private static final String AUTHOR_DELETED_SUCCESSFULLY = "Author deleted successfully";

	@Autowired
	AuthorService authorService;

	@GetMapping
	@PreAuthorize("hasAnyAuthority('author:read')")
	public Page<Author> getAllAuthors(@RequestParam(required = false, defaultValue = "") String name, Pageable pageable) {
		return authorService.getAllAuthors(name, pageable);
	}

	@PostMapping
	@PreAuthorize("hasAnyAuthority('author:create')")
	public ResponseEntity<Author> saveAuthor(@Valid @RequestBody Author author) {
		Author authorSaved = authorService.saveAuthor(author);
		return ResponseEntity.status(HttpStatus.OK).body(authorSaved);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('author:read')")
	public ResponseEntity<Author> getSingleAuthor(@PathVariable("id") Long id) throws AuthorNotFoundException {
		Author authorSaved = authorService.getExistAuthor(id);
		return ResponseEntity.status(HttpStatus.OK).body(authorSaved);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('author:update')")
	public ResponseEntity<Author> updateAuthor(@Valid @PathVariable("id") Long id, @RequestBody Author author) throws AuthorNotFoundException {
		Author authorSaved = authorService.updateAuthor(id, author);
		return ResponseEntity.status(HttpStatus.OK).body(authorSaved);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('author:delete')")
	public ResponseEntity<?> deleteAuthor(@PathVariable("id") Long id) throws AuthorNotFoundException {
		authorService.deleteAuthor(id);
		return response(HttpStatus.OK, AUTHOR_DELETED_SUCCESSFULLY);
	}

	@PutMapping("/{id}/active")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAnyAuthority('author:update')")
	public void upatePropertyActive(@PathVariable("id") Long id, @RequestBody Boolean active) {
		authorService.upatePropertyActive(id, active);
	}

	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
		return new ResponseEntity<>(
				new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message),
				httpStatus);
	}
}
