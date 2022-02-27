package com.booksApiv2.service;

import static com.booksApiv2.constant.FileConstant.DIRECTORY_CREATED;
import static com.booksApiv2.constant.FileConstant.DOT;
import static com.booksApiv2.constant.FileConstant.FILE_SAVED_IN_FILE_SYSTEM;
import static com.booksApiv2.constant.FileConstant.FORWARD_SLASH;
import static com.booksApiv2.constant.FileConstant.JPG_EXTENSION;
import static com.booksApiv2.constant.FileConstant.PDF_EXTENSION;
import static com.booksApiv2.constant.FileConstant.NOT_AN_IMAGE_FILE; 
import static com.booksApiv2.constant.FileConstant.NOT_A_PDF_FILE;
import static com.booksApiv2.constant.FileConstant.BOOK_FOLDER;
import static com.booksApiv2.constant.FileConstant.BOOK_IMAGE_PATH;
import static com.booksApiv2.constant.FileConstant.BOOK_PDF_PATH;
import static org.springframework.http.MediaType.IMAGE_GIF_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.booksApiv2.domain.Author;
import com.booksApiv2.domain.Book;
import com.booksApiv2.domain.Comment;
import com.booksApiv2.domain.Like;
import com.booksApiv2.domain.User;
import com.booksApiv2.exception.domain.AuthorNotFoundException;
import com.booksApiv2.exception.domain.BookNotFoundException;
import com.booksApiv2.exception.domain.CommentNotFoundException;
import com.booksApiv2.exception.domain.LikeInvalidOptionException;
import com.booksApiv2.exception.domain.NotAPdfFileException;
import com.booksApiv2.exception.domain.NotAnImageFileException;
import com.booksApiv2.exception.domain.OwnerCommentNotFoundException;
import com.booksApiv2.repository.AuthorRepository;
import com.booksApiv2.repository.BookRepository;
import com.booksApiv2.repository.CommentRepository;
import com.booksApiv2.repository.LikeRepository;
import com.booksApiv2.repository.UserRepository;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class BookService {

	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	private static final String NO_BOOK_FOUND_BY_ID = "No book found by id: ";
	private static final String NO_AUTHOR_FOUND_OR_IS_INACTIVE = "No author found, or is inactive";
	private static final String COMMENT_TO_BE_SAVED = "Comment to be saved: ";
	private static final String LIKE_TO_BE_SAVED = "like to be saved: ";
	private static final String INVALID_OPTION = "Invalid option. The valid options for add a like are: CONGRATULATIONS, SUPPORT, LOVED, GENIUS, INTERESTING";
	private static final String IS_NOT_YOUR_COMMENT = "This is not your comment. You can't update it. Id number: ";
	private static final String NO_COMMENT_FOUND_BY_ID = "No comment found by id: ";

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private AuthorRepository authorRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private LikeRepository likeRepository;
	
	@Autowired
	private UserRepository userRepository;

	public Book getExistBook(Long id) throws BookNotFoundException {
		Book bookSaved = bookRepository.findById(id)
				.orElseThrow(() -> new BookNotFoundException(NO_BOOK_FOUND_BY_ID + id));
		return bookSaved;
	}

	public Book addNewBook(String name, String publishingCompany, String summary, Long authorId, Date publicationDate, MultipartFile bookImage, MultipartFile bookPdf) throws IOException, NotAnImageFileException, AuthorNotFoundException, NotAPdfFileException {
		Optional<Author> authorBook = authorRepository.findById(authorId);
		if (!(authorBook.isPresent()) || (authorBook.get().isInactive())) {
			LOGGER.error(NO_AUTHOR_FOUND_OR_IS_INACTIVE);
			throw new AuthorNotFoundException(NO_AUTHOR_FOUND_OR_IS_INACTIVE);
		}
		Book book = new Book();
		Author author = new Author();
		author.setId(authorId);
		book.setName(name);
		book.setPublishingCompany(publishingCompany);
		book.setSummary(summary);
		book.setAuthor(author);
		book.setPublicationDate(publicationDate);
		bookRepository.save(book);
		saveBookImage(book, bookImage);
		saveBookPdf(book, bookPdf);
		LOGGER.info("New book: " + book.getName());
		return book;
	}

	public Book updateBook(Long id, String name, String publishingCompany, String summary, Long authorId, Date publicationDate, MultipartFile bookImage, MultipartFile bookPdf) throws IOException, NotAnImageFileException, AuthorNotFoundException, BookNotFoundException, NotAPdfFileException {
		Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(NO_BOOK_FOUND_BY_ID + id));
		Optional<Author> authorBook = authorRepository.findById(authorId);
		if (!(authorBook.isPresent()) || (authorBook.get().isInactive())) {
			LOGGER.error(NO_AUTHOR_FOUND_OR_IS_INACTIVE);
			throw new AuthorNotFoundException(NO_AUTHOR_FOUND_OR_IS_INACTIVE);
		}
		Author author = new Author();
		author.setId(authorId);
		book.setName(name);
		book.setPublishingCompany(publishingCompany);
		book.setSummary(summary);
		book.setAuthor(author);
		book.setPublicationDate(publicationDate);
		bookRepository.save(book);
		saveBookImage(book, bookImage);
		saveBookPdf(book, bookPdf);
		LOGGER.info("New book: " + book.getName());
		return book;
	}

	public void deleteBook(Long id) throws BookNotFoundException, IOException {
		Book book = getExistBook(id);
		Path bookFolder = Paths.get(BOOK_FOLDER + book.getName()).toAbsolutePath().normalize();
		FileUtils.deleteDirectory(new File(bookFolder.toString()));
		LOGGER.info("Path folder deleted: " + bookFolder + ". Book name: " + book.getName()); 
		bookRepository.deleteById(id);
	}
	
	public Comment getExistComment(Long id) throws CommentNotFoundException {
		Comment commentSaved = commentRepository.findById(id)
				.orElseThrow(() -> new CommentNotFoundException(NO_COMMENT_FOUND_BY_ID + id));
		return commentSaved;
	}

	public Comment saveComment(Long bookId, Comment comment) throws BookNotFoundException {
		Book book = getExistBook(bookId);
		comment.setBook(book);
		comment.setUser(getAuthenticatedUser());
		comment.setCommentDate(new Date());
		LOGGER.info(COMMENT_TO_BE_SAVED + comment.getText());
		return commentRepository.save(comment);
	}

	public Comment updateComment(Long bookId, Comment comment, Long idComment) throws OwnerCommentNotFoundException, BookNotFoundException, CommentNotFoundException {
		getExistBook(bookId);
		Comment commentSaved = getExistComment(idComment);
		if (getAuthenticatedUser().getId() != commentSaved.getUser().getId()) {
			LOGGER.error(IS_NOT_YOUR_COMMENT);
			throw new OwnerCommentNotFoundException(IS_NOT_YOUR_COMMENT);
		}
		commentSaved.setText(comment.getText());
		LOGGER.info(COMMENT_TO_BE_SAVED + comment.getText());
		return commentRepository.save(commentSaved);
	}

	public void deleteComment(Long bookId, Long idComment) throws BookNotFoundException, CommentNotFoundException, OwnerCommentNotFoundException {
		getExistBook(bookId);
		Comment commentSaved = getExistComment(idComment);
		if (getAuthenticatedUser().getId() != commentSaved.getUser().getId()) {
			LOGGER.error(IS_NOT_YOUR_COMMENT);
			throw new OwnerCommentNotFoundException(IS_NOT_YOUR_COMMENT);
		}
		commentRepository.deleteById(idComment);
	}

	public List<Comment> getBookComments(Long bookId) throws BookNotFoundException {
		Book bookSaved = getExistBook(bookId);
		return bookSaved.getComments();
	}
	
	public int getTotalCommentsNumber(Long bookId) throws BookNotFoundException {
		Book book = getExistBook(bookId);
		int totalBookCommentsNumber = book.getComments().size();
		LOGGER.info("The book "+ book.getName() + " has " + totalBookCommentsNumber + " comments");
        return totalBookCommentsNumber;
    }
	
	public Like saveLike(Long bookId, Like like) throws BookNotFoundException, LikeInvalidOptionException {
		Book book = getExistBook(bookId);
		Like likeByUserSaved = likeRepository.findLikeByUserAndBook(getAuthenticatedUser(), book);
		String DeslikeType = "DESLIKED";
		if((likeByUserSaved == null) && (like.getLikeType().toString() == DeslikeType)) {
			LOGGER.error(INVALID_OPTION);
			throw new LikeInvalidOptionException(INVALID_OPTION);
		}
		if((likeByUserSaved == null) && (like.getLikeType().toString() != DeslikeType)) {
			like.setBook(book);
			like.setUser(getAuthenticatedUser());
			like.setLikeDate(new Date());
			likeRepository.save(like);
			return like;
		}
		if((likeByUserSaved != null) && (like.getLikeType().toString() != DeslikeType)) {
			likeByUserSaved.setId(likeByUserSaved.getId());
			likeByUserSaved.setLikeType(like.getLikeType());
			likeByUserSaved.setLikeDate(new Date());
			likeRepository.save(likeByUserSaved);
			return likeByUserSaved;
		}		
		if((likeByUserSaved != null) && (like.getLikeType().toString() == DeslikeType)) {
			likeRepository.deleteById(likeByUserSaved.getId());
			return null;
		}		
		LOGGER.info(LIKE_TO_BE_SAVED + like.getLikeType());		
		return like;			
	}	
	
	public List<Like> getBookLikes(Long bookId) throws BookNotFoundException {
		Book bookSaved = getExistBook(bookId);
		return bookSaved.getLikes();
	}
	
	public int getTotalLikesNumber(Long bookId) throws BookNotFoundException {
		Book book = getExistBook(bookId);
		int totalBookLikesNumber = book.getLikes().size();
		LOGGER.info("The book "+ book.getName() + " has " + totalBookLikesNumber + " likes");
        return totalBookLikesNumber;
    }

	private void saveBookImage(Book book, MultipartFile bookImage) throws IOException, NotAnImageFileException {
		if (bookImage != null) {
			if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(bookImage.getContentType())) {
				throw new NotAnImageFileException(bookImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
			}
			Path bookFolder = Paths.get(BOOK_FOLDER + book.getName()).toAbsolutePath().normalize();
			if (!Files.exists(bookFolder)) {
				Files.createDirectories(bookFolder);
				LOGGER.info(DIRECTORY_CREATED + bookFolder);
			}
			Files.deleteIfExists(Paths.get(bookFolder + book.getName() + DOT + JPG_EXTENSION));
			Files.copy(bookImage.getInputStream(), bookFolder.resolve(book.getName() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
			book.setBookImageUrl(setBookImageUrl(book.getName()));
			bookRepository.save(book);
			LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + bookImage.getOriginalFilename());
		}
	}
	
	private void saveBookPdf(Book book, MultipartFile bookPdf) throws IOException, NotAPdfFileException {
		if (bookPdf != null) {
			if (!Arrays.asList(APPLICATION_PDF_VALUE).contains(bookPdf.getContentType())) {
				throw new NotAPdfFileException(bookPdf.getOriginalFilename() + NOT_A_PDF_FILE);
			}
			Path bookFolder = Paths.get(BOOK_FOLDER + book.getName()).toAbsolutePath().normalize();
			if (!Files.exists(bookFolder)) {
				Files.createDirectories(bookFolder);
				LOGGER.info(DIRECTORY_CREATED + bookFolder);
			}
			Files.deleteIfExists(Paths.get(bookFolder + book.getName() + DOT + PDF_EXTENSION));
			Files.copy(bookPdf.getInputStream(), bookFolder.resolve(book.getName() + DOT + PDF_EXTENSION), REPLACE_EXISTING);
			book.setBookPdfUrl(setBookPdfUrl(book.getName()));
			bookRepository.save(book);
			LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + bookPdf.getOriginalFilename());
		}
	}

	private String setBookImageUrl(String name) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(BOOK_IMAGE_PATH + name + FORWARD_SLASH + name + DOT + JPG_EXTENSION).toUriString();
	}
	
	private String setBookPdfUrl(String name) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(BOOK_PDF_PATH + name + FORWARD_SLASH + name + DOT + PDF_EXTENSION).toUriString();
	}
	
	public User findUserByUsername(String username) {
		return userRepository.findUserByUsername(username);
	}

	public User getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getName();
		User user = findUserByUsername(currentPrincipalName);
		return user;
	}
	
	public List<Book> uploadData(MultipartFile file) throws IOException{
		List<Book> bookList = new ArrayList<>();
		InputStream inputStream = file.getInputStream();
		
		CsvParserSettings settings = new CsvParserSettings();
		settings.setHeaderExtractionEnabled(true);
		
		CsvParser parser = new CsvParser(settings);
		List<Record> parserAllRecords = parser.parseAllRecords(inputStream);
		
		parserAllRecords.forEach(record -> {
			Book book = new Book();
			book.setName(record.getString("Name"));
			book.setSummary(record.getString("Summary"));
			book.setPublishingCompany(record.getString("Publishing Company"));
			Long idAuthor = Long.parseLong(record.getString("Author"));
			Author author = new Author();
			author.setId(idAuthor);
			book.setAuthor(author);
			bookList.add(book);		
		});
		return bookRepository.saveAll(bookList);
	}
	
	public byte[] generatePdf2() throws FileNotFoundException, JRException {
		JRBeanCollectionDataSource jRBeanCollectionDataSource = new JRBeanCollectionDataSource( bookRepository.findAll());
		JasperReport jasperReport = JasperCompileManager.compileReport(new FileInputStream("src/main/resources/books-report.jrxml"));		
		HashMap<String, Object> map = new HashMap<>();
		JasperPrint report = JasperFillManager.fillReport(jasperReport, map, jRBeanCollectionDataSource);		
		byte[] data = JasperExportManager.exportReportToPdf(report);
		return data;
	}
}
