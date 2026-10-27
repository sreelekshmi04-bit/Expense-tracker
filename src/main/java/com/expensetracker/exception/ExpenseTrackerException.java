package com.expensetracker.exception;

/**
 * Base custom exception for the Expense Tracker application.
 * Demonstrates exception hierarchy and OOP exception handling.
 */
public class ExpenseTrackerException extends Exception {
    public ExpenseTrackerException(String message) {
        super(message);
    }

    public ExpenseTrackerException(String message, Throwable cause) {
        super(message, cause);
    }
}
