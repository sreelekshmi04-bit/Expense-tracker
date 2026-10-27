package com.expensetracker.exception;

/**
 * Thrown when login credentials or user authentication fails.
 */
public class AuthenticationException extends ExpenseTrackerException {
    public AuthenticationException(String message) {
        super(message);
    }
}
