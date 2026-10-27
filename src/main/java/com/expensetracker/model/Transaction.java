package com.expensetracker.model;

import com.expensetracker.exception.ValidationException;
import java.time.LocalDate;
import java.sql.Timestamp;

/**
 * Abstract Base Class: Transaction
 * 
 * OOP Concepts Demonstrated:
 * 1. Abstraction: Defines an abstract contract for financial movements without revealing
 *    the exact mathematical sign or categorization rules until subclassed.
 * 2. Encapsulation: All fields are private, with validated getters and setters.
 * 3. Inheritance Base: Extended by Income and Expense subclasses.
 * 4. Polymorphic Foundation: Declares abstract methods (getSignedAmount, getTransactionType,
 *    getCategoryOrSource) that allow callers to process collections of Transactions polymorphically.
 */
public abstract class Transaction {
    // Encapsulation: private attributes
    private int id;
    private int userId;
    private double amount;
    private LocalDate transactionDate;
    private String description;
    private Timestamp createdAt;

    // Default constructor
    public Transaction() {
    }

    // Parameterized constructor
    public Transaction(int userId, double amount, LocalDate transactionDate, String description) 
            throws ValidationException {
        this.userId = userId;
        setAmount(amount);
        setTransactionDate(transactionDate);
        setDescription(description);
    }

    // Full constructor
    public Transaction(int id, int userId, double amount, LocalDate transactionDate, 
                       String description, Timestamp createdAt) throws ValidationException {
        this(userId, amount, transactionDate, description);
        this.id = id;
        this.createdAt = createdAt;
    }

    // =========================================================================
    // ABSTRACT METHODS (Polymorphism & Abstraction)
    // Subclasses MUST implement these methods according to their financial nature.
    // =========================================================================

    /**
     * Calculates the signed impact of this transaction on the account balance.
     * Income returns positive (+amount), Expense returns negative (-amount).
     */
    public abstract double getSignedAmount();

    /**
     * Returns the uppercase transaction discriminator type ("INCOME" or "EXPENSE").
     */
    public abstract String getTransactionType();

    /**
     * Polymorphic accessor: Returns 'source' for Income and 'category' for Expense.
     */
    public abstract String getCategoryOrSource();

    /**
     * Polymorphic mutator: Sets 'source' for Income and 'category' for Expense.
     */
    public abstract void setCategoryOrSource(String value) throws ValidationException;

    // =========================================================================
    // GETTERS AND SETTERS WITH ENCAPSULATION & VALIDATION
    // =========================================================================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    /**
     * Encapsulation validation: amount must be strictly greater than 0.
     */
    public void setAmount(double amount) throws ValidationException {
        if (Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0.0) {
            throw new ValidationException("Transaction amount must be a positive number greater than 0.");
        }
        // Round to 2 decimal places to avoid floating point inaccuracies
        this.amount = Math.round(amount * 100.0) / 100.0;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    /**
     * Encapsulation validation: date cannot be null and cannot be in the distant future.
     */
    public void setTransactionDate(LocalDate transactionDate) throws ValidationException {
        if (transactionDate == null) {
            throw new ValidationException("Transaction date is required.");
        }
        if (transactionDate.isAfter(LocalDate.now().plusDays(1))) {
            throw new ValidationException("Transaction date cannot be in the future.");
        }
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) throws ValidationException {
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Transaction description cannot be empty.");
        }
        if (description.trim().length() > 255) {
            throw new ValidationException("Transaction description cannot exceed 255 characters.");
        }
        this.description = description.trim();
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("%s[id=%d, date=%s, amount=₹%.2f, desc='%s']",
            getTransactionType(), id, transactionDate, amount, description);
    }
}
