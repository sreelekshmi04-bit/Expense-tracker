package com.expensetracker.model;

import com.expensetracker.exception.ValidationException;
import java.sql.Timestamp;
import java.util.regex.Pattern;

/**
 * Represents an authenticated user of the Expense Tracker System.
 * Demonstrates Encapsulation: all fields are private with strict validation in setters.
 */
public class User {
    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private String salt;
    private String currency;
    private Timestamp createdAt;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // Default constructor
    public User() {
        this.currency = "INR";
    }

    // Parameterized constructor
    public User(String name, String email) throws ValidationException {
        this();
        setName(name);
        setEmail(email);
    }

    // Full constructor
    public User(int id, String name, String email, String passwordHash, String salt, String currency, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.currency = (currency != null && !currency.isBlank()) ? currency : "INR";
        this.createdAt = createdAt;
    }

    // Getters and Setters with Encapsulation & Validation

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("User name cannot be empty.");
        }
        if (name.trim().length() < 2) {
            throw new ValidationException("User name must be at least 2 characters long.");
        }
        this.name = name.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws ValidationException {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email address cannot be empty.");
        }
        String cleanEmail = email.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(cleanEmail).matches()) {
            throw new ValidationException("Invalid email format: " + email);
        }
        this.email = cleanEmail;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = (currency != null && !currency.isBlank()) ? currency : "INR";
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User[id=" + id + ", name=" + name + ", email=" + email + "]";
    }
}
