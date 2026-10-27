package com.expensetracker.util;

import com.expensetracker.exception.ValidationException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Common validation helpers for inputs across servlets and services.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public static void validateNotEmpty(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required and cannot be blank.");
        }
    }

    public static void validateEmail(String email) throws ValidationException {
        validateNotEmpty(email, "Email");
        if (!EMAIL_PATTERN.matcher(email.trim().toLowerCase()).matches()) {
            throw new ValidationException("Please provide a valid email address (e.g. user@domain.com).");
        }
    }

    public static void validatePasswordStrength(String password) throws ValidationException {
        validateNotEmpty(password, "Password");
        if (password.length() < 6) {
            throw new ValidationException("Password must contain at least 6 characters.");
        }
    }

    public static double parseAndValidateAmount(String amountStr) throws ValidationException {
        validateNotEmpty(amountStr, "Amount");
        try {
            double amount = Double.parseDouble(amountStr.trim());
            if (amount <= 0.0) {
                throw new ValidationException("Amount must be greater than zero.");
            }
            return Math.round(amount * 100.0) / 100.0;
        } catch (NumberFormatException e) {
            throw new ValidationException("Amount must be a valid numeric value.");
        }
    }

    public static LocalDate parseAndValidateDate(String dateStr) throws ValidationException {
        validateNotEmpty(dateStr, "Date");
        try {
            LocalDate date = LocalDate.parse(dateStr.trim());
            if (date.isAfter(LocalDate.now().plusDays(1))) {
                throw new ValidationException("Date cannot be in the future.");
            }
            return date;
        } catch (DateTimeParseException e) {
            throw new ValidationException("Date must be in valid YYYY-MM-DD format.");
        }
    }
}
