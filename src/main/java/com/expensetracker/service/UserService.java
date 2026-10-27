package com.expensetracker.service;

import com.expensetracker.exception.AuthenticationException;
import com.expensetracker.exception.DatabaseException;
import com.expensetracker.exception.ValidationException;
import com.expensetracker.model.User;
import java.util.Optional;

/**
 * Service interface for User registration and authentication.
 * Demonstrates Abstraction & Interface Segregation.
 */
public interface UserService {

    /**
     * Registers a new user with salt-based SHA-256 password hashing.
     */
    User register(String name, String email, String plainPassword) 
            throws ValidationException, DatabaseException;

    /**
     * Authenticates an existing user with email and password.
     */
    User login(String email, String plainPassword) 
            throws AuthenticationException, ValidationException, DatabaseException;

    /**
     * Retrieves user details by ID.
     */
    Optional<User> getUserById(int userId) throws DatabaseException;
}
