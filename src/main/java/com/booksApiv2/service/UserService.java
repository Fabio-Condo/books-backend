package com.booksApiv2.service;

import com.booksApiv2.domain.User;
import com.booksApiv2.exception.domain.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface UserService {

	User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException;

	List<User> getUsers();

	User findUserByUsername(String username);

	User findUserByEmail(String email);

	User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException;

	User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException;

	void deleteUser(String username) throws IOException, UserNotFoundException, UsernameExistException, EmailExistException;

	void resetPassword(String email) throws MessagingException, EmailNotFoundException;

	User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException;

	User registerSuperUserAfterDeploy(String firstName, String lastName, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException, SuperUserExistException;

	User findUserByRole(String role);
	
	void upatePropertyActive(String username, Boolean active) throws UserNotFoundException, UsernameExistException, EmailExistException;
	
	void upatePropertyNotLocked(String username, Boolean active) throws UserNotFoundException, UsernameExistException, EmailExistException;
}
