package com.expensetracker.model;

import java.sql.Timestamp;

/**
 * Represents a Smart Spending Alert generated when category spending hits 80% or exceeds 100%.
 * Demonstrates Encapsulation.
 */
public class Alert {
    private int id;
    private int userId;
    private String category;
    private String monthYear;
    private String alertType; // "WARNING" or "OVERSPENT"
    private double thresholdPercent;
    private double spentAmount;
    private double budgetLimit;
    private double differenceAmount; // positive if overspent, negative if under
    private String message;
    private boolean isRead;
    private Timestamp createdAt;

    public Alert() {
    }

    public Alert(int userId, String category, String monthYear, String alertType,
                 double thresholdPercent, double spentAmount, double budgetLimit,
                 double differenceAmount, String message) {
        this.userId = userId;
        this.category = category;
        this.monthYear = monthYear;
        this.alertType = alertType;
        this.thresholdPercent = Math.round(thresholdPercent * 100.0) / 100.0;
        this.spentAmount = Math.round(spentAmount * 100.0) / 100.0;
        this.budgetLimit = Math.round(budgetLimit * 100.0) / 100.0;
        this.differenceAmount = Math.round(differenceAmount * 100.0) / 100.0;
        this.message = message;
        this.isRead = false;
    }

    public Alert(int id, int userId, String category, String monthYear, String alertType,
                 double thresholdPercent, double spentAmount, double budgetLimit,
                 double differenceAmount, String message, boolean isRead, Timestamp createdAt) {
        this(userId, category, monthYear, alertType, thresholdPercent, spentAmount, budgetLimit, differenceAmount, message);
        this.id = id;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Getters and Setters

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

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public double getThresholdPercent() {
        return thresholdPercent;
    }

    public void setThresholdPercent(double thresholdPercent) {
        this.thresholdPercent = thresholdPercent;
    }

    public double getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(double spentAmount) {
        this.spentAmount = spentAmount;
    }

    public double getBudgetLimit() {
        return budgetLimit;
    }

    public void setBudgetLimit(double budgetLimit) {
        this.budgetLimit = budgetLimit;
    }

    public double getDifferenceAmount() {
        return differenceAmount;
    }

    public void setDifferenceAmount(double differenceAmount) {
        this.differenceAmount = differenceAmount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("Alert[%s on %s: %s (Spent: ₹%.2f / Limit: ₹%.2f)]",
            alertType, category, message, spentAmount, budgetLimit);
    }
}
