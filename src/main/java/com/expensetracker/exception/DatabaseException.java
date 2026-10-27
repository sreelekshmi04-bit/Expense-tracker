package com.expensetracker.exception;

/**
 * Thrown when database or SQL operations encounter errors.
 */
public class DatabaseException extends ExpenseTrackerException {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
