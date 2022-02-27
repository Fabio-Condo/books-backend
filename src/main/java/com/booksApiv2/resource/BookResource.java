package com.booksApiv2.resource;

import static com.booksApiv2.constant.FileConstant.FORWARD_SLASH;
import static com.booksApiv2.constant.FileConstant.BOOK_FOLDER;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE; 
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.booksApiv2.domain.Book;
import com.booksApiv2.domain.Comment;
import com.booksApiv2.domain.HttpResponse;
import com.booksApiv2.domain.Like;
import com.booksApiv2.exception.domain.AuthorNotFoundException;
import com.booksApiv2.exception.domain.BookNotFoundException;
import com.booksApiv2.exception.domain.CommentNotFoundException;
import com.booksApiv2.exception.domain.LikeInvalidOptionException;
import com.booksApiv2.exception.domain.NotAPdfFileException;
import com.booksApiv2.exception.domain.NotAnImageFileException;
import com.booksApiv2.exception.domain.OwnerCommentNotFoundException;

import com.booksApiv2.exception.ExceptionHandling;
import com.booksApiv2.repository.BookRepository;
import com.booksApiv2.repository.filter.BookFilter;
import com.booksApiv2.repository.projection.BookSummary;
import com.booksApiv2.service.BookService;

import net.sf.jasperreports.engine.JRException;

@RestController
@RequestMapping(path = { "/", "/books" })
public class BookResource extends ExceptionHandling {

	private static final String BOOK_DELETED_SUCCESSFULLY = "Book deleted successfully";
	private static final String COMMENT_DELETED_SUCCESSFULLY = "Comment deleted successfully";
	private static final String FILE_IMPORTED_SUCCESSFULLY = "File imported successfully";

	@Autowired
	public BookService bookService;

	@Autowired
	public BookRepository bookRepository;

	@GetMapping
	@PreAuthorize("hasAnyAuthority('book:read')")
	public Page<Book> searchBook(BookFilter bookFilter, Pageable pageable) {
		return bookRepository.bookFilter(bookFilter, pageable);
	}

	@GetMapping(params = "summary")
	@PreAuthorize("hasAnyAuthority('book:read')")
	public Page<BookSummary> summary(BookFilter bookFilter, Pageable pageable) {
		return bookRepository.summary(bookFilter, pageable);
	}

	@PostMapping
	@PreAuthorize("hasAnyAuthority('book:create')")
	public ResponseEntity<Book> addNewBook(@Valid 
										   @RequestParam("name") String name,
										   @RequestParam("publishingCompany") String publishingCompany, 
										   @RequestParam("summary") String summary,
										   @RequestParam("authorId") String authorId, 
										   @RequestParam("publicationDate") Date publicationDate,
										   @RequestParam(value = "bookImage", required = false) MultipartFile bookImage,
										   @RequestParam(value = "bookPdf", required = false) MultipartFile bookPdf) throws IOException, NotAnImageFileException, AuthorNotFoundException, NumberFormatException, NotAPdfFileException {
		Book newBook = bookService.addNewBook(name, publishingCompany, summary, Long.parseLong(authorId),publicationDate, bookImage, bookPdf);
		return new ResponseEntity<>(newBook, OK);
	}

	@PutMapping("/update/{id}")
	@PreAuthorize("hasAnyAuthority('book:update')")
	public ResponseEntity<Book> updateBook(@Valid 
										   @PathVariable("id") Long id, 
										   @RequestParam("name") String name,
									       @RequestParam("publishingCompany") String publishingCompany, 
										   @RequestParam("summary") String summary,
									       @RequestParam("authorId") String authorId, 
										   @RequestParam("publicationDate") Date publicationDate,
										   @RequestParam(value = "bookImage", required = false) MultipartFile bookImage,
										   @RequestParam(value = "bookPdf", required = false) MultipartFile bookPdf) throws IOException,NotAnImageFileException, AuthorNotFoundException, NumberFormatException, BookNotFoundException, NotAPdfFileException {
		Book newBook = bookService.updateBook(id, name, publishingCompany, summary, Long.parseLong(authorId),publicationDate, bookImage, bookPdf);
		return new ResponseEntity<>(newBook, OK);
	}

	@GetMapping(path = "/image/{name}/{fileName}", produces = IMAGE_JPEG_VALUE)
	public byte[] getBookImage(@PathVariable("name") String name, @PathVariable("fileName") String fileName) throws IOException {
		return Files.readAllBytes(Paths.get(BOOK_FOLDER + name + FORWARD_SLASH + fileName));
	}

	@GetMapping(path = "/pdf/{name}/{fileName}", produces = APPLICATION_PDF_VALUE)
	public byte[] getBookPdf(@PathVariable("name") String name, @PathVariable("fileName") String fileName) throws IOException {
		return Files.readAllBytes(Paths.get(BOOK_FOLDER + name + FORWARD_SLASH + fileName));
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('book:read')")
	public ResponseEntity<?> getSingleBook(@PathVariable("id") Long id) throws BookNotFoundException {
		Book bookSaved = bookService.getExistBook(id);
		return ResponseEntity.status(HttpStatus.OK).body(bookSaved);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('book:delete')")
	public ResponseEntity<HttpResponse> deleteBook(@PathVariable("id") Long id) throws BookNotFoundException, IOException {
		bookService.deleteBook(id);
		return response(HttpStatus.OK, BOOK_DELETED_SUCCESSFULLY);
	}

	@PostMapping("/{id}/comments")
	@PreAuthorize("hasAnyAuthority('comment:create')")
	public ResponseEntity<Comment> addComment(@PathVariable("id") Long id, @RequestBody Comment comment) throws BookNotFoundException {
		Comment commentSaved = bookService.saveComment(id, comment);
		return ResponseEntity.status(HttpStatus.OK).body(commentSaved);
	}

	@PutMapping("/{id}/comments/{comentId}")
	@PreAuthorize("hasAnyAuthority('comment:update')")
	public ResponseEntity<Comment> updateComment(@PathVariable("id") Long id, @RequestBody Comment comment,@PathVariable("comentId") Long comentId) throws Exception {
		Comment commentSaved = bookService.updateComment(id, comment, comentId);
		return ResponseEntity.status(HttpStatus.OK).body(commentSaved);
	}

	@DeleteMapping("/{id}/comments/{comentId}")
	@PreAuthorize("hasAnyAuthority('comment:delete')")
	public ResponseEntity<HttpResponse> deleteComment(@PathVariable("id") Long id, @PathVariable("comentId") Long comentId) throws BookNotFoundException, CommentNotFoundException, OwnerCommentNotFoundException {
		bookService.deleteComment(id, comentId);
		return response(HttpStatus.OK, COMMENT_DELETED_SUCCESSFULLY);
	}

	@GetMapping("/{id}/comments")
	@PreAuthorize("hasAnyAuthority('comment:read')")
	public ResponseEntity<List<Comment>> getBookComments(@PathVariable("id") Long id) throws BookNotFoundException {
		List<Comment> comments = bookService.getBookComments(id);
		return ResponseEntity.status(HttpStatus.OK).body(comments);
	}
	
	@PostMapping("/{id}/likes")
	@PreAuthorize("hasAnyAuthority('comment:create')")
	public ResponseEntity<Like> addLike(@PathVariable("id") Long id, @RequestBody Like like) throws BookNotFoundException, LikeInvalidOptionException{
		Like likeSaved = bookService.saveLike(id, like);
		return ResponseEntity.status(HttpStatus.OK).body(likeSaved);
	}
	
	@GetMapping("/{id}/comments/total-number")
	@PreAuthorize("hasAnyAuthority('comment:read')")
	public int getTotalCommentsNumber(@PathVariable("id") Long id) throws BookNotFoundException {	
		return bookService.getTotalCommentsNumber(id);	
	}
	
	@GetMapping("/{id}/likes")
	@PreAuthorize("hasAnyAuthority('comment:read')")
	public ResponseEntity<List<Like>> getBookLikes(@PathVariable("id") Long id) throws BookNotFoundException {
		List<Like> likes = bookService.getBookLikes(id);
		return ResponseEntity.status(HttpStatus.OK).body(likes);
	}

	@GetMapping("/{id}/likes/total-number")
	@PreAuthorize("hasAnyAuthority('comment:read')")
	public int getTotalLikesNumber(@PathVariable("id") Long id) throws BookNotFoundException {	
		return bookService.getTotalLikesNumber(id);	
	}
	
	@PostMapping("/upload")
	@PreAuthorize("hasAnyAuthority('book:create')")
	public ResponseEntity<HttpResponse> uploadData(@RequestParam("file") MultipartFile file) throws Exception{	
		bookService.uploadData(file);
		return response(HttpStatus.OK, FILE_IMPORTED_SUCCESSFULLY);
	}
	
	@GetMapping("/report")
	@PreAuthorize("hasAnyAuthority('book:read')")
	public ResponseEntity<byte[]> generatePdf() throws FileNotFoundException, JRException { 	
		byte[] data = bookService.generatePdf2();
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE).body(data);
	}
	
	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
		return new ResponseEntity<>( new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message), httpStatus);
	}
}
