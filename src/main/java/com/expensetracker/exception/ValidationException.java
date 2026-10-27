package com.expensetracker.exception;

/**
 * Thrown when data validation fails (e.g. negative amounts, empty categories, invalid email).
 * Demonstrates OOP Encapsulation and Data Integrity.
 */
public class ValidationException extends ExpenseTrackerException {
    public ValidationException(String message) {
        super(message);
    }
}
