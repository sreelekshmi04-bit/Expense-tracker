package com.expensetracker.dao;

import com.expensetracker.exception.DatabaseException;
import com.expensetracker.model.User;
import java.util.Optional;

/**
 * Data Access Object interface for User entities.
 * Demonstrates Abstraction: separates business logic from SQL data persistence.
 */
public interface UserDao {
    /**
     * Inserts a new user record.
     * @return the auto-generated user id
     */
    int create(User user) throws DatabaseException;

    /**
     * Finds a user by their unique email.
     */
    Optional<User> findByEmail(String email) throws DatabaseException;

    /**
     * Finds a user by primary key id.
     */
    Optional<User> findById(int id) throws DatabaseException;

    /**
     * Checks if an email is already registered.
     */
    boolean existsByEmail(String email) throws DatabaseException;

    /**
     * Updates an existing user's profile.
     */
    boolean update(User user) throws DatabaseException;
}
