package com.expensetracker.service;

import com.expensetracker.dao.UserDao;
import com.expensetracker.dao.UserDaoImpl;
import com.expensetracker.exception.AuthenticationException;
import com.expensetracker.exception.DatabaseException;
import com.expensetracker.exception.ValidationException;
import com.expensetracker.model.User;
import com.expensetracker.util.PasswordUtil;
import com.expensetracker.util.ValidationUtil;
import java.util.Optional;

/**
 * Service Implementation for User operations.
 * Demonstrates:
 * 1. Composition: Holds a reference to UserDao.
 * 2. Encapsulation: Validates inputs and handles salt generation internally.
 * 3. Security: Plain passwords are never stored in the database.
 */
public class UserServiceImpl implements UserService {

    // Composition: Service has-a DAO
    private final UserDao userDao;

    public UserServiceImpl() {
        this(new UserDaoImpl());
    }

    // Dependency injection constructor
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User register(String name, String email, String plainPassword) 
            throws ValidationException, DatabaseException {
        // 1. Validate inputs
        ValidationUtil.validateNotEmpty(name, "Full Name");
        ValidationUtil.validateEmail(email);
        ValidationUtil.validatePasswordStrength(plainPassword);

        String cleanEmail = email.trim().toLowerCase();

        // 2. Check for existing user
        if (userDao.existsByEmail(cleanEmail)) {
            throw new ValidationException("An account with email '" + cleanEmail + "' already exists.");
        }

        // 3. Generate salt and hash password
        String salt = PasswordUtil.generateSalt();
        String passwordHash = PasswordUtil.hashPassword(plainPassword, salt);

        // 4. Create encapsulated User object
        User user = new User(name.trim(), cleanEmail);
        user.setPasswordHash(passwordHash);
        user.setSalt(salt);
        user.setCurrency("INR");

        // 5. Persist to DB
        int id = userDao.create(user);
        user.setId(id);
        return user;
    }

    @Override
    public User login(String email, String plainPassword) 
            throws AuthenticationException, ValidationException, DatabaseException {
        ValidationUtil.validateEmail(email);
        ValidationUtil.validateNotEmpty(plainPassword, "Password");

        String cleanEmail = email.trim().toLowerCase();

        Optional<User> userOpt = userDao.findByEmail(cleanEmail);
        if (userOpt.isEmpty()) {
            throw new AuthenticationException("Invalid email or password.");
        }

        User user = userOpt.get();
        boolean matches = PasswordUtil.verifyPassword(plainPassword, user.getSalt(), user.getPasswordHash());
        if (!matches) {
            throw new AuthenticationException("Invalid email or password.");
        }

        return user;
    }

    @Override
    public Optional<User> getUserById(int userId) throws DatabaseException {
        return userDao.findById(userId);
    }
}
