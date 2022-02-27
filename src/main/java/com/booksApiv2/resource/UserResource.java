package com.booksApiv2.resource;

import com.booksApiv2.domain.HttpResponse;
import com.booksApiv2.domain.User;
import com.booksApiv2.domain.UserPrincipal;
import com.booksApiv2.exception.ExceptionHandling;
import com.booksApiv2.exception.domain.*;
import com.booksApiv2.service.UserService;
import com.booksApiv2.utility.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.booksApiv2.constant.FileConstant.*;
import static com.booksApiv2.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(path = { "/", "/user" })
public class UserResource extends ExceptionHandling {
	public static final String EMAIL_SENT = "An email with a new password was sent to: ";
	public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";
	private AuthenticationManager authenticationManager;
	private UserService userService;
	private JWTTokenProvider jwtTokenProvider;

	@Autowired
	public UserResource(AuthenticationManager authenticationManager, UserService userService,JWTTokenProvider jwtTokenProvider) {
		this.authenticationManager = authenticationManager;
		this.userService = userService;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@PostMapping("/login")
	public ResponseEntity<User> login(@RequestBody User user) {
		authenticate(user.getUsername(), user.getPassword());
		User loginUser = userService.findUserByUsername(user.getUsername());
		UserPrincipal userPrincipal = new UserPrincipal(loginUser);
		HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
		return new ResponseEntity<>(loginUser, jwtHeader, OK);
	}

	@PostMapping("/register")
	public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
		User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
		return new ResponseEntity<>(newUser, OK);
	}

	@PostMapping("/register/super-user")
	public ResponseEntity<User> registerSuperUserAfterDeploy(@RequestBody User user) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException, SuperUserExistException {
		User newUser = userService.registerSuperUserAfterDeploy(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
		return new ResponseEntity<>(newUser, OK);
	}

	@PostMapping("/add")
	@PreAuthorize("hasAnyAuthority('user:create')")
	public ResponseEntity<User> addNewUser(@RequestParam("firstName") String firstName,
										   @RequestParam("lastName") String lastName, 
										   @RequestParam("username") String username,
										   @RequestParam("email") String email, @RequestParam("role") String role,
										   @RequestParam("isActive") String isActive, 
										   @RequestParam("isNonLocked") String isNonLocked,
										   @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException,NotAnImageFileException {
		User newUser = userService.addNewUser(firstName, lastName, username, email, role,Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);
		return new ResponseEntity<>(newUser, OK);
	}

	@PostMapping("/update")
	@PreAuthorize("hasAnyAuthority('user:update')")
	public ResponseEntity<User> update(@RequestParam("currentUsername") String currentUsername,
									   @RequestParam("firstName") String firstName, 
									   @RequestParam("lastName") String lastName,
									   @RequestParam("username") String username, 
									   @RequestParam("email") String email,
									   @RequestParam("role") String role, 
									   @RequestParam("isActive") String isActive,
									   @RequestParam("isNonLocked") String isNonLocked,
									   @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException,NotAnImageFileException {
		User updatedUser = userService.updateUser(currentUsername, firstName, lastName, username, email, role,Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);
		return new ResponseEntity<>(updatedUser, OK);
	}

	@GetMapping("/find/{username}")
	@PreAuthorize("hasAnyAuthority('user:read')")
	public ResponseEntity<User> getUser(@PathVariable("username") String username) {
		User user = userService.findUserByUsername(username);
		return new ResponseEntity<>(user, OK);
	}

	@GetMapping("/list")
	@PreAuthorize("hasAnyAuthority('user:read')")
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> users = userService.getUsers();
		return new ResponseEntity<>(users, OK);
	}

	@GetMapping("/resetpassword/{email}")
	@PreAuthorize("hasAnyAuthority('user:update')")
	public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws MessagingException, EmailNotFoundException {
		userService.resetPassword(email);
		return response(OK, EMAIL_SENT + email);
	}

	@DeleteMapping("/delete/{username}")
	@PreAuthorize("hasAnyAuthority('user:delete')")
	public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException, UserNotFoundException, UsernameExistException, EmailExistException {
		userService.deleteUser(username);
		return response(OK, USER_DELETED_SUCCESSFULLY);
	}

	@PostMapping("/updateProfileImage")
	@PreAuthorize("hasAnyAuthority('user:update')")
	public ResponseEntity<User> updateProfileImage(@RequestParam("username") String username, @RequestParam(value = "profileImage") MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
		User user = userService.updateProfileImage(username, profileImage);
		return new ResponseEntity<>(user, OK);
	}
	
	@PutMapping("/{username}/active")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAnyAuthority('user:update')")
	public void upatePropertyActive(@PathVariable("username") String username, @RequestBody Boolean active) throws UserNotFoundException, UsernameExistException, EmailExistException {
		userService.upatePropertyActive(username, active);
	}
	
	@PutMapping("/{username}/not-locked")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAnyAuthority('user:update')")
	public void upatePropertyNotLocked(@PathVariable("username") String username, @RequestBody Boolean notLocked) throws UserNotFoundException, UsernameExistException, EmailExistException {
		userService.upatePropertyNotLocked(username, notLocked);
	}
	
	@GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
	public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName) throws IOException {
		return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
	}

	@GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
	public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
		URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try (InputStream inputStream = url.openStream()) {
			int bytesRead;
			byte[] chunk = new byte[1024];
			while ((bytesRead = inputStream.read(chunk)) > 0) {
				byteArrayOutputStream.write(chunk, 0, bytesRead);
			}
		}
		return byteArrayOutputStream.toByteArray();
	}

	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
		return new ResponseEntity<>(
				new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message),
				httpStatus);
	}

	private HttpHeaders getJwtHeader(UserPrincipal user) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
		return headers;
	}

	private void authenticate(String username, String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	}
}
