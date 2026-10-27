package com.expensetracker.model;

import com.expensetracker.exception.ValidationException;
import java.sql.Timestamp;
import java.util.regex.Pattern;

/**
 * Represents a monthly spending limit for an expense category.
 * Demonstrates Encapsulation with domain validation.
 */
public class Budget {
    private int id;
    private int userId;
    private String category;
    private double amountLimit;
    private String monthYear; // e.g. "2026-10"
    private Timestamp createdAt;

    private static final Pattern MONTH_PATTERN = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])$");

    public Budget() {
    }

    public Budget(int userId, String category, double amountLimit, String monthYear) 
            throws ValidationException {
        this.userId = userId;
        setCategory(category);
        setAmountLimit(amountLimit);
        setMonthYear(monthYear);
    }

    public Budget(int id, int userId, String category, double amountLimit, String monthYear, Timestamp createdAt) 
            throws ValidationException {
        this(userId, category, amountLimit, monthYear);
        this.id = id;
        this.createdAt = createdAt;
    }

    // Getters and Setters with Encapsulation & Validation

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) throws ValidationException {
        if (category == null || category.trim().isEmpty()) {
            throw new ValidationException("Budget category cannot be empty.");
        }
        String cleanCat = category.trim();
        boolean valid = Expense.VALID_CATEGORIES.stream()
            .anyMatch(c -> c.equalsIgnoreCase(cleanCat));
        if (!valid) {
            throw new ValidationException("Invalid budget category: " + category);
        }
        this.category = cleanCat;
    }

    public double getAmountLimit() {
        return amountLimit;
    }

    public void setAmountLimit(double amountLimit) throws ValidationException {
        if (Double.isNaN(amountLimit) || Double.isInfinite(amountLimit) || amountLimit <= 0.0) {
            throw new ValidationException("Budget amount limit must be greater than 0.");
        }
        this.amountLimit = Math.round(amountLimit * 100.0) / 100.0;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) throws ValidationException {
        if (monthYear == null || !MONTH_PATTERN.matcher(monthYear.trim()).matches()) {
            throw new ValidationException("Budget month must be in YYYY-MM format (e.g. 2026-10).");
        }
        this.monthYear = monthYear.trim();
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("Budget[id=%d, category='%s', limit=₹%.2f, month=%s]",
            id, category, amountLimit, monthYear);
    }
}
