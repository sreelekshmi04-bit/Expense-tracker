package com.expensetracker.model;

import com.expensetracker.exception.ValidationException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Subclass: Income
 * 
 * OOP Concepts Demonstrated:
 * 1. Inheritance: Extends Transaction base class, inheriting common attributes
 *    (id, amount, transactionDate, description, createdAt).
 * 2. Specialized State: Introduces 'source' attribute specific to income inflows.
 * 3. Method Overriding & Polymorphism:
 *    - Overrides getSignedAmount() to return positive amount (+amount).
 *    - Overrides getTransactionType() to return "INCOME".
 *    - Overrides getCategoryOrSource() to return the income source.
 */
public class Income extends Transaction {

    public static final List<String> VALID_SOURCES = Arrays.asList(
        "Salary", "Scholarship", "Freelance", "Gift", "Investment", "Other"
    );

    // Specialized attribute for Income
    private String source;

    public Income() {
        super();
    }

    // Constructor without userId
    public Income(double amount, LocalDate transactionDate, String description, String source) 
            throws ValidationException {
        super(1, amount, transactionDate, description);
        setSource(source);
    }

    public Income(int userId, double amount, LocalDate transactionDate, String description, String source) 
            throws ValidationException {
        super(userId, amount, transactionDate, description);
        setSource(source);
    }

    public Income(int id, int userId, double amount, LocalDate transactionDate, 
                  String description, String source, Timestamp createdAt) throws ValidationException {
        super(id, userId, amount, transactionDate, description, createdAt);
        setSource(source);
    }

    // =========================================================================
    // POLYMORPHIC METHOD OVERRIDES
    // =========================================================================

    /**
     * Polymorphic implementation: Incomes increase account balance (positive value).
     */
    @Override
    public double getSignedAmount() {
        return getAmount();
    }

    @Override
    public String getTransactionType() {
        return "INCOME";
    }

    @Override
    public String getCategoryOrSource() {
        return this.source;
    }

    @Override
    public void setCategoryOrSource(String value) throws ValidationException {
        setSource(value);
    }

    // =========================================================================
    // SPECIALIZED GETTERS & SETTERS (ENCAPSULATION)
    // =========================================================================

    public String getSource() {
        return source;
    }

    public void setSource(String source) throws ValidationException {
        if (source == null || source.trim().isEmpty()) {
            throw new ValidationException("Income source cannot be empty.");
        }
        String cleanSource = source.trim();
        boolean valid = VALID_SOURCES.stream()
            .anyMatch(s -> s.equalsIgnoreCase(cleanSource));
        if (!valid) {
            throw new ValidationException(
                "Invalid income source '" + source + "'. Allowed: " + String.join(", ", VALID_SOURCES)
            );
        }
        this.source = cleanSource;
    }

    @Override
    public String toString() {
        return String.format("Income[id=%d, source='%s', amount=+₹%.2f, date=%s]",
            getId(), source, getAmount(), getTransactionDate());
    }
}
