package com.expensetracker.model;

import com.expensetracker.exception.ValidationException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Subclass: Expense
 * 
 * OOP Concepts Demonstrated:
 * 1. Inheritance: Extends Transaction base class, reusing core properties
 *    (id, amount, transactionDate, description, createdAt).
 * 2. Specialized State:
 *    - 'category': expense classification (Food, Transport, Shopping, etc.)
 *    - 'paymentMethod': payment mode (Cash, UPI, Card, Bank Transfer)
 * 3. Method Overriding & Polymorphism:
 *    - Overrides getSignedAmount() to return NEGATIVE amount (-amount).
 *    - Overrides getTransactionType() to return "EXPENSE".
 *    - Overrides getCategoryOrSource() to return the expense category.
 */
public class Expense extends Transaction {

    public static final List<String> VALID_CATEGORIES = Arrays.asList(
        "Food", "Transport", "Shopping", "Bills", "Education", "Entertainment", "Healthcare", "Other"
    );

    public static final List<String> VALID_PAYMENT_METHODS = Arrays.asList(
        "Cash", "UPI", "Card", "Bank Transfer"
    );

    // Specialized attributes for Expense
    private String category;
    private String paymentMethod = "Cash";

    public Expense() {
        super();
    }

    // Constructor without userId
    public Expense(double amount, LocalDate transactionDate, String description, String category, String paymentMethod) 
            throws ValidationException {
        super(1, amount, transactionDate, description);
        setCategory(category);
        setPaymentMethod(paymentMethod);
    }

    public Expense(int userId, double amount, LocalDate transactionDate, String description, String category) 
            throws ValidationException {
        this(userId, amount, transactionDate, description, category, "Cash");
    }

    public Expense(int userId, double amount, LocalDate transactionDate, String description, 
                   String category, String paymentMethod) throws ValidationException {
        super(userId, amount, transactionDate, description);
        setCategory(category);
        setPaymentMethod(paymentMethod);
    }

    public Expense(int id, int userId, double amount, LocalDate transactionDate, 
                   String description, String category, Timestamp createdAt) throws ValidationException {
        this(id, userId, amount, transactionDate, description, category, "Cash", createdAt);
    }

    public Expense(int id, int userId, double amount, LocalDate transactionDate, 
                   String description, String category, String paymentMethod, Timestamp createdAt) 
                   throws ValidationException {
        super(id, userId, amount, transactionDate, description, createdAt);
        setCategory(category);
        setPaymentMethod(paymentMethod);
    }

    // =========================================================================
    // POLYMORPHIC METHOD OVERRIDES
    // =========================================================================

    /**
     * Polymorphic implementation: Expenses reduce account balance (negative value).
     */
    @Override
    public double getSignedAmount() {
        return -getAmount();
    }

    @Override
    public String getTransactionType() {
        return "EXPENSE";
    }

    @Override
    public String getCategoryOrSource() {
        return this.category;
    }

    @Override
    public void setCategoryOrSource(String value) throws ValidationException {
        setCategory(value);
    }

    // =========================================================================
    // SPECIALIZED GETTERS & SETTERS (ENCAPSULATION)
    // =========================================================================

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) throws ValidationException {
        if (category == null || category.trim().isEmpty()) {
            throw new ValidationException("Expense category cannot be empty.");
        }
        String cleanCategory = category.trim();
        boolean valid = VALID_CATEGORIES.stream()
            .anyMatch(c -> c.equalsIgnoreCase(cleanCategory));
        if (!valid) {
            throw new ValidationException(
                "Invalid expense category '" + category + "'. Allowed: " + String.join(", ", VALID_CATEGORIES)
            );
        }
        this.category = cleanCategory;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) throws ValidationException {
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            this.paymentMethod = "Cash";
            return;
        }
        String cleanMethod = paymentMethod.trim();
        boolean valid = VALID_PAYMENT_METHODS.stream()
            .anyMatch(m -> m.equalsIgnoreCase(cleanMethod));
        if (!valid) {
            this.paymentMethod = "Cash";
        } else {
            // Find matched casing
            for (String m : VALID_PAYMENT_METHODS) {
                if (m.equalsIgnoreCase(cleanMethod)) {
                    this.paymentMethod = m;
                    break;
                }
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Expense[id=%d, category='%s', amount=-₹%.2f, date=%s, payment='%s']",
            getId(), category, getAmount(), getTransactionDate(), paymentMethod);
    }
}
