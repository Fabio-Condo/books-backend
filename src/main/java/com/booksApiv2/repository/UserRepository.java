package com.booksApiv2.repository;

import com.booksApiv2.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByUsername(String username);

    User findUserByEmail(String email);
    
    User findUserByRole(String role);
}
